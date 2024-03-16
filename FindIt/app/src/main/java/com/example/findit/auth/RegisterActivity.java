package com.example.findit.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputRepeatPassword;
    TextView errorTextView;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    Button continue_with_google;
    ProgressBar google_pb;
    GoogleSignInClient gsc;
    TextView loginHint;
    static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(getResources().getColor(R.color.light_blue));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();
        google_pb = findViewById(R.id.google_pb);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputRepeatPassword = findViewById(R.id.repeatPassword);
        progressBar = findViewById(R.id.submit_pb);
        continue_with_google = findViewById(R.id.submitGoogle);
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
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
        continue_with_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continue_with_google.setText("");
                continue_with_google.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); // Set all drawables to null
                google_pb.setVisibility(View.VISIBLE);
                signIn();

            }
        });
    }
    void signIn(){
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

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        continue_with_google.setText("Google");
                        google_pb.setVisibility(View.INVISIBLE);
                        continue_with_google.setCompoundDrawablesWithIntrinsicBounds(R.drawable.google_logo, 0, 0, 0);
                        Log.w("GOOGLE: ", "signInWithCredential:failure", task.getException());
                    }
                });
    }
}
