package be.ucll.javafxspringboot.controller;

import be.ucll.javafxspringboot.Models.Contact;
import be.ucll.javafxspringboot.Repository.FirebaseRepository;
import com.google.firebase.auth.FirebaseAuthException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@FxmlView("Contact.fxml")
public class ContactController {

    @Autowired
    FirebaseRepository db;
    @Autowired
    MainController mC;

    @FXML
    ScrollPane list;
    @FXML
    Button update;

    VBox vBox = new VBox();

    private ObservableList<Contact> contactList = FXCollections.observableList(new ArrayList<>());
    private ObservableList<Contact> contacts;

    public void initialize() throws FirebaseAuthException {
        vBox.getChildren().clear();
        for (Contact con : contacts){
            CheckBox cB = new CheckBox(con.getName());
            if (con.getSelected()){
                cB.setSelected(true);
                cB.setId(con.getContactID());
            }
            vBox.getChildren().add(cB);
            vBox.setSpacing(10);

        }
        list.setContent(vBox);
        update.setOnAction(updateB);
    }

    EventHandler<ActionEvent> updateB = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            contactList.clear();
            for(int i = 0; i< vBox.getChildren().size();i++){
                CheckBox c = (CheckBox) vBox.getChildren().get(i);
                if(c.isSelected()){
                    contactList.add(new Contact(c.getId(),mC.getUser().getUid(),c.getText()));
                }
            }
            Stage close = (Stage) update.getScene().getWindow();
            close.close();
        }
    };


    public ObservableList<Contact> getContactList() throws FirebaseAuthException {
        contacts = db.getContacts(mC.getUser().getUid());
        for (Contact con : contacts){
            if (con.getSelected()){
                contactList.add(con);
            }
        }
        return contactList;
    }

    public void setContactList(ObservableList<Contact> contactList) {
        this.contactList = contactList;
    }

    public void updateContactList(){

    }

    public ObservableList<Contact> getAllContacts(){
        return contacts;
    }
}
