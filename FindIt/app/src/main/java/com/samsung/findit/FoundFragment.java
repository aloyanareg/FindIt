//package com.samsung.findit;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.Map;
//
//public class FoundFragment extends Fragment {
//    private FirebaseFirestore db;
//    private CollectionReference itemsCollection;
//    LinearLayout items;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        db = FirebaseFirestore.getInstance();
//        itemsCollection = db.collection("items");
//        items = getView().findViewById(R.id.all_items);
//        startActivity();
//        return inflater.inflate(R.layout.fragment_lost, container, false);
//    }
//
//    protected void startActivity() {
//        items.removeAllViews();
//        db.collection("items")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult()) {
//                                Map<String, Object> data = document.getData();
//                                if (data != null) {
//                                    String lostFound = (String) data.get("lostFound");
//                                    String type = (String) data.get("type");
//                                    String location = (String) data.get("location");
//                                    String color = (String) data.get("color");
//                                    String email = (String) data.get("email");
//                                    if (lostFound.equals("Lost")) {
//                                        RelativeLayout new_item = new RelativeLayout(getContext());
//
//                                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
//                                                RelativeLayout.LayoutParams.MATCH_PARENT,
//                                                RelativeLayout.LayoutParams.WRAP_CONTENT
//                                        );
//                                        layoutParams.setMargins(0, 20, 0, 0);
//                                        new_item.setPadding(25, 25, 25, 25);
//                                        new_item.setLayoutParams(layoutParams);
//                                        new_item.setBackgroundResource(R.drawable.rls_background);
//                                        TextView textView = new TextView(getContext());
//                                        textView.setText(lostFound + " " + type + "\n" + "Color: " + color + "\n" + "In " + location + "\n" + email);
//                                        textView.setTextSize(20);
//                                        new_item.addView(textView);
//                                        items.addView(new_item);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                });
//    }
//}