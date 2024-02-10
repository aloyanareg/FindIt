package com.samsung.findit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LostFoundActivity extends AppCompatActivity {
    Button lost_button;
    Button found_button;
    Button new_item_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found);
        lost_button = findViewById(R.id.lost_button);
        found_button = findViewById(R.id.found_button);
        new_item_button = findViewById(R.id.new_item_button);
        lost_button.setBackgroundResource(R.drawable.lost_found_buttons_clicked);
        found_button.setBackgroundResource(R.drawable.lost_found_buttons);
        loadFragment(new LostFragment());
        lost_button.setOnClickListener(view -> {
            lost_button.setBackgroundResource(R.drawable.lost_found_buttons_clicked);
            found_button.setBackgroundResource(R.drawable.lost_found_buttons);
            loadFragment(new LostFragment());
        });

        found_button.setOnClickListener(view -> {
            lost_button.setBackgroundResource(R.drawable.lost_found_buttons);
            found_button.setBackgroundResource(R.drawable.lost_found_buttons_clicked);
            loadFragment(new FoundFragment());
        });

        new_item_button.setOnClickListener(view -> {
            Intent intent = new Intent(LostFoundActivity.this, AddItemActivity.class);
            LostFoundActivity.this.startActivity(intent);
        });
    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.all_items, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}