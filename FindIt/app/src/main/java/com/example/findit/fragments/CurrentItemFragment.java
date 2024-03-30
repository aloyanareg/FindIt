package com.example.findit.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.findit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class CurrentItemFragment extends Fragment {
    ProgressBar progressBar;
    ImageButton closeButton;
    Button contactButton;
    TextView title, color, location, description;
    FirebaseFirestore db;
    FirebaseUser user;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        contactButton = getView().findViewById(R.id.contact_button);
        progressBar = getView().findViewById(R.id.progressBarCurrent);
        user = FirebaseAuth.getInstance().getCurrentUser();
        progressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
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
        description = getView().findViewById(R.id.description);
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
            color.setText(bundle.getString("color"));
            location.setText(bundle.getString("location"));
            description.setText(bundle.getString("description"));
            progressBar.setVisibility(View.GONE);
            closeButton.setVisibility(View.VISIBLE);
        }).addOnFailureListener(exception -> {
        });
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getUid().equals(bundle.getString("ownerId"))){
                    db.collection("users")
                            .whereEqualTo("userID", user.getUid())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    document.getReference().update("chatWith", FieldValue.arrayUnion(bundle.getString("ownerId")))
                                            .addOnSuccessListener(aVoid -> Log.d("UpdateSuccess", "DocumentSnapshot successfully updated!"))
                                            .addOnFailureListener(e -> Log.w("UpdateError", "Error updating document", e));
                                }
                            })
                            .addOnFailureListener(e -> Log.w("QueryError", "Error getting documents: ", e));
                    db.collection("users")
                            .whereEqualTo("userID", bundle.getString("ownerId"))
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    String newItem = user.getUid();
                                    document.getReference().update("chatWith", FieldValue.arrayUnion(newItem))
                                            .addOnSuccessListener(aVoid -> Log.d("UpdateSuccess", "DocumentSnapshot successfully updated!"))
                                            .addOnFailureListener(e -> Log.w("UpdateError", "Error updating document", e));
                                }
                            })
                            .addOnFailureListener(e -> Log.w("QueryError", "Error getting documents: ", e));
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_item, container, false);
    }
}