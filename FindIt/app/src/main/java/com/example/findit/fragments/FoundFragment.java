package com.example.findit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findit.R;
import com.example.findit.adapter.ItemAdapter;
import com.example.findit.model.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FoundFragment extends Fragment {
    private FirebaseFirestore db;
    private ItemAdapter itemAdapter;
    ProgressBar progressBar;
    public FoundFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found, container, false);

        db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemAdapter = new ItemAdapter(new ArrayList<>());
        recyclerView.setAdapter(itemAdapter);
        progressBar = getActivity().findViewById(R.id.progressBar);
        loadDataFromFirestore();

        return view;
    }

    private void loadDataFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("items")
                .whereEqualTo("lost", false)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Item> itemList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Item item = documentSnapshot.toObject(Item.class);
                            itemList.add(item);
                        }
                        itemAdapter.setItems(itemList);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
