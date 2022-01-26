package be.ucll.javafxspringboot.controller;

import be.ucll.javafxspringboot.Models.Contact;
import be.ucll.javafxspringboot.Models.GroupchatList;
import be.ucll.javafxspringboot.Repository.FirebaseRepository;
import com.google.cloud.firestore.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Component
@FxmlView("Group.fxml")
public class GroupController {

    @Autowired
    FirebaseRepository db;
    @Autowired
    MainController mC;
    @Autowired
    ContactController cC;
    @FXML
    TextField groupName;
    @FXML
    Button create;
    @FXML
    ScrollPane scroll;
    @FXML
    Label fout;

    private ObservableList<GroupchatList> groups = FXCollections.observableList(new ArrayList<>());
    private ArrayList<Contact> groupContacts = new ArrayList<>();
    private ArrayList<Contact> tempList = new ArrayList<>();
    ObservableList<Contact> contacts = FXCollections.observableList(new ArrayList<>());
    VBox vBox = new VBox();

    public ObservableList<GroupchatList> getGroupList() throws ExecutionException, InterruptedException {
        CollectionReference gCL = db.getGroupChatList();
        gCL.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
                List<DocumentChange> documentChanges = (snapshots == null) ? new ArrayList<DocumentChange>() : snapshots.getDocumentChanges();
                for (DocumentChange doc : documentChanges) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        GroupchatList mes = doc.getDocument().toObject(GroupchatList.class);
                        if (mes.getUser1().equals(mC.getUser().getUid()) ||
                                mes.getUser2().equals(mC.getUser().getUid()) ||
                                mes.getUser3().equals(mC.getUser().getUid()) ||
                                mes.getUser4().equals(mC.getUser().getUid())) {
                            groups.add(mes);
                        }
                    }
                }
            }
        });
        return groups;
    }

    public void initialize() throws ExecutionException, InterruptedException {
        contacts.clear();
        contacts = cC.getAllContacts();
        for (Contact con : contacts) {
            CheckBox checkBox = new CheckBox(con.getName());
            checkBox.setId(con.getContactID());
            vBox.getChildren().add(checkBox);
        }
        scroll.setContent(vBox);
        create.setOnAction(createButton);
    }

    EventHandler<ActionEvent> createButton = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            tempList.clear();
            groupContacts.add(new Contact());
            groupContacts.add(new Contact());
            groupContacts.add(new Contact());
            boolean valid = false;
            for (int i = 0; i < vBox.getChildren().size(); i++) {
                CheckBox c = (CheckBox) vBox.getChildren().get(i);
                if (c.isSelected()) {
                    tempList.add(new Contact(c.getId(), mC.getUser().getUid(), c.getText()));
                }
            }
            if (tempList.size()<2){
                System.out.println("te weinig");
                fout.setText("You need more then 1 contact to create a groupchat");
            }else if (tempList.size()>3){
                System.out.println("te veel");
                fout.setText("You can only add 3 contacts to a groupchat");
            }
            if (tempList.size()>=2 && tempList.size()<=3){
                System.out.println("juiste hoeveelheid");
                for (int i=0;i<tempList.size();i++){
                    groupContacts.set(i,tempList.get(i));
                    fout.setText("");
                    valid = true;
                }
            }
            if (valid) {
                Random random = new Random();
                GroupchatList newGroup = new GroupchatList(mC.getUser().getUid()+ random.nextInt(10000), groupName.getText(),
                        mC.getUser().getDisplayName(), mC.getUser().getUid(),
                        groupContacts.get(0).getName(), groupContacts.get(0).getContactID(),
                        groupContacts.get(1).getName(), groupContacts.get(1).getContactID(),
                        groupContacts.get(2).getName(), groupContacts.get(2).getContactID());
                db.AddGroup(newGroup);
                Stage close = (Stage) create.getScene().getWindow();
                close.close();
            }
        }
    };
}

