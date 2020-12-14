//In the change name activity, the user is able to change his name
package com.stuberf.chatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stuberf.chatapp.ui.login.ProfileActivity;

import java.util.HashMap;

public class ChangeNameActivity extends AppCompatActivity {

    EditText editText;
    Button saveButton;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        //Initialize variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        editText = findViewById(R.id.changeName_editText);
        saveButton = findViewById(R.id.saveNameButton);

        //get current name from user in Database
        firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(ChangeNameActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if (snapshot != null) {
                    editText.setText(snapshot.get("name").toString());
                    saveButton.setEnabled(true);
                }
            }
        });
    }

    //save the new Name when the save button is pressed
    public void saveName(View view) {
        firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).update("name", editText.getText().toString()).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Intent intent = new Intent(ChangeNameActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChangeNameActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}