//In the profile a user can modify his profile
package com.stuberf.chatapp.ui.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.stuberf.chatapp.ChangeImageActivity;
import com.stuberf.chatapp.ChangeNameActivity;
import com.stuberf.chatapp.ChangeStatusActivity;
import com.stuberf.chatapp.MainActivity;
import com.stuberf.chatapp.R;
import com.stuberf.chatapp.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextView nameTextView, statusTextView;
    private ImageView imageView;
    private Button buttonBack;
    private CheckBox visibilityCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initialize variables
        imageView = findViewById(R.id.imageView);
        nameTextView = findViewById(R.id.textViewName);
        statusTextView = findViewById(R.id.textViewStatus);
        visibilityCheckBox = findViewById(R.id.checkBoxVisibility);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //get User data from database
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseUser.getEmail());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if(error!= null) {
                    Toast.makeText(ProfileActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if(snapshot!=null){
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getData();

                    User user = new User(data);
                    /*Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            imageView.setImageBitmap(bitmap);
                        }
                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            e.printStackTrace();
                            System.out.println("here!");
                        }
                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    };
                    Picasso.get().load(user.getBitmapLink()).into(target);*/
                    nameTextView.setText(user.getName());
                    statusTextView.setText(user.getStatus());
                    visibilityCheckBox.setChecked(user.getVisible().equals("yes"));
                }
            }

        });

        //enable change of attributes
        /*imageView.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent intent = new Intent(ProfileActivity.this, ChangeImageActivity.class);
                                             startActivity(intent);
                                         }
                                     }
        );*/
        statusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ChangeStatusActivity.class));
            }
        });
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ChangeNameActivity.class));
            }
        });
        visibilityCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).update("visible", "yes");
                    visibilityCheckBox.setChecked(true);
                }
                else{
                    firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).update("visible", "no");
                    visibilityCheckBox.setChecked(false);
                }
            }
        });
    }

    //Override of standard back pressed function such that the Main activity gets restarted
    @Override
    public void onBackPressed(){
        if(nameTextView.getText().toString().equals("No name set")){
            Toast.makeText(ProfileActivity.this, "You have to insert your name!", Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

}