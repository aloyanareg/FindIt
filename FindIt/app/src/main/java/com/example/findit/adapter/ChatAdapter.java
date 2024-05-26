package com.example.findit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findit.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.UserViewHolder> {
    private List<String> chatWithList;
    private LayoutInflater inflater;
    private ChatAdapter.OnItemClickListener listener;
    FirebaseFirestore db;
    public ChatAdapter(Context context, ChatAdapter.OnItemClickListener listener) {
        this.chatWithList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chat_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        String userId = chatWithList.get(position);
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String displayName = document.getString("userName");
                    holder.userName.setText(displayName);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(userId);
                }
            }
        });
    }
    public interface OnItemClickListener {
        void onItemClick(String id);
    }
    @Override
    public int getItemCount() {
        return chatWithList.size();
    }

    public void setItems(List<String> chatWithList) {
        this.chatWithList = chatWithList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.display_name);
        }
    }
}
