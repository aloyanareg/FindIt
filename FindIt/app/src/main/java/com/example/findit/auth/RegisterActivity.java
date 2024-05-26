package com.example.findit.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findit.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findit.MainActivity;
import com.example.findit.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputRepeatPassword, user_name;
    TextView errorTextView;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    RelativeLayout continue_with_google;
    ProgressBar google_pb;
    GoogleSignInClient gsc;
    TextView loginHint;
    static final int RC_SIGN_IN = 9001;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(getResources().getColor(R.color.light_blue));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        db = FirebaseFirestore.getInstance();
        gsc = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();
        google_pb = findViewById(R.id.google_pb);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        user_name = findViewById(R.id.user_name);
        inputRepeatPassword = findViewById(R.id.repeatPassword);
        progressBar = findViewById(R.id.submit_pb);
        continue_with_google = findViewById(R.id.google_rl);
        errorTextView = findViewById(R.id.error_message_register);


        loginHint = findViewById(R.id.login_hint);
        loginHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        Button btnRegister = findViewById(R.id.submit);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorTextView.setText("");
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String repeatPassword = inputRepeatPassword.getText().toString().trim();
                String userName = user_name.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    errorTextView.setText("Enter email address");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    errorTextView.setText("Enter password.");
                    return;
                }

                if (TextUtils.isEmpty(repeatPassword)) {
                    errorTextView.setText("Repeat your password.");
                    return;
                }
                if (TextUtils.isEmpty(userName)) {
                    errorTextView.setText("Enter username");
                    return;
                }

                if (!password.equals(repeatPassword)) {
                    errorTextView.setText("Passwords do not match!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                btnRegister.setText("");
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                btnRegister.setText(R.string.register_submit_hint);
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registration failed. " + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration successful!",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = auth.getCurrentUser();
                                    List<String> chatWithList = new ArrayList<>();
                                    User _user = new User(user.getUid(), chatWithList, userName);
                                    db.collection("users").document(user.getUid()).set(_user);
                                    sendVerificationEmail(user);
                                }
                            }
                        });
            }
        });
        continue_with_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.google_text).setVisibility(View.INVISIBLE);
                findViewById(R.id.google_button_logo).setVisibility(View.INVISIBLE);
                google_pb.setVisibility(View.VISIBLE);
                signIn();

            }
        });
    }
    private void showVerificationDialog(FirebaseUser user) {

        Dialog verificationDialog = new Dialog(this);
        verificationDialog.setContentView(R.layout.dialog_verification);
        TextView backButton = verificationDialog.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {verificationDialog.dismiss();});
        verificationDialog.setCancelable(false);
        verificationDialog.show();
        final Handler handler = new Handler(Looper.getMainLooper());
        final int delay = 5000;

        handler.postDelayed(new Runnable() {
            public void run() {
                user.reload().addOnCompleteListener(task -> {
                    if (user.isEmailVerified()) {
                        verificationDialog.dismiss();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        handler.postDelayed(this, delay);
                    }
                });
            }
        }, delay);
    }
    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showVerificationDialog(user);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GOOGLE: ", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("GOOGLE", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        System.out.println(user.getEmail());
                        List<String> chatWithList = new ArrayList<>();
                        User _user = new User(user.getUid(), chatWithList, user.getDisplayName());
                        db.collection("users").document(user.getUid()).set(_user);
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        findViewById(R.id.google_text).setVisibility(View.VISIBLE);
                        findViewById(R.id.google_button_logo).setVisibility(View.VISIBLE);
                        Log.w("GOOGLE: ", "signInWithCredential:failure", task.getException());
                    }
                });
    }
}
