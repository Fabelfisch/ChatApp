package com.stuberf.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stuberf.chatapp.ui.login.ProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    private ChatRecyclerAdapter.RecyclerViewOnClickListener listener;

    ChatRecyclerAdapter chatRecyclerAdapter;
    final ArrayList<HashMap<String, Object>> chats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).collection("Chats").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(final QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                final HashMap<String, Object> chat = new HashMap<String, Object>();
                                chat.put("chatId", documentSnapshot.getId());
                                firebaseFirestore.collection("Chats").document(documentSnapshot.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot snapshot) {
                                        Object creator = snapshot.get("creator");
                                        if(creator!=null) {
                                            if (creator.toString().equals(firebaseUser.getEmail())) {
                                                Object user = snapshot.get("user");
                                                if(user!=null) {
                                                    chat.put("user", user.toString());
                                                }
                                                else{
                                                    chat.put("user", "Error loading user!");
                                                }
                                            } else {
                                                chat.put("user", creator.toString());
                                            }
                                            chats.add(chat);
                                        }
                                        else{
                                            chat.put("user", "Error loading user!");
                                        }
                                        fill();
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void fill(){
        setOnClickListener();
        RecyclerView recyclerView = findViewById(R.id.mainRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerAdapter = new ChatRecyclerAdapter(chats, listener);
        recyclerView.setAdapter(chatRecyclerAdapter);

    }
    private void setOnClickListener() {
        listener = new ChatRecyclerAdapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("Chat", chats.get(position).get("chatId").toString());
                startActivity(intent);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.addContact) {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void addChat(View view){
        startActivity(new Intent(MainActivity.this, AddChatActivity.class));
    }
}