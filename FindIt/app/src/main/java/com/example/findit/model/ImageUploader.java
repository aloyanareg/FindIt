package com.example.findit.model;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ImageUploader {
    private StorageReference storageReference;
    String imageName = "";
    public ImageUploader() {
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void uploadImage(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        imageName = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("images/" + imageName + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();
        imageRef.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                    });
                })
                .addOnFailureListener(e -> {
                });
    }
    public String getUrl(){
        return imageName;
    }
}
