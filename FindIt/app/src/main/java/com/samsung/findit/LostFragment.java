package com.samsung.findit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class LostFragment extends Fragment {
    private CollectionReference itemsCollection;
    LinearLayout all_items;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        itemsCollection = db.collection("items");
        all_items =  getActivity().findViewById(R.id.all_items);
        progressBar = getActivity().findViewById(R.id.progressBar);
        startActivity();

        return inflater.inflate(R.layout.fragment_lost, container, false);
    }

    private void startActivity() {
        progressBar.setVisibility(View.VISIBLE);
        all_items.removeAllViews();
        itemsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            if (data != null) {
                                String lostFound = (String) data.get("lostFound");
                                String type = (String) data.get("type");
                                String location = (String) data.get("location");
                                String color = (String) data.get("color");
                                String email = (String) data.get("email");
                                System.out.println(lostFound + " " + type + "\n" + "Color: " + color + "\n" + "In " + location + "\n" + email);

                                if (lostFound.equals("Lost")) {
                                    RelativeLayout new_item = new RelativeLayout(getContext());

                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.MATCH_PARENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    layoutParams.setMargins(20, 40, 20, 0);
                                    new_item.setPadding(25, 25, 25, 25);
                                    new_item.setLayoutParams(layoutParams);
                                    new_item.setBackgroundResource(R.drawable.rls_background);
                                    TextView textView = new TextView(getContext());
                                    textView.setText(lostFound + " " + type + "\n" + "Color: " + color + "\n" + "In " + location + "\n" + email);
                                    textView.setTextSize(20);
                                    new_item.addView(textView);
                                    all_items.addView(new_item);
                                }
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}