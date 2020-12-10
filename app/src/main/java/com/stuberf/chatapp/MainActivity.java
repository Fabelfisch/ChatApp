package com.stuberf.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stuberf.chatapp.ui.login.LoginActivity;
import com.stuberf.chatapp.ui.login.ProfileActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    private ChatRecyclerAdapter.RecyclerViewOnClickListener listener;

    ChatRecyclerAdapter chatRecyclerAdapter;
    final ArrayList<HashMap<String, Object>> chats = new ArrayList<>();
    private TextView toolbarText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize variables
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        progressBar = findViewById(R.id.loadingChats);
        progressBar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        toolbarText = toolbar.findViewById(R.id.toolbarText);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //get all chats from the current user
        DocumentReference doc = firebaseFirestore.collection("Users").document(firebaseUser.getEmail());
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                toolbarText.setText(task.getResult().get("name").toString());
            }
        });
        doc.collection("Chats").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(final QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                final HashMap<String, Object> chat = new HashMap<String, Object>();
                                chat.put("chatId", documentSnapshot.getId());
                                chat.put("unread",documentSnapshot.get("unread").toString());
                                firebaseFirestore.collection("Chats").document(documentSnapshot.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot snapshot) {
                                        Object creator = snapshot.get("creator");
                                        if(creator!=null) {
                                            //add for each chat the two users and a timestamp of the last change
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
                                            chat.put("time",snapshot.get("time"));
                                            chats.add(chat);
                                        }
                                        else{
                                            chat.put("user", "Error loading user!");
                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            chats.sort(new MyComparator());
                                        }
                                        fill();
                                    }
                                });
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    //function to fill the chat recycler adapter
    private void fill(){
        progressBar.setVisibility(View.GONE);
        setOnClickListener();
        RecyclerView recyclerView = findViewById(R.id.mainRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerAdapter = new ChatRecyclerAdapter(chats, listener);
        recyclerView.setAdapter(chatRecyclerAdapter);

    }

    //Enables user to select a chat
    private void setOnClickListener() {
        listener = new ChatRecyclerAdapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("Chat", chats.get(position).get("chatId").toString());
                intent.putExtra("User", chats.get(position).get("user").toString());
                startActivity(intent);
            }
        };
    }

    //Implementation of the Toolbar menu
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
        if(item.getItemId() == R.id.logout){
            firebaseAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    //Starts the add chat activity to add a chat
    public void addChat(View view){
        startActivity(new Intent(MainActivity.this, AddChatActivity.class));
    }

    //comparator class to compare the chats in respect to the latest change
    class MyComparator implements Comparator<HashMap<String, Object>>{

        @Override
        public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
            if(Long.parseLong(o1.get("time").toString())<Long.parseLong(o2.get("time").toString())){
                return 1;
            }
            return -1;
        }
    }

    //refresh the main activity
    public void refresh(View view){
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
}