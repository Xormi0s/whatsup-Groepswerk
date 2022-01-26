package be.ucll.whatsup.Contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import be.ucll.whatsup.Chat.MakeGroupChat;
import be.ucll.whatsup.Login.LoginScreen;
import be.ucll.whatsup.Login.User;
import be.ucll.whatsup.R;
import be.ucll.whatsup.SettingsMenu;

public class ContactList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerViewContact;
    private RecyclerView recyclerViewGroup;
    private ArrayList<ContactModel> contactModels = new ArrayList<ContactModel>();
    private ContactListAdapter contactListAdapter;
    private DrawerLayout drawerLayout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference contactlist;
    private CollectionReference userslist;
    private String uid;
    private GroupListAdapter GroupListAdapter;
    private CollectionReference group;
    private ArrayList<GroupContact> groupContacts = new ArrayList<GroupContact>();
    private ArrayList<GroupContact> tempGroupContacts = new ArrayList<GroupContact>();
    private ArrayList<contactListModel> tempContactlist = new ArrayList<contactListModel>();
    private ArrayList<User> usersdb = new ArrayList<User>();
    private TextView textView;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        recyclerViewContact = findViewById(R.id.list);
        recyclerViewGroup = findViewById(R.id.list2);

        drawerLayout = findViewById(R.id.nav_view);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        String name = intent.getStringExtra("name");
        User user = new User(intent.getStringExtra("uid"), intent.getStringExtra("number"), name);

        user.checkDB();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        group = db.collection("GroupschatList");
        contactlist = db.collection("Contactlists");
        userslist = db.collection("Users");

        textView = findViewById(R.id.textView4);

        if(name != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name).build();
            currentUser.updateProfile(profileUpdates);
        }

        textView.setText("Contact list");


        setGroupContacts();
        getUsersDB();

        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(this));

        GroupListAdapter = new GroupListAdapter(this, groupContacts, uid);

        recyclerViewGroup.setAdapter(GroupListAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;

        switch (item.getItemId()){
            case R.id.nav_settings:
                intent = new Intent(this, SettingsMenu.class);
                startActivity(intent);
                break;
            case R.id.nav_contacts:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_group:
                intent = new Intent(this, MakeGroupChat.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(this, LoginScreen.class);
                startActivity(intent);
                mAuth.signOut();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            getContactList();
        } else {
            ActivityCompat.requestPermissions(ContactList.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }
    }

    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor = getContentResolver().query(uri, null,null,null, sort);

        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";

                Cursor phoneCursor = getContentResolver().query(uriPhone,null, selection, new String[]{id}, null);

                if (phoneCursor.moveToNext()){
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    ContactModel model = new ContactModel(name, number);
                    String tempNumber = model.getNumber();
                    tempNumber = tempNumber.replaceAll("\\s", "");
                    for(User contact: usersdb){
                        if(contact.getNumber().equals(tempNumber)){
                            contactModels.add(model);
                        }
                    }
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        recyclerViewContact.setLayoutManager(new LinearLayoutManager(this));

        contactListAdapter = new ContactListAdapter(this, contactModels, uid);

        recyclerViewContact.setAdapter(contactListAdapter);

        getContactListDB();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getContactList();
        } else {
            Toast.makeText(ContactList.this, "Permission Denied!", Toast.LENGTH_SHORT).show();

            checkPermission();
        }
    }

    public void showGroup(View view){
        textView.setText("Group list");
        recyclerViewContact.setVisibility(View.INVISIBLE);
        recyclerViewGroup.setVisibility(View.VISIBLE);
    }

    public void showContact(View view){
        if(groupContacts == null){
            setGroupContacts();
        }
        textView.setText("Contact list");
        recyclerViewContact.setVisibility(View.VISIBLE);
        recyclerViewGroup.setVisibility(View.INVISIBLE);
    }

    public void setGroupContacts(){
        group.addSnapshotListener((value, error) -> value.getDocumentChanges().forEach(d -> {
            GroupContact output = d.getDocument().toObject(GroupContact.class);
            tempGroupContacts.add(output);
            finalGroupContacts();
        }));


        /*group.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()) {
                      for (QueryDocumentSnapshot document : task.getResult()) {
                          GroupContact output = document.toObject(GroupContact.class);
                          tempGroupContacts.add(output);
                      }
                  }
                  finalGroupContacts();
              }
          }
        );*/
    }

    public void finalGroupContacts(){
        if(tempGroupContacts != null){
            for (GroupContact contact: tempGroupContacts){
                if(contact.getUser4() != null){
                    if(contact.getUser1().equals(uid) || contact.getUser2().equals(uid) || contact.getUser3().equals(uid) || contact.getUser4().equals(uid)){
                        groupContacts.add(contact);
                    }
                } else {
                    if(contact.getUser1().equals(uid) || contact.getUser2().equals(uid) || contact.getUser3().equals(uid)){
                        groupContacts.add(contact);
                    }
                }
            }

            List<GroupContact> set = groupContacts.stream().distinct().collect(Collectors.toList());
            groupContacts.clear();
            groupContacts.addAll(set);

            if(groupContacts.size() >= 1){
                GroupListAdapter.notifyItemInserted(groupContacts.size() - 1);
                recyclerViewGroup.smoothScrollToPosition(groupContacts.size() - 1);
            }
        }
    }

    public void getContactListDB(){
        contactlist.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()) {
                      for (QueryDocumentSnapshot document : task.getResult()) {
                          contactListModel output = document.toObject(contactListModel.class);
                          tempContactlist.add(output);
                      }
                  }
                  makeContactListDB();
              }
          }
        );
    }

    public void getUsersDB(){
        userslist.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User output = document.toObject(User.class);
                            usersdb.add(output);
                        }
                    }
                    checkPermission();
                }
            }
        );
    }

    public void makeContactListDB(){
        String contactid = "";
        Boolean check = false;
        ArrayList<contactListModel> temp = new ArrayList<contactListModel>();
        ArrayList<contactListModel> checktemp = new ArrayList<contactListModel>();
        ArrayList<contactListModel> output = new ArrayList<contactListModel>();

        if(tempContactlist.size() == 0){
            for(User user: usersdb){
                for(ContactModel contactmodel: contactModels){
                    String tempNumber = contactmodel.getNumber();
                    tempNumber = tempNumber.replaceAll("\\s", "");
                    if(user.getNumber().equals(tempNumber)){
                        contactid = user.getUid();
                        if(!contactid.equals(uid)){
                            contactlist.add(new contactListModel(contactid, uid, contactmodel.getName()));
                        }
                    }
                }
            }
        } else {
            for(User user: usersdb){
                for(ContactModel contactmodel: contactModels){
                    String tempNumber = contactmodel.getNumber();
                    tempNumber = tempNumber.replaceAll("\\s", "");
                    if(user.getNumber().equals(tempNumber)){
                        contactid = user.getUid();
                        if(!contactid.equals(uid)){
                            temp.add(new contactListModel(contactid, uid, contactmodel.getName()));
                        }
                    }
                }
            }
        }
        for (contactListModel contacts : temp){
            for(contactListModel contactList : tempContactlist){
                if(contacts.getUserID().equals(contactList.getUserID()) && contacts.getContactID().equals(contactList.getContactID())){
                    check = true;
                } else {
                    checktemp.add(new contactListModel(contacts.getContactID(), contacts.getUserID(), contacts.getName()));
                }
            }
            if(check){
                checktemp = new ArrayList<contactListModel>();
            } else {
                for(contactListModel checkout: checktemp){
                    output.add(checkout);
                }
            }
            check = false;
        }

        List<contactListModel> set = output.stream().distinct().collect(Collectors.toList());

        if(set.size() > 0){
            for(contactListModel contacts : set){
                contactlist.add(contacts);
            }
        }
    }
}