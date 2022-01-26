package be.ucll.javafxspringboot.controller;

import be.ucll.javafxspringboot.Models.Contact;
import be.ucll.javafxspringboot.Models.GroupMessages;
import be.ucll.javafxspringboot.Models.GroupchatList;
import be.ucll.javafxspringboot.Models.Message;
import be.ucll.javafxspringboot.Repository.FirebaseRepository;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.swing.text.StyledEditorKit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@FxmlView("Main.fxml")
public class MainController {

    private UserRecord user;
    @Autowired
    private FxWeaver fxWeaver;
    @Autowired
    FirebaseRepository dbService;
    @Autowired
    ContactController cC;
    @Autowired
    GroupController gC;

    private ObservableList<Contact> contactsList = FXCollections.observableList(new ArrayList<>());
    private ObservableList<GroupchatList> groupList = FXCollections.observableList(new ArrayList<>());
    private ObservableList<Message> messageObservableList = FXCollections.observableList(new ArrayList<>());
    private ObservableList<GroupMessages> groupMessageObservableList = FXCollections.observableList(new ArrayList<>());
    private String contactId;
    private String groupId;
    private CollectionReference messages;
    private CollectionReference groupMessages;
    ArrayList<Message> messageLijst = new ArrayList<>();
    ArrayList<GroupMessages> groupMessageLijst = new ArrayList<>();

    @FXML
    MenuBar menubar;
    @FXML
    MenuItem account;
    @FXML
    MenuItem contacts;
    @FXML
    MenuItem groups;
    @FXML
    MenuItem logout;
    @FXML
    Label userGreet;
    @FXML
    Button sendButton;
    @FXML
    TextArea inputText;
    @FXML
    SplitPane splitView;
    @FXML
    Button sendImage;
    @FXML
    Label contactOrGroup;

    VBox vBoxC = new VBox();
    VBox chatView = new VBox();
    ScrollPane scrollChat = new ScrollPane();
    ScrollPane scrollContact = new ScrollPane();

    @FXML
    public void initialize() throws Exception {
        buttonControll(true);
        contactsList.clear();
        groupList.clear();
        scrollChat.setContent(chatView);
        scrollChat.setFitToWidth(true);
        scrollChat.setVvalue(1.0);
        scrollContact.setContent(vBoxC);
        scrollContact.setFitToWidth(true);
        splitView.getItems().addAll(scrollContact,scrollChat);
        splitView.setDividerPosition(0,0.2f);
        sendImage.setOnAction(imageButtonHandler);
        contactsList = cC.getContactList();
        groupList = gC.getGroupList();
        setContactView(contactsList);
        setGroupChatView(groupList);
        groupListListener();
        contactListListener();
        vBoxC.setSpacing(10);
        messages = dbService.getMes(user.getUid());
        messageListener();
        groupMessages = dbService.getGroupMes();
        groupMessageListener();
    }

