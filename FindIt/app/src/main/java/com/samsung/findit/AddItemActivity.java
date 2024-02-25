package com.samsung.findit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {
    RadioGroup rg;
    String choice = "";
    Button add_item;
    String type;
    String color;
    String location;
    EditText type_et;
    EditText color_et;
    EditText location_et;
    private FirebaseFirestore db;
    private CollectionReference itemsCollection;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        db = FirebaseFirestore.getInstance();
        itemsCollection = db.collection("items");
        add_item = findViewById(R.id.add_item);
        type_et = findViewById(R.id.item_type);
        color_et = findViewById(R.id.item_color);
        location_et = findViewById(R.id.item_location);
        rg = findViewById(R.id.item_lost_or_found);

        user = FirebaseAuth.getInstance().getCurrentUser();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.item_lost){
                    choice = "Lost";
                }
                else if(checkedId == R.id.item_found){
                    choice = "Found";
                }
            }
        });
        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = type_et.getText().toString();
                color = color_et.getText().toString();
                location = location_et.getText().toString();
                if(!choice.equals("") && !type.equals("") && !color.equals("") && !location.equals("")){
                    assert user != null;
                    addLostFoundItem(choice, color, location, null, type, user.getEmail());
                    Intent intent = new Intent(AddItemActivity.this, LostFoundActivity.class);
                    intent.putExtra("currentUser", user);
                    AddItemActivity.this.startActivity(intent);
                }
            }
        });
    }
    public void addLostFoundItem(String lostFound, String color, String location, String photoUrl, String type, String email) {
        Map<String, Object> item = new HashMap<>();
        item.put("lostFound", lostFound);
        item.put("color", color);
        item.put("location", location);
        item.put("photo", photoUrl);
        item.put("type", type);
        item.put("email", email);
        itemsCollection.add(item)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("Adding item", "Item added with ID: " + task.getResult().getId());

                        } else {
                            Log.w("Adding item", "Error adding item", task.getException());
                        }
                    }
                });
    }
}