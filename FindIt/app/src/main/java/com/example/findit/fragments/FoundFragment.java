package com.example.findit.fragments;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findit.R;
import com.example.findit.adapter.ItemAdapter;
import com.example.findit.model.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FoundFragment extends Fragment {
    private FirebaseFirestore db;
    private ItemAdapter itemAdapter;

    ProgressBar progressBar;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    public FoundFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found, container, false);
        getActivity().findViewById(R.id.buttons_rl).setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemAdapter = new ItemAdapter(new ArrayList<>(), item -> {
            Fragment currentItemFragment = new CurrentItemFragment();
            Bundle bundle = new Bundle();
            bundle.putString("photoUrl", item.getPhotoUrl());
            bundle.putString("ownerId", item.getOwnerID());
            bundle.putString("color", item.getColor());
            bundle.putBoolean("lostFound", item.isLost());
            bundle.putString("location", item.getLocation());
            bundle.putString("title", item.getTitle());
            bundle.putString("description", item.getDescription());
            bundle.putString("pastFragment", "FoundFragment");
            bundle.putString("ownerPhone", item.getOwnerPhone());
            currentItemFragment.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            getActivity().findViewById(R.id.buttons_rl).setVisibility(GONE);
            transaction.replace(R.id.fragment_container, currentItemFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        recyclerView.setAdapter(itemAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    loadDataFromFirestore();
                }
            }
        });
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
                            //if(!user.getUid().equals(item.getOwnerID())){
                                itemList.add(item);
                            //}
                        }
                        itemAdapter.setItems(itemList);
                        progressBar.setVisibility(GONE);
                    }
                });
    }
}
