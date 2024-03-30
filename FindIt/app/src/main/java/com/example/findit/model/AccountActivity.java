package com.example.findit.model;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.findit.AddItemActivity;
import com.example.findit.ChatActivity;
import com.example.findit.MainActivity;
import com.example.findit.R;
import com.example.findit.auth.RegisterActivity;
import com.example.findit.fragments.MyItemsFragment;
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
    TextView userName;
    LinearLayout my_account;
    Button my_posts_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(getColor(R.color.light_blue));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        userImage = findViewById(R.id.user_image);
        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setItemActiveIndex(3);
        userName = findViewById(R.id.user_name);
        userName.setTextColor(getColor(R.color.light_blue));
        my_account = findViewById(R.id.my_account);
        my_posts_button = findViewById(R.id.my_posts_button);
        log_out = findViewById(R.id.log_out_button);
        mAuth = FirebaseAuth.getInstance();
        Uri photoUrl = mAuth.getCurrentUser().getPhotoUrl();
        userName.setText(mAuth.getCurrentUser().getDisplayName());
        if(photoUrl != null){
            userImage.setImageURI(photoUrl);
        }
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                mAuth.signOut();
                Intent intent = new Intent(AccountActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        my_posts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyItemsFragment fragment = new MyItemsFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, fragment)
                        .commit();
            }
        });
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                Intent intent;
                switch (i){
                    case 0:
                        intent = new Intent(AccountActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case 1:
                        intent = new Intent(AccountActivity.this, AddItemActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case 2:
                        intent = new Intent(AccountActivity.this, ChatActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                }
                return true;
            }
        });
    }
}