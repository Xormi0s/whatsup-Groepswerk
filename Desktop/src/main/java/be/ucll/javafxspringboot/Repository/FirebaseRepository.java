package be.ucll.javafxspringboot.Repository;


import be.ucll.javafxspringboot.Models.Contact;
import be.ucll.javafxspringboot.Models.GroupMessages;
import be.ucll.javafxspringboot.Models.GroupchatList;
import be.ucll.javafxspringboot.Models.Message;
import com.google.api.core.ApiFuture;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Primary
@Repository
public class FirebaseRepository {

    private Firestore db;
    private FirebaseApp app;
    private CollectionReference messages;
    private CollectionReference groupMessages;
    private CollectionReference groupChatList;

    public CollectionReference getMes(String userId){
        messages.whereEqualTo("userID",userId);
        return messages;
    }

    public CollectionReference getGroupMes (){
        return groupMessages;
    }

    public CollectionReference getGroupChatList(){
        return groupChatList;
    }

    public FirebaseRepository() throws Exception {
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/google-services.json"));
        Firestore db = FirestoreOptions.newBuilder().setCredentials(credentials).setProjectId("whatsup-528ac").build().getService();
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/google-services.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            app = FirebaseApp.initializeApp(options);
        }
        this.db = db;
        messages = db.collection("messages");
        groupMessages = db.collection("GroupMessages");
        groupChatList = db.collection("GroupschatList");
    }

    public Firestore getDb() {
        return db;
    }

    FirebaseApp getApp(){
        return app;
    }

    public UserRecord Login(String phone) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().getUserByPhoneNumber(phone);
    }

   public ObservableList<Contact> getContacts(String uId) throws FirebaseAuthException {
       ApiFuture<QuerySnapshot> q = db.collection("Contactlists").whereEqualTo("userID",uId).get();
       List<QueryDocumentSnapshot> doc = null;
       try {
           doc = q.get().getDocuments();
       } catch (Exception e) {
           e.printStackTrace();
       }
       ObservableList<Contact> cL = FXCollections.observableList(new ArrayList<>());
       for (QueryDocumentSnapshot d: doc){
           cL.add(d.toObject(Contact.class));
       }
       return cL;
   }

   public UserRecord getUser(String uId) throws FirebaseAuthException {
       return FirebaseAuth.getInstance().getUser(uId);
   }

   public ObservableList<GroupchatList> getGroups(String uId) throws ExecutionException, InterruptedException {
       ApiFuture<QuerySnapshot> q = db.collection("GroupschatList").get();
       List<QueryDocumentSnapshot> doc = q.get().getDocuments();
       ObservableList<GroupchatList> gL = FXCollections.observableList(new ArrayList<>());
       for (QueryDocumentSnapshot d : doc){
           GroupchatList group = new GroupchatList(d.getString("id"),d.getString("name"),
                   d.getString("name1"),d.getString("user1"),
                   d.getString("name2"),d.getString("user2"),
                   d.getString("name3"),d.getString("user3"),
                   d.getString("name4"),d.getString("user4"));
           if (group.getUser1().equals(uId)||group.getUser2().equals(uId)||group.getUser3().equals(uId)||group.getUser4().equals(uId)){
               gL.add(group);
           }
       }
       return gL;
   }

    public void sendMessage(Message mes, GroupMessages gMes){
        if (mes!=null){
            messages.add(mes);
        }else if (gMes!=null){
            groupMessages.add(gMes);
        }
   }

   public void AddGroup (GroupchatList group){
        groupChatList.add(group);
   }

}


