package com.stuberf.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class AddChatActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private ChatAddRecyclerAdapter.RecyclerViewOnClickListener listener;

    ChatAddRecyclerAdapter chatAddRecyclerAdapter;
    final ArrayList<String> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).collection("Contacts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                contacts.add(documentSnapshot.getId());
                            }
                            fill();
                        }
                    }
                });

    }

    private void fill(){
        setOnClickListener();
        RecyclerView recyclerView =findViewById(R.id.addChatRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAddRecyclerAdapter = new ChatAddRecyclerAdapter(contacts, listener);
        recyclerView.setAdapter(chatAddRecyclerAdapter);

    }

    private void setOnClickListener() {
        listener = new ChatAddRecyclerAdapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                addChat(contacts.get(position));
            }
        };
    }

    public void addChat(final String email){
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String id1 = email+firebaseUser.getEmail();
        final String id2 = firebaseUser.getEmail()+email;
        final DocumentReference doc = firebaseFirestore.collection("Chats").document(id1);
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    Toast.makeText(AddChatActivity.this, "You have already a chat with this person!", Toast.LENGTH_LONG).show();
                }
                else{
                    DocumentReference doc2 = firebaseFirestore.collection("Chats").document(id2);
                    doc2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value.exists()) {
                                Toast.makeText(AddChatActivity.this, "You have already a chat with this person!", Toast.LENGTH_LONG).show();
                            }
                            else{
                                add(id1, email);
                            }
                        }
                    });
                }
            }
        });

    }


    private void add(final String id, final String email){
        HashMap<String, Object> users = new HashMap<String, Object>();
        users.put("creator", firebaseUser.getEmail());
        users.put("user", email);
        firebaseFirestore.collection("Chats").document(id).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(final Void aVoid) {
                final HashMap<String, Object> hashMap = new HashMap<String, Object>();
                firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).collection("Chats").document(id).set(hashMap).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseFirestore.collection("Users").document(email).collection("Chats").document(id).set(hashMap).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println(e.getLocalizedMessage());
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddChatActivity.this, "Chat created!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(AddChatActivity.this, MainActivity.class));
                            }
                        });
                    }
                });
            }
        });
    }
}