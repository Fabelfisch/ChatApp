package com.stuberf.chatapp;

import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.stuberf.chatapp.ui.login.ProfileActivity;

public class ChangeStatusActivity extends AppCompatActivity {

    EditText editText;
    Button saveButton;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);

        //initialize variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        editText = findViewById(R.id.changeStatus_editText);
        saveButton = findViewById(R.id.saveStatusButton);

        //get current status of the user
        firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(ChangeStatusActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if (snapshot != null) {
                    editText.setText(snapshot.get("status").toString());
                    saveButton.setEnabled(true);
                }
            }
        });
    }


    //save the new status to the firebase
    public void saveStatus(View view) {
        System.out.println(editText.getText());
        firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).update("status", editText.getText().toString()).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Intent intent = new Intent(ChangeStatusActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChangeStatusActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}