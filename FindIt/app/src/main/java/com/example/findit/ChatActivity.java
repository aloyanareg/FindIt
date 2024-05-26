package com.example.findit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import com.example.findit.adapter.ChatAdapter;
import com.example.findit.fragments.CurrentChatFragment;
import com.example.findit.model.AccountActivity;
import com.example.findit.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class ChatActivity extends AppCompatActivity {
    SmoothBottomBar bottomBar;
    ChatAdapter chatAdapter;
    ScrollView scrollView;
    FirebaseFirestore db;
    FirebaseUser user;
    protected void onResume(){
        super.onResume();
        bottomBar.setItemActiveIndex(2);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        db = FirebaseFirestore.getInstance();
        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.VISIBLE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, id -> {
            Fragment currentChatFragment = new CurrentChatFragment();
            Bundle bundle = new Bundle();
            bundle.putString("ownerId", id);
            db.collection("users").document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DocumentSnapshot document = task.getResult();
                    String displayName = document.getString("userName");
                    bundle.putString("userName", displayName);
                    currentChatFragment.setArguments(bundle);
                    scrollView.setVisibility(View.INVISIBLE);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, currentChatFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });
        });
        recyclerView.setAdapter(chatAdapter);
        db.collection("users")
                .whereEqualTo("userID", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User currentUser = documentSnapshot.toObject(User.class);
                        List<String> chatWithList = currentUser.getChatWith();
                        chatAdapter.setItems(chatWithList);
                    }
                });
        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setItemActiveIndex(2);
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                Intent intent;
                switch (i) {
                    case 0:
                        intent = new Intent(ChatActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);

                        break;
                    case 1:
                        intent = new Intent(ChatActivity.this, AddItemActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case 3:
                        intent = new Intent(ChatActivity.this, AccountActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                }
                return true;
            }
        });
    }
}