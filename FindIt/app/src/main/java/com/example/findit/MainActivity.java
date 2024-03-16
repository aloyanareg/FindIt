package com.example.findit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.findit.auth.RegisterActivity;
import com.example.findit.fragments.FoundFragment;
import com.example.findit.fragments.LostFragment;
import com.example.findit.model.AccountActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    SmoothBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomBar = findViewById(R.id.bottomBar);

        loadFragment(new LostFragment());
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }

        Button newItemButton = findViewById(R.id.new_item_button);
        Button lostButton = findViewById(R.id.lost_button);
        Button foundButton = findViewById(R.id.found_button);

        newItemButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddItemActivity.class)));

        foundButton.setOnClickListener(v -> {
                    loadFragment(new FoundFragment());
                    foundButton.setBackgroundResource(R.drawable.lost_found_buttons_clicked);
                    lostButton.setBackgroundResource(R.drawable.lost_found_buttons);
                }
        );

        lostButton.setOnClickListener(v -> {
                    loadFragment(new LostFragment());
                    foundButton.setBackgroundResource(R.drawable.lost_found_buttons);
                    lostButton.setBackgroundResource(R.drawable.lost_found_buttons_clicked);
                }
        );
       bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
           @Override
           public boolean onItemSelect(int i) {
               Intent intent;
               switch (i){
                   case 1:
                       intent = new Intent(MainActivity.this, AddItemActivity.class);
                       startActivity(intent);
                       overridePendingTransition(0, 0);
                       break;
                   case 2:
                       intent = new Intent(MainActivity.this, AccountActivity.class);
                       startActivity(intent);
                       overridePendingTransition(0, 0);
               }
               return true;
           }
       });

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}