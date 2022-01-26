package be.ucll.whatsup.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

import be.ucll.whatsup.Contacts.ContactList;
import be.ucll.whatsup.R;

public class LoginScreen extends AppCompatActivity {

    private EditText phonenumberId, codeId, nameId;
    private Button loginButtonId,VerifyCodeButtonId, notFirstTimeId, firstTimeId;
    private static final String TAG = "PhoneAuthActivity";
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private CollectionReference users;
    private FirebaseFirestore db;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        db = FirebaseFirestore.getInstance();
        users = db.collection("Users");
        phonenumberId = findViewById(R.id.phonenumberId);
        loginButtonId = findViewById(R.id.loginButtonId);
        notFirstTimeId = findViewById(R.id.notFirstTimeId);
        nameId = findViewById(R.id.nameId);
        firstTimeId = findViewById(R.id.firstTimeId);
        codeId = findViewById(R.id.codeId);
        VerifyCodeButtonId = findViewById(R.id.VerifyCodeButtonId);
        phonenumberId.setVisibility(View.INVISIBLE);
        loginButtonId.setVisibility(View.INVISIBLE);
        codeId.setVisibility(View.INVISIBLE);
        nameId.setVisibility(View.INVISIBLE);
        VerifyCodeButtonId.setVisibility(View.INVISIBLE);
        loginButtonId.setVisibility(View.INVISIBLE);

        firstTimeId.setOnClickListener(v -> {
            loginButtonId.setVisibility(View.VISIBLE);
            phonenumberId.setVisibility(View.VISIBLE);
            nameId.setVisibility(View.VISIBLE);
            codeId.setVisibility(View.VISIBLE);
            VerifyCodeButtonId.setVisibility(View.VISIBLE);
            loginButtonId.setVisibility(View.VISIBLE);
            loginButtonId.setVisibility(View.VISIBLE);
            firstTimeId.setVisibility(View.INVISIBLE);
            notFirstTimeId.setVisibility(View.INVISIBLE);
        });

        notFirstTimeId.setOnClickListener(v -> {
            loginButtonId.setVisibility(View.VISIBLE);
            phonenumberId.setVisibility(View.VISIBLE);
            codeId.setVisibility(View.VISIBLE);
            VerifyCodeButtonId.setVisibility(View.VISIBLE);
            loginButtonId.setVisibility(View.VISIBLE);
            loginButtonId.setVisibility(View.VISIBLE);
            firstTimeId.setVisibility(View.INVISIBLE);
            notFirstTimeId.setVisibility(View.INVISIBLE);
        });

        loginButtonId.setOnClickListener(v -> {
            String phonenumber = phonenumberId.getText().toString();

            if (phonenumber.isEmpty()){
                Toast.makeText(LoginScreen.this, "Cannot be empty!", Toast.LENGTH_SHORT).show();
            } else {
                startPhoneNumberVerification(phonenumber);
            }
        });

        VerifyCodeButtonId.setOnClickListener(v -> {
            String code = codeId.getText().toString();
            if (code.isEmpty()) {
                Toast.makeText(LoginScreen.this, "Cannot be empty!", Toast.LENGTH_SHORT).show();
            } else {
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.e("Exception:", "FirebaseAuthInvalidCredentialsException" + e);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.e("Exception:", "FirebaseTooManyRequestsException" + e);
                }
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
                Log.i("Verification code:", verificationId);
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        name = nameId.getText().toString();
                        if(name.equals("")){
                            name = null;
                        }

                        Log.d(TAG, "signInWithCredential:success");
                        String phone = currentUser.getPhoneNumber();

                        Intent intent = new Intent(LoginScreen.this, ContactList.class);
                        intent.putExtra("uid", currentUser.getUid());
                        intent.putExtra("number", currentUser.getPhoneNumber());
                        intent.putExtra("name", name);
                        startActivity(intent);

                        FirebaseUser user = task.getResult().getUser();

                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                });
    }

    //check to see if the user is currently signed in.
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            currentUser.reload();
        }
        updateUI(currentUser);
    }
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)// Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void updateUI(FirebaseUser user) {

    }
    //query over the user, might need to move this to another class but temporarily implement it here
}