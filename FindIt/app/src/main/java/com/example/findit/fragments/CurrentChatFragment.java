package com.example.findit.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.findit.R;

public class CurrentChatFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    TextView displayName;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_chat, container, false);
        Bundle bundle = getArguments();
        displayName = view.findViewById(R.id.display_name);
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.send_button);
        displayName.setText(bundle.getString("userName"));
        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
    }
}