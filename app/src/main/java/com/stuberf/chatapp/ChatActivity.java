package com.stuberf.chatapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    EditText messageInput;
    ProgressBar progressBar;
    TextView textView;

    ArrayList<Map<String, Object>> messages;
    String chatId;
    RecyclerView recyclerView;
    MessageRecyclerAdapter messageRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //make Toolbar
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.chatToolbar);

        //make Progressbar visible until the messages are loaded
        progressBar = findViewById(R.id.loadingChat);
        progressBar.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        TextView toolbarText = toolbar.findViewById(R.id.chatToolbarText);
        toolbarText.setText(getIntent().getStringExtra("User"));

        //Initialize all variables
        chatId = getIntent().getStringExtra("Chat");
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        messageInput = findViewById(R.id.messageInput);
        messages = new ArrayList<Map<String, Object>>();
        textView = findViewById(R.id.noMessagesTextView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.messageRecycler);
        recyclerView.setLayoutManager(layoutManager);
        messageRecyclerAdapter = new MessageRecyclerAdapter(messages, firebaseUser.getEmail());
        recyclerView.setAdapter(messageRecyclerAdapter);

        //flag that the user is currently in the chat
        firebaseFirestore.collection("Users").document(firebaseUser.getEmail())
                .collection("Chats").document(chatId).update("online", 1);

        //Wait for user to type message
        messageInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageInput.setText("");
            }
        });
        //enable send with Enter button
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

        //scroll to the bottom of the messages, such that the user sees the most recent messages
        if(recyclerView.getAdapter().getItemCount()>0) {
            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        }

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                textView.setText("");
                System.out.println(firebaseUser.getEmail());
                firebaseFirestore.collection("Users").document(firebaseUser.getEmail())
                        .collection("Chats").document(chatId).update("unread", 0).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        System.out.println("done!");
                    }
                });
                messages.add((HashMap) snapshot.getValue());
                progressBar.setVisibility(View.GONE);
                messageRecyclerAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                textView.setText("");
                messages.add((HashMap) snapshot.getValue());
                progressBar.setVisibility(View.GONE);
                messageRecyclerAdapter.notifyDataSetChanged();
                firebaseFirestore.collection("Users").document(firebaseUser.getEmail())
                        .collection("Chats").document(chatId).update("unread", 0);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                textView.setText("");
                messages.add((HashMap) snapshot.getValue());
                progressBar.setVisibility(View.GONE);
                messageRecyclerAdapter.notifyDataSetChanged();
                firebaseFirestore.collection("Users").document(firebaseUser.getEmail())
                        .collection("Chats").document(chatId).update("unread", 0);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        };
        //retrieve the data from the realtime database
        databaseReference.child(chatId).addChildEventListener(childEventListener);

        //if there are no messages in the chat, then this is shown to the user
        firebaseFirestore.collection("Chats").document(chatId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().get("messages").toString().equals("0")) {
                    progressBar.setVisibility((View.GONE));
                    textView.setText("There are currently no messages in this chat!");
                }
            }
        });

        //flag the chat that it is read
        firebaseFirestore.collection("Users").document(firebaseUser.getEmail())
                .collection("Chats").document(chatId).update("unread", 0);
        //the following code is from https://stackoverflow.com/questions/34102741/recyclerview-not-scrolling-to-end-when-keyboard-opens
        //again scroll to the bottom
        if (Build.VERSION.SDK_INT >= 11) {
            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (bottom < oldBottom) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(recyclerView.getAdapter().getItemCount()>0) {
                                    recyclerView.smoothScrollToPosition(
                                            recyclerView.getAdapter().getItemCount() - 1);
                                }
                            }
                        }, 100);
                    }
                }
            });
        }
    }

    //send message function
    public void send(View view){
        if(!messageInput.getText().toString().replace(" ", "").equals("")) {
            final Message message = new Message(messageInput.getText().toString(), firebaseUser.getEmail());
            databaseReference.child(chatId).child(message.getMessageTime() + "").setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    messageInput.getText().clear();
                    firebaseFirestore.collection("Chats").document(chatId).update("time", new Date().getTime());
                    firebaseFirestore.collection("Chats").document(chatId).update("messages", 1);
                    firebaseFirestore.collection("Users").document(getIntent().getStringExtra("User"))
                            .collection("Chats").document(chatId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().get("online").toString().equals("0")){
                                firebaseFirestore.collection("Users").document(getIntent().getStringExtra("User"))
                                        .collection("Chats").document(chatId).update("unread", 1);
                            }
                        }
                    });
                }
            });
        }
        else{
            messageInput.getText().clear();
        }
    }

    //Override of standard back pressed function such that the Main activity gets restarted
    @Override
    public void onBackPressed(){
        databaseReference.child(chatId).removeEventListener(childEventListener);
        firebaseFirestore.collection("Users").document(firebaseUser.getEmail())
                .collection("Chats").document(chatId).update("online", 0);
        startActivity(new Intent(ChatActivity.this, MainActivity.class));
    }
}