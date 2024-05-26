package com.example.findit.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class MyItemsFragment extends Fragment {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    private FirebaseFirestore db;
    private ItemAdapter itemAdapter;

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() != null) {
            getActivity().findViewById(R.id.user_name).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.user_image).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.my_posts_button).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.log_out_button).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            bundle.putString("ownerPhone", item.getOwnerPhone());
            currentItemFragment.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, currentItemFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        view.findViewById(R.id.exit_my_posts).setOnClickListener(v -> {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(MyItemsFragment.this);
                transaction.commit();
            }
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
        loadDataFromFirestore();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_items, container, false);
    }

    private void loadDataFromFirestore() {
        db.collection("items")
                .whereEqualTo("ownerID", user.getUid())
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
                    }
                });
    }
}