    private void groupMessageListener() {
        groupMessages.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
                List<DocumentChange> documentChanges = (snapshots == null) ? new ArrayList<>() : snapshots.getDocumentChanges();
                for (DocumentChange doc : documentChanges) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        GroupMessages mes =// new GroupMessages(doc.getDocument().getString("groupID"),null,doc.getDocument().getString("message"),doc.getDocument().getString("senderID"));
                                    doc.getDocument().toObject(GroupMessages.class);
                        groupMessageObservableList.add(mes);
                    }
                }
                groupMessageObservableList.sort(Comparator.comparing(GroupMessages::getTimestamp));
                groupMessageObservableList.addListener(new ListChangeListener<GroupMessages>() {
                    @Override
                    public void onChanged(Change<? extends GroupMessages> change) {
                        if (change.next()) {
                            Platform.runLater(() -> {
                                try {
                                    updateGroupChatView(groupMessageObservableList);
                                } catch (FirebaseAuthException firebaseAuthException) {
                                    firebaseAuthException.printStackTrace();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void messageListener() {
        messages.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
                List<DocumentChange> documentChanges = (snapshots == null) ? new ArrayList<DocumentChange>() : snapshots.getDocumentChanges();
                for (DocumentChange doc : documentChanges) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        Message mes = doc.getDocument().toObject(Message.class);
                        if (mes.getUserID().equals(user.getUid()) || mes.getContactID().equals(user.getUid())) {
                            messageObservableList.add(mes);
                        }
                    }
                }
                messageObservableList.sort(Comparator.comparing(Message::getTimestamp));
                messageObservableList.addListener(new ListChangeListener<Message>() {
                    @Override
                    public void onChanged(Change<? extends Message> change) {
                        if (change.next()){
                            Platform.runLater(()->{
                                updateChatView(messageObservableList);
                            });
                        }
                    }
                });
            }
        });
    }

    private void contactListListener() {
        contactsList.addListener(new ListChangeListener<Contact>() {
            @Override
            public void onChanged(Change<? extends Contact> change) {
                if (change.next()) {
                    Platform.runLater(() -> {
                        vBoxC.getChildren().clear();
                        setContactView(contactsList);
                        setGroupChatView(groupList);
                    });
                }
            }
        });
    }

    private void groupListListener() {
        groupList.addListener(new ListChangeListener<GroupchatList>() {
            @Override
            public void onChanged(Change<? extends GroupchatList> change) {
                if (change.next()) {
                    Platform.runLater(() -> {
                        vBoxC.getChildren().clear();
                        setContactView(contactsList);
                        setGroupChatView(groupList);
                    });
                }
            }
        });
    }

    public void logout(ActionEvent actionEvent) {
        vBoxC.getChildren().clear();
        chatView.getChildren().clear();
        messageObservableList.clear();
        groupMessageObservableList.clear();
        Stage close = (Stage) menubar.getScene().getWindow();
        close.close();
        Parent root = fxWeaver.loadView(LoginController.class);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        //terug naar login screen
    }

    public  void setUser(UserRecord user){
        this.user = user;
    }

    public UserRecord getUser(){
        return user;
    }

    public void setContactView(ObservableList<Contact> list){
        for (Contact us : list) {
            Button but = new Button();
            but.setText(us.getName());
            but.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #8feb9e;" +
                    "-fx-background-color: #c4f5cc;"+ "-fx-background-radius: 5;"+"-fx-background-insets: 5;");
            but.setId(us.getContactID());
            but.setOnAction(chatButtonHandler);
            vBoxC.getChildren().add(but);
            vBoxC.setAlignment(Pos.TOP_CENTER);
        }
    }

    public void setGroupChatView(ObservableList<GroupchatList> groupList){
        Label label = new Label();
        label.setText("Groups: ");
        label.setStyle("-fx-label-padding: 10;");
        vBoxC.getChildren().add(label);
        for (GroupchatList gL : groupList){
            Button but = new Button();
            but.setText(gL.getName());
            but.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                    + "-fx-border-radius: 5;" + "-fx-border-color: #6fa6ed;"+
                    "-fx-background-color: #bed8fa;"+ "-fx-background-radius: 5;"+"-fx-background-insets: 5;");
            but.setId(gL.getId());
            but.setOnAction(groupButtonHandler);
            vBoxC.getChildren().add(but);
            vBoxC.setAlignment(Pos.TOP_CENTER);
        }
    }

    private void updateChatView(ObservableList<Message> oL) {
        chatView.getChildren().clear();
        messageLijst.clear();
        for (Message mes : messageObservableList) {
            if (mes.getContactID().equals(user.getUid()) && mes.getUserID().equals(contactId)){
                messageLijst.add(mes);
            } else if (mes.getContactID().equals(contactId) && mes.getUserID().equals(user.getUid())) {
                messageLijst.add(mes);
            }
        }
        for (Message mes : messageLijst) {
            if (mes.getUserID().equals(user.getUid())) {
                ImageView imageView = new ImageView();
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                if (mes.getImage()!=null){
                    Blob blob = mes.getImage();
                    InputStream i = blob.toByteString().newInput();
                    Image ima = new Image(i);
                    imageView.setImage(ima);
                    hBox.getChildren().add(imageView);
                    chatView.getChildren().add(hBox);
                }else{
                    Label label = new Label();
                    label.setText(mes.getMessage());
                    label.setFont(Font.font(14.0));
                    label.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                            + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                            + "-fx-border-radius: 5;" + "-fx-border-color: #6fa6ed;"+
                            "-fx-background-color: #bed8fa;"+ "-fx-background-radius: 5;"+"-fx-background-insets: 5;");
                    hBox.getChildren().add(label);
                    chatView.getChildren().add(hBox);
                }
            } else {
                ImageView imageView = new ImageView();
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_LEFT);
                if (mes.getImage()!=null) {
                    Blob blob = mes.getImage();
                    InputStream i = blob.toByteString().newInput();
                    Image ima = new Image(i);
                    imageView.setImage(ima);
                    hBox.getChildren().add(imageView);
                    chatView.getChildren().add(hBox);
                }else{
                    Label label = new Label();
                    label.setText(mes.getMessage());
                    label.setFont(Font.font(14.0));
                    label.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                            + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                            + "-fx-border-radius: 5;" + "-fx-border-color: #8feb9e;" +
                            "-fx-background-color: #c4f5cc;"+ "-fx-background-radius: 5;"+"-fx-background-insets: 5;");
                    hBox.getChildren().add(label);
                    chatView.getChildren().add(hBox);
                }
            }
        }
    }

    private void updateGroupChatView(ObservableList<GroupMessages> oG) throws FirebaseAuthException {
        chatView.getChildren().clear();
        groupMessageLijst.clear();
        for (GroupMessages mes : groupMessageObservableList) {
            if (mes.getGroupID().equals(groupId)) {
                groupMessageLijst.add(mes);
            }
        }
        for (GroupMessages mes : groupMessageLijst) {
            if (mes.getSenderID().equals(user.getUid())) {
                ImageView imageView = new ImageView();
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                if (mes.getImage()!=null){
                    Blob blob = mes.getImage();
                    InputStream i = blob.toByteString().newInput();
                    Image ima = new Image(i);
                    imageView.setImage(ima);
                    hBox.getChildren().add(imageView);
                    chatView.getChildren().add(hBox);
                }else{
                    Label label = new Label();
                    label.setText(mes.getMessage());
                    label.setFont(Font.font(14.0));
                    label.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                            + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                            + "-fx-border-radius: 5;" + "-fx-border-color: #6fa6ed;"+
                            "-fx-background-color: #bed8fa;"+ "-fx-background-radius: 5;"+"-fx-background-insets: 5;");
                    hBox.getChildren().add(label);
                    chatView.getChildren().add(hBox);
                }
            } else {
                ImageView imageView = new ImageView();
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_LEFT);
                if (mes.getImage()!=null) {
                    Blob blob = mes.getImage();
                    InputStream i = blob.toByteString().newInput();
                    Image ima = new Image(i);
                    imageView.setImage(ima);
                    Label sender = new Label();
                    sender.setText(dbService.getUser(mes.getSenderID()).getDisplayName()+": ");
                    sender.setFont(Font.font("verdana",FontPosture.ITALIC,10));
                    hBox.getChildren().add(sender);
                    hBox.getChildren().add(imageView);
                    chatView.getChildren().add(hBox);
                }else{
                    Label label = new Label();
                    Label sender = new Label();
                    sender.setText(dbService.getUser(mes.getSenderID()).getDisplayName()+": ");
                    sender.setFont(Font.font("verdana",FontPosture.ITALIC,10));
                    label.setText(mes.getMessage());
                    label.setFont(Font.font(14.0));
                    label.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                            + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                            + "-fx-border-radius: 5;" + "-fx-border-color: #8feb9e;" +
                            "-fx-background-color: #c4f5cc;"+ "-fx-background-radius: 5;"+"-fx-background-insets: 5;");
                    hBox.getChildren().add(sender);
                    hBox.getChildren().add(label);
                    chatView.getChildren().add(hBox);
                }
            }
        }
    }

    private void clearIds(){
        contactId = null;
        groupId = null;
    }

    private void buttonControll(Boolean a){
        sendButton.setDisable(a);
        sendImage.setDisable(a);
    }

    private  String getGroupId(Button button){
        groupId = button.getId();
        return groupId;
    }

    private  String getGroupName(Button button){
        String groupName = button.getText();
        return groupName;
    }

    private String getButtonId(Button button){
        contactId  = button.getId();
        return contactId;
    }

    private String getButtonName(Button button){
        String contactName = button.getText();
        return contactName;
    }

    public void sendImage(File file) throws IOException {
        if (contactId != null) {
            byte[] fileContent = FileUtils.readFileToByteArray(new File(String.valueOf(file)));
            Blob blob = Blob.fromBytes(fileContent);
            Message mes = new Message(user.getUid(),contactId,null, blob );
            if (file!=null){
                dbService.sendMessage(mes,null);
            }
        }else if (groupId!=null){
            byte[] fileContent = FileUtils.readFileToByteArray(new File(String.valueOf(file)));
            Blob blob = Blob.fromBytes(fileContent);
            GroupMessages gMes = new GroupMessages(groupId,blob,null, user.getUid());
            if (file!=null){
                dbService.sendMessage(null,gMes);
            }
        }
    }

    public void sendMessage(ActionEvent event) throws Exception {
        if (!inputText.getText().isEmpty() && contactId!=null){
            Message mes = new Message(user.getUid(),contactId,inputText.getText(),null);
            dbService.sendMessage(mes,null);
            inputText.clear();
        }else if (!inputText.getText().isEmpty() && groupId!=null){
            GroupMessages gMes = new GroupMessages(groupId,null,inputText.getText(), user.getUid());
            dbService.sendMessage(null,gMes);
            inputText.clear();
        }
        else{
            System.out.println("No message");
        }
    }

    EventHandler<ActionEvent> chatButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            clearIds();
            buttonControll(false);
            scrollChat.setVvalue(1.0);
            getButtonId((Button) actionEvent.getSource());
            contactOrGroup.setText(getButtonName((Button) actionEvent.getSource()));
            updateChatView(messageObservableList);
        }
    };

    EventHandler<ActionEvent> groupButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            buttonControll(false);
            clearIds();
            scrollChat.setVvalue(1.0);
            getGroupId((Button) actionEvent.getSource());
            contactOrGroup.setText(getGroupName((Button) actionEvent.getSource()));
            try {
                updateGroupChatView(groupMessageObservableList);
            } catch (FirebaseAuthException e) {
                e.printStackTrace();
            }
        }
    };

    EventHandler<ActionEvent> imageButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            FileChooser dC = new FileChooser();
            Window window = sendImage.getScene().getWindow();
            File selectedFile = dC.showOpenDialog(window);
            if (selectedFile != null){
                try {
                    sendImage(selectedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void openContacts(ActionEvent actionEvent) {
        Parent root = fxWeaver.loadView(ContactController.class);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Contacts");
        stage.show();
    }

    public void createGroup(ActionEvent actionEvent){
        Parent root = fxWeaver.loadView(GroupController.class);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Create a group");
        stage.show();
    }
}

