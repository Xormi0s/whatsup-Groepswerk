package be.ucll.whatsup.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

import be.ucll.whatsup.Contacts.ContactList;
import be.ucll.whatsup.Contacts.ContactListAdapter;
import be.ucll.whatsup.Contacts.ContactModel;
import be.ucll.whatsup.Contacts.GroupContact;
import be.ucll.whatsup.Contacts.contactListModel;
import be.ucll.whatsup.Login.User;
import be.ucll.whatsup.R;

public class MakeGroupChat extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference contactlist;
    private CollectionReference groupschatlist;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private MakeGroupAdaptor makeGroupAdaptor;
    private ArrayList<contactListModel> contactModels = new ArrayList<contactListModel>();
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_group_chat);
        FirebaseApp.initializeApp(this);

        recyclerView = findViewById(R.id.recyclerView4);

        db = FirebaseFirestore.getInstance();
        contactlist = db.collection("Contactlists");
        groupschatlist = db.collection("GroupschatList");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        getContactlistDB();

        Button button = findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeGroup();
            }
        });
    }

    public void getContactlistDB() {
        Activity activity = this;
        String userid = currentUser.getUid();

        contactlist.whereEqualTo("userID", userid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        contactListModel output = document.toObject(contactListModel.class);
                        contactModels.add(output);
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));

                makeGroupAdaptor = new MakeGroupAdaptor(activity, contactModels);

                recyclerView.setAdapter(makeGroupAdaptor);
            }
        });
    }

    public void makeGroup(){
        ArrayList<contactListModel> temp = makeGroupAdaptor.getCheckboxlist();
        Random random = new Random();
        EditText editText = findViewById(R.id.textView6);
        if(temp.size() < 2){
            Toast.makeText(this, "You need a minimum of 2 contacts for a groupchat", Toast.LENGTH_SHORT).show();
        }

        if(temp.size() > 3){
            Toast.makeText(this, "Groupschat has a limit of 4 contacts incl. yourself. You can only select 3 contacts !", Toast.LENGTH_SHORT).show();
        }

        if(temp.size() >= 2 && temp.size() <=3){
            String name1 = currentUser.getDisplayName();
            String name2 = temp.get(0).getName();
            String name3 = temp.get(1).getName();
            String name4 = null;
            if(temp.size() == 3){
                name4 = temp.get(2).getName();
            }
            String user1 = currentUser.getUid();
            String user2 = temp.get(0).getContactID();
            String user3 = temp.get(1).getContactID();
            String user4 = null;
            if(temp.size() >=3){
                user4 = temp.get(2).getContactID();
            }

            GroupContact groupContact = new GroupContact(currentUser.getUid() + Integer.toString(random.nextInt(1000)), editText.getText().toString(), name1, name2, name3, name4, user1, user2, user3, user4);

            groupschatlist.add(groupContact);

            Intent intent = new Intent(this,ContactList.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        }
    }
}