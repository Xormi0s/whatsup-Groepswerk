package be.ucll.javafxspringboot.controller;

import be.ucll.javafxspringboot.Repository.FirebaseRepository;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("Login.fxml")
public class LoginController {

    @Autowired
    private FirebaseRepository firebaseRepository;

    @Autowired
    private MainController mC;

    @Autowired
    private FxWeaver fxWeaver;

    private UserRecord usR;

    @FXML
    Button login;
    @FXML
    TextField phone;
    @FXML
    Label fout;

    public void loginToWhatsApp(ActionEvent actionEvent) throws FirebaseAuthException {
        try{
            usR = firebaseRepository.Login(phone.getText());
            fout.setText("");
        }catch (Exception e){
            System.out.println("no account found");
            fout.setText("No account found !!");
        }

        if (usR != (null)) {
            Stage close = (Stage) login.getScene().getWindow();
            close.close();
            mC.setUser(usR);
            Parent root = fxWeaver.loadView(MainController.class);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("WhatsUp!");
            stage.show();
        }
    }
}
