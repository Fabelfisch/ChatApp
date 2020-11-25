package com.stuberf.chatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    //private FirebaseDatabase firebaseDatabase;
    EditText messageInput;

    ArrayList<Map<String, Object>> messages;
    String chatId;
    RecyclerView recyclerView;
    MessageRecyclerAdapter messageRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatId = getIntent().getStringExtra("Chat");
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        /*firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("message");
        reference.setValue("Hello World");*/
        messageInput = findViewById(R.id.messageInput);
        messageInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageInput.setText("");
            }
        });
        messageInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    send(v);
                    return true;
                }
                return false;
            }
        });
        messages = new ArrayList<Map<String, Object>>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        firebaseFirestore.collection("Chats").document(chatId).collection("Messages").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot snap:queryDocumentSnapshots){
                    messages.add(snap.getData());
                }
                System.out.println(messages);
                recyclerView = findViewById(R.id.messageRecycler);
                recyclerView.setLayoutManager(layoutManager);
                messageRecyclerAdapter = new MessageRecyclerAdapter(messages, firebaseUser.getEmail());
                recyclerView.setAdapter(messageRecyclerAdapter);
            }
        });
    }

    public void send(View view){
        final Message message = new Message(messageInput.getText().toString(), firebaseUser.getEmail());
        firebaseFirestore.collection("Chats").document(chatId).collection("Messages").document(message.getMessageTime()+" ").set(message.getHash()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                messages.add(message.getHash());
                messageRecyclerAdapter.notifyDataSetChanged();
                messageInput.getText().clear();
            }
        });
    }
}