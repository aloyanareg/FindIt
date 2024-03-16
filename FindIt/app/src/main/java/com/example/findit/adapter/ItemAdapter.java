package com.example.findit.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.findit.R;
import com.example.findit.model.Item;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
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
        holder.titleTextView.setText(isLost + ": " + item.getTitle());
        holder.colorTextView.setText("Color: " + item.getColor());
        holder.locationTextView.setText("Location: " + item.getLocation());
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("/images/" + item.getPhotoUrl() + ".jpg");
        System.out.println(imageRef);
        // Assuming you have an initialized StorageReference (e.g., imageRef)
        imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
            String imageUrl = downloadUrl.toString();
            Glide.with(holder.itemImage.getContext()).load(imageUrl).into(holder.itemImage);
        }).addOnFailureListener(exception -> {
        });

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

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            colorTextView = itemView.findViewById(R.id.colorTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            itemImage = itemView.findViewById(R.id.item_image);
        }
    }
}

