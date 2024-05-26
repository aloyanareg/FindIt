package com.example.findit.model;

import androidx.appcompat.app.AppCompatActivity;
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

import com.example.findit.AddItemActivity;
import com.example.findit.MainActivity;
import com.example.findit.R;
import com.example.findit.auth.RegisterActivity;
import com.example.findit.fragments.MyItemsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    FirebaseFirestore db;

    @Override
    protected void onResume() {
        super.onResume();
        bottomBar.setItemActiveIndex(2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        getWindow().setStatusBarColor(getColor(R.color.blue));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        userImage = findViewById(R.id.user_image);
        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setItemActiveIndex(2);
        db = FirebaseFirestore.getInstance();
        userName = findViewById(R.id.user_name);
        userName.setTextColor(getColor(R.color.blue));
        my_account = findViewById(R.id.my_account);
        my_posts_button = findViewById(R.id.my_posts_button);
        log_out = findViewById(R.id.log_out_button);
        mAuth = FirebaseAuth.getInstance();
        userName.setVisibility(View.VISIBLE);
        userImage.setVisibility(View.VISIBLE);
        log_out.setVisibility(View.VISIBLE);
        my_posts_button.setVisibility(View.VISIBLE);
        findViewById(R.id.my_account).setVisibility(View.VISIBLE);
        Uri photoUrl = mAuth.getCurrentUser().getPhotoUrl();
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                System.out.println("user task successful");
                if (document.exists()) {
                    System.out.println("user exists");
                    String displayName = document.getString("userName");
                    userName.setText(displayName);
                }
            }
        });
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
                hideMyAccountItems();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
                }
                return true;
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                showMyAccountItems();
            }
        });
    }

    private void hideMyAccountItems() {
        my_account.setVisibility(View.GONE);
    }

    private void showMyAccountItems() {
        my_account.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
