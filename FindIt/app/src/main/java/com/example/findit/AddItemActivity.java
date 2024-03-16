package com.example.findit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.findit.model.AccountActivity;
import com.example.findit.model.ImageUploader;
import com.example.findit.model.Item;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class AddItemActivity extends AppCompatActivity  {
    SmoothBottomBar bottomBar;
    private EditText itemTypeEditText, itemColorEditText, itemLocationEditText;
    private RadioGroup itemLostOrFoundRadioGroup;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ImageView selectedImageView;
    Button pick_image_button;
    FrameLayout imageLayout;
    // Create a storage reference from our app
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        imageLayout = findViewById(R.id.imageLayout);
        bottomBar = findViewById(R.id.bottomBar);
        selectedImageView = findViewById(R.id.selected_image_view);
        db = FirebaseFirestore.getInstance();
        pick_image_button = findViewById(R.id.pick_image_button);
        auth = FirebaseAuth.getInstance();
        itemTypeEditText = findViewById(R.id.item_type);
        itemColorEditText = findViewById(R.id.item_color);
        itemLocationEditText = findViewById(R.id.item_location);
        itemLostOrFoundRadioGroup = findViewById(R.id.item_lost_or_found);

        AppCompatButton addButton = findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToFirestore();
            }
        });
        bottomBar.setItemActiveIndex(1);
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                Intent intent;
                switch (i) {
                    case 0:
                        intent = new Intent(AddItemActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case 2:
                        intent = new Intent(AddItemActivity.this, AccountActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                }
                return true;
            }
        });
        pick_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLayout.getVisibility() == View.GONE) {
                    ImagePicker.with(AddItemActivity.this)
                            .crop(400, 400)
                            .compress(1024)
                            .maxResultSize(1080, 1080)
                            .start();
                } else {
                    pick_image_button.setBackgroundResource(R.drawable.add_new_item_button);
                    pick_image_button.setText(R.string.pick_image);
                    selectedImageView.setImageURI(null);
                    imageLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        imageLayout.setVisibility(View.VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        selectedImageView.setImageURI(uri);
        selectedImageView.setVisibility(View.VISIBLE);
        if(selectedImageView.getDrawable() == null){
            selectedImageView.setVisibility(View.GONE);
        }
        else{
            pick_image_button.setBackgroundResource(R.drawable.ic_close_button);
            pick_image_button.setText(R.string.remove_image);
        }
    }
    private void addItemToFirestore() {
        String type = itemTypeEditText.getText().toString().trim();
        String color = itemColorEditText.getText().toString().trim();
        String location = itemLocationEditText.getText().toString().trim();
        boolean isLost = itemLostOrFoundRadioGroup.getCheckedRadioButtonId() == R.id.item_lost;

        if (itemLostOrFoundRadioGroup.getCheckedRadioButtonId() == -1 || type.isEmpty() || color.isEmpty() || location.isEmpty()|| selectedImageView.getDrawable() == null) {
            Toast.makeText(AddItemActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Item item = new Item();
        item.setTitle(type);
        item.setColor(color);
        item.setLocation(location);
        item.setOwnerID(auth.getCurrentUser().getUid());
        item.setLost(isLost);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        ImageUploader uploader = new ImageUploader();
        uploader.uploadImage(selectedImageView);
        item.setPhotoUrl(uploader.getUrl());
        db.collection("items")
                .add(item)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddItemActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddItemActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(AddItemActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
