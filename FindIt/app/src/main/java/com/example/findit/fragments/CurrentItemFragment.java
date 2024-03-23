package com.example.findit.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.findit.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class CurrentItemFragment extends Fragment {
    ProgressBar progressBar;
    ImageButton closeButton;
    TextView title, color, location;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        progressBar = getView().findViewById(R.id.progressBarCurrent);
        progressBar.setVisibility(View.VISIBLE);
        closeButton = getView().findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        title = getView().findViewById(R.id.title);
        location = getView().findViewById(R.id.location);
        color = getView().findViewById(R.id.color);
        Bundle bundle = getArguments();
        ImageView item_image = getView().findViewById(R.id.item_image);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("/images/" + bundle.getString("photoUrl") + ".jpg");
        imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
            String imageUrl = downloadUrl.toString();
            Glide.with(item_image).load(imageUrl).into(item_image);
            String lostFound;
            if(bundle.getBoolean("lostFound")){
                lostFound = "Lost";
            }
            else{
                lostFound = "Found";
            }
            title.setText(lostFound + " " + bundle.getString("title"));
            color.setText("Color: " +bundle.getString("color"));
            location.setText("Location: " + bundle.getString("location"));
            progressBar.setVisibility(View.GONE);
            closeButton.setVisibility(View.VISIBLE);
        }).addOnFailureListener(exception -> {
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_item, container, false);
    }
}