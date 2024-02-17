    package com.samsung.findit;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;

    import android.annotation.SuppressLint;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.os.Bundle;
    import android.text.Editable;
    import android.text.TextWatcher;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.ActionCodeSettings;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;

    public class RegisterActivity extends AppCompatActivity {
        Button submit;
        EditText email_et;
        EditText password_et;
        EditText repeat_password_et;
        Button submit_by_google;
        TextView errors;
        TextView login_hint;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            submit = findViewById(R.id.submit);
            submit_by_google = findViewById(R.id.submitGoogle);
            email_et = findViewById(R.id.email);
            password_et = findViewById(R.id.password);
            repeat_password_et = findViewById(R.id.repeatPassword);
            errors = findViewById(R.id.errors);
            login_hint = findViewById(R.id.login_hint);
            password_et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().isEmpty()) {
                        password_et.setError("Password cannot be empty");
                    } else {
                        password_et.setError(null);
                    }
                }
            });
            FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            login_hint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(intent);
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    String email = email_et.getText().toString();
                    String password = password_et.getText().toString();
                    String repeatPassword = repeat_password_et.getText().toString();
                    if (!password.equals(repeatPassword)) {
                        errors.setText("Passwords do not match");
                    }
                    else if(password.length() < 8){
                        errors.setText("Password length must be not less than 8");
                    }
                    else {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> verificationTask) {
                                                        if (verificationTask.isSuccessful()) {
                                                            Log.d("email", "Email verification sent.");
                                                        } else {
                                                            Log.e("email", "Failed to send email verification.", verificationTask.getException());
                                                        }
                                                    }
                                                });
                                                Intent intent = new Intent(RegisterActivity.this, LostFoundActivity.class);
                                                RegisterActivity.this.startActivity(intent);

                                            }
                                        } else {
                                            Log.e("registration", "Registration failed.", task.getException());
                                        }
                                    }
                                });
                    }
                }

            });
        }
    }