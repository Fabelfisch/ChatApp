package com.stuberf.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.stuberf.chatapp.ui.login.ProfileActivity;

import java.util.HashMap;
import java.util.Map;

public class AddContactActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private EditText email;
    private Button addButton;
    private ProgressBar progressBar;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        //initialize variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        email = findViewById(R.id.contactEmail_editText);
        addButton = findViewById(R.id.addContactButton);
        progressBar = findViewById(R.id.progressBarContact);

        //check  if the inserted email is a valid email adress before allowing to press the add button
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(email.getText().toString().trim().matches(emailPattern)){
                    addButton.setEnabled(true);
                }
            }
        });

    }

    //add the contact if the user hasn't already added it
    public void addContact(View view){
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference doc = firebaseFirestore.collection("Users").document(email.getText().toString());
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    final HashMap<String, Object> map = new HashMap<String, Object>();
                    final DocumentReference contact = firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).collection("Contacts").document(email.getText().toString());
                    contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot data = task.getResult();
                            if(!data.exists()){
                                contact.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AddContactActivity.this, "Contact added!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(AddContactActivity.this, MainActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AddContactActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(AddContactActivity.this, "This user is already a contact of yours!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddContactActivity.this, "There is no user with this email!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //override of standard back button
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
        startActivity(intent);
    }
}