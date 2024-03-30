package com.example.findit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.findit.R;
import com.example.findit.model.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private OnItemClickListener listener;
    FirebaseFirestore db;

    public ItemAdapter(List<Item> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        String isLost;
        if (item.isLost()) isLost = "Lost";
        else isLost = "Found";
        String photoUrl = item.getPhotoUrl();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(item.getOwnerID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();
                String displayName = document.getString("userName");
                holder.displayName.setText(displayName);
            }
        });
        holder.titleTextView.setText(isLost + ": " + item.getTitle());
        holder.colorTextView.setText("Color: " + item.getColor());
        holder.locationTextView.setText("Location: " + item.getLocation());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users").child(item.getOwnerID());
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("/images/" + item.getPhotoUrl() + ".jpg");
        System.out.println(imageRef);
        holder.pb.setVisibility(View.VISIBLE);
        imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
            String imageUrl = downloadUrl.toString();
            Glide.with(holder.itemImage.getContext()).load(imageUrl).into(holder.itemImage);
            holder.pb.setVisibility(View.GONE);
        }).addOnFailureListener(exception -> {
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            }
        });
    }
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItems(List<Item> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView colorTextView;
        TextView locationTextView;
        ImageView itemImage;
        TextView displayName;
        ProgressBar pb;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            colorTextView = itemView.findViewById(R.id.colorTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            displayName = itemView.findViewById(R.id.display_name);
            itemImage = itemView.findViewById(R.id.item_image);
            pb = itemView.findViewById(R.id.image_progressBar);
        }
    }
}

