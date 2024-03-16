package com.example.findit.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.findit.AddItemActivity;
import com.example.findit.MainActivity;
import com.example.findit.R;
import com.example.findit.auth.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class AccountActivity extends AppCompatActivity {
    SmoothBottomBar bottomBar;
    Button log_out;
    FirebaseAuth mAuth;
    ImageView userImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        userImage = findViewById(R.id.user_image);
        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setItemActiveIndex(2);
        log_out = findViewById(R.id.log_out_button);
        mAuth = FirebaseAuth.getInstance();
        Uri photoUrl = mAuth.getCurrentUser().getPhotoUrl();
        if(photoUrl == null){
            userImage.setBackgroundResource(R.drawable.baseline_account_circle_24);
        }
        userImage.setImageURI(photoUrl);
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                mAuth.signOut();
                Intent intent = new Intent(AccountActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                Intent intent;
                switch (i){
                    case 1:
                        intent = new Intent(AccountActivity.this, AddItemActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case 0:
                        intent = new Intent(AccountActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                }
                return true;
            }
        });
    }
}