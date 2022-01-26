package be.ucll.whatsup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.util.FirebaseAuthError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;

public class Register_email extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private EditText passId, emailId;
    private Button registerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register_email);
        registerId = findViewById(R.id.registerId);
        passId = findViewById(R.id.passId);
        emailId = findViewById(R.id.emailId);

        registerId.setOnClickListener(v -> {
            String emailString = emailId.getText().toString();
            String passString = passId.getText().toString();
            if (emailString.isEmpty() || passString.isEmpty()) {
                Toast.makeText(Register_email.this, "Cannot be empty!", Toast.LENGTH_SHORT).show();
            } else {
                sendEmailVerification();
                createAccount(emailString, passString);
                //AuthCredential credential = PhoneAuthProvider.getCredential(emailString, passString);
                //                mAuth.getCurrentUser().linkWithCredential(credential)
                //                        .addOnCompleteListener(this, task -> {
                //                            if (task.isSuccessful()){
                //                                Log.d(TAG, "LinkWithCredential:success");
                //                                FirebaseUser user = task.getResult().getUser();
                //                                updateUI(user);
                //                            } else {
                //                                Log.w(TAG, "linkWithCredential:failure", task.getException());
                //                                Toast.makeText(Register_email.this, "Authentication failed.",
                //                                        Toast.LENGTH_SHORT).show();
                //                                updateUI(null);
                //                            }
                //                        });
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }
    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(Register_email.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
        // [END create_user_with_email]
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> Toast.makeText(Register_email.this, "Email send!", Toast.LENGTH_SHORT).show());
        // [END send_email_verification]
    }



    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }
}