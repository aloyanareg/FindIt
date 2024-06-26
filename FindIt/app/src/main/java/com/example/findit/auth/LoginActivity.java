package com.example.findit.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findit.MainActivity;
import com.example.findit.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    TextView errorTextView;
    RelativeLayout continue_with_google;
    ProgressBar google_pb;
    GoogleSignInClient gsc;
    static final int RC_SIGN_IN = 9001;
    private ProgressBar progressBar;
    Button guest_mode;
    private FirebaseAuth auth;
    TextView register_hint;
    Button submitButton;
    private final String default_email = "sictst1@gmail.com";
    private final String default_password = "Samsung2023";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(getResources().getColor(R.color.light_blue));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        guest_mode = findViewById(R.id.guest_mode);
        gsc = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();
        register_hint = findViewById(R.id.register_hint);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.submit_pb);
        errorTextView = findViewById(R.id.error_message_login);

        continue_with_google = findViewById(R.id.google_rl);
        google_pb = findViewById(R.id.google_pb);
        register_hint.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        submitButton = findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    errorTextView.setText("Enter email address.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    errorTextView.setText("Enter password.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                submitButton.setText("");
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                submitButton.setText(R.string.login_submit_hint);
                                if (!task.isSuccessful()) {
                                    Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    checkEmailVerification(auth.getCurrentUser());
                                }
                            }
                        });
            }
        });
        guest_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    auth.signInWithEmailAndPassword(default_email, default_password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    submitButton.setText(R.string.login_submit_hint);
                                    if (!task.isSuccessful()) {
                                        Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                        checkEmailVerification(auth.getCurrentUser());
                                    }
                                }
                            });
                }catch (Exception e){

                }
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
    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void checkEmailVerification(FirebaseUser user) {
        if (!user.isEmailVerified()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Email not verified");
            builder.setMessage("Please check your email to verify your account.");
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_verification, null);
            dialogView.setBackgroundDrawable(getDrawable(R.drawable.verifiy_dialog));
            builder.setView(dialogView);
            ProgressBar progressBar = dialogView.findViewById(R.id.dialog_progress_bar);
            TextView dialogText = dialogView.findViewById(R.id.dialog_text);
            dialogText.setText("Sending verification email...");
            TextView backButton = dialogView.findViewById(R.id.back_button);
            AlertDialog dialog = builder.create();
            backButton.setOnClickListener(view -> {
                dialog.dismiss();
            });
            dialog.setCancelable(false);
            dialog.show();
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialogText.setText("Verification email sent. Check your email and verify your account.");
                        } else {
                            dialogText.setText("Failed to send verification email. Try again later.");
                        }
                        progressBar.setVisibility(View.GONE);
                    });

            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                dialog.dismiss();
            });
            builder.setNegativeButton("Resend Email", (dialogInterface, i) -> {
                sendVerificationEmail(user);
                dialog.dismiss();
            });
        } else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
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

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        findViewById(R.id.google_text).setVisibility(View.VISIBLE);
                        google_pb.setVisibility(View.INVISIBLE);
                        findViewById(R.id.google_button_logo).setVisibility(View.VISIBLE);
                        Log.w("GOOGLE: ", "signInWithCredential:failure", task.getException());
                    }
                });
    }
}
