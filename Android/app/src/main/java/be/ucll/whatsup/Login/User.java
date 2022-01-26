package be.ucll.whatsup.Login;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import be.ucll.whatsup.Chat.Message;

public class User {

    private String uid;
    private String number;
    private String name;
    private String email;
    private String pass;

    private CollectionReference users;
    private FirebaseFirestore db;
    private List<User> userList = new ArrayList<>();

    public User() {

    }

    public User(String uid, String number) {
        this.uid = uid;
        this.number = number;
    }

    public User(String uid, String number, String name) {
        this.uid = uid;
        this.number = number;
        this.name = name;
    }

    public void checkDB(){
        db = FirebaseFirestore.getInstance();
        users = db.collection("Users");

        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User output = document.toObject(User.class);
                        userList.add(output);
                        }
                    }
                addUser();
                }
            }
        );
    }

    public void addUser(){
        boolean add = true;
        if(userList != null){
            for(User user: userList){
                if(user.getNumber().equals(this.number)){
                    add = false;
                }
                if(user.getUid() == null){
                    add = false;
                }
            }

            if(add == true && this.uid != null && this.number != null){
                users.add(new User(uid, number, name));
            }
        }
    }

    public String getUid() {
        return uid;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}
