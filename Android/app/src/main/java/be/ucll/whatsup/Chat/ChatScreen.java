package be.ucll.whatsup.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.ucll.whatsup.Contacts.ContactList;
import be.ucll.whatsup.Login.LoginScreen;
import be.ucll.whatsup.Login.User;
import be.ucll.whatsup.R;
import be.ucll.whatsup.SettingsMenu;

public class ChatScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private FirebaseFirestore db;
    private CollectionReference messages;
    private CollectionReference users;
    private List<Message> chatMessageList = new ArrayList<>();
    private ActivityResultLauncher<Uri> takePicture;
    private Uri uri;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String contactUser;
    private String phonenumber;
    private String name;
    private User userTest;
    private RecyclerView recyclerView;
    private ChatMessageListAdapter chatMessageListAdapter;
    private Boolean switchImgCam = false;
    private Boolean emptyStart = false;

    private ActivityResultLauncher<String[]> contentPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        if(switchImgCam){
            sendCam();
        } else {
            sendImage();
        }
    });

    private ActivityResultLauncher<String> selectContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        try {
            if (result != null) {
                InputStream inputStream = getContentResolver().openInputStream(result);
                Message message = new Message(currentUser.getUid(), contactUser ,null, Blob.fromBytes(IOUtils.toByteArray(inputStream)));
                messages.add(message);
            } else {
                Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_LONG).show();
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    });

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;

        switch (item.getItemId()){
            case R.id.nav_settings:
                intent = new Intent(this, SettingsMenu.class);
                startActivity(intent);
                break;
            case R.id.nav_contacts:
                intent = new Intent(this, ContactList.class);
                intent.putExtra("uid", mAuth.getUid());
                startActivity(intent);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        Intent intent = getIntent();
        FirebaseApp.initializeApp(this);

        drawerLayout = findViewById(R.id.nav_view);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        phonenumber = intent.getStringExtra("number");
        phonenumber = phonenumber.replaceAll("\\s", "");
        name = intent.getStringExtra("name");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView2);
        chatMessageListAdapter = new ChatMessageListAdapter(chatMessageList, this);
        chatMessageListAdapter.setHasStableIds(true);
        recyclerView.setAdapter(chatMessageListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        messages = db.collection("messages");
        users = db.collection("Users");

        String user = currentUser.getUid();

        TextView textView = findViewById(R.id.contactName);
        textView.setText(name);

        users.whereEqualTo("number", phonenumber).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userTest = document.toObject(User.class);
                        if (userTest.getNumber().equals(phonenumber)) {
                            contactUser = userTest.getUid();
                            loadMsgs(user, contactUser);
                        }
                    }
                }
            }
        });


        Button sendImage = findViewById(R.id.sentimg);
        Button sendMessage = findViewById(R.id.sentmsg);
        Button sendCam = findViewById(R.id.sentcam);

        sendImage.setOnClickListener(v -> sendImage());
        sendMessage.setOnClickListener(v -> sendMessage());
        sendCam.setOnClickListener(v -> {
            switchImgCam = true;
            sendCam();
        });

        File file = new File(getFilesDir(), "images");
        uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);

        takePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            try {
                if (result != null && result == true) {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Message message = new Message(currentUser.getUid(), contactUser ,null, Blob.fromBytes(IOUtils.toByteArray(inputStream)));
                    messages.add(message);
                } else {
                    Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_LONG).show();
                }
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        });
    }

    public void sendImage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectContent.launch("image/*");
        } else {
            contentPermissions.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
        }
    }

    public void sendCam(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePicture.launch(uri);
        } else {
            contentPermissions.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
        }
    }

    public void sendMessage() {
        EditText editText = findViewById(R.id.textmsg);
        Message message = new Message(currentUser.getUid(), contactUser ,editText.getText().toString(), null);
        messages.add(message);
        editText.setText("");
    }

    public void loadMsgs(String user, String contactUser){
        messages.whereIn("userID", Arrays.asList(user, contactUser)).orderBy("timestamp").addSnapshotListener((value, error) -> value.getDocumentChanges().forEach(d -> {
            Message output = d.getDocument().toObject(Message.class);
            if(output.getContactID() != null && output.getContactID().equals(contactUser)){
                chatMessageList.add(output);
                chatMessageListAdapter.notifyItemInserted(chatMessageList.size() - 1);
                recyclerView.smoothScrollToPosition(chatMessageList.size() - 1);
            }
            if(output.getContactID() != null && output.getContactID().equals(user)){
                chatMessageList.add(output);
                chatMessageListAdapter.notifyItemInserted(chatMessageList.size() - 1);
                recyclerView.smoothScrollToPosition(chatMessageList.size() - 1);
            }
        }));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ContactList.class);
        intent.putExtra("uid", mAuth.getUid());
        finish();
        startActivity(intent);
    }
}