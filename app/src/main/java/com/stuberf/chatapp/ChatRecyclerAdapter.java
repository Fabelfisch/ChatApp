package com.stuberf.chatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stuberf.chatapp.ui.login.ProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.MenuItemHolder> {
    ArrayList<String> contacts;
    FirebaseFirestore firebaseFirestore;

    public ChatRecyclerAdapter(ArrayList<String> contacts) {
        this.contacts = contacts;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    @NonNull

    @Override
    public MenuItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate( R.layout.recycler_row, parent, false);
        return new MenuItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MenuItemHolder holder, final int position) {

        // We set the texts and the image of our MenuItemHolder object
        final String email = contacts.get(position);
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(email);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                }
                if (snapshot != null) {
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
                    };*/
                    if(user.getVisible().equals("yes")){
                        holder.nameText.setText(user.getName());
                        holder.emailText.setText(email);
                        holder.statusText.setText(user.getStatus());
                        //Picasso.get().load(user.getBitmapLink()).into(target);
                    }
                    else{
                        holder.nameText.setText("");
                        holder.statusText.setText("");
                        holder.emailText.setTextSize(20);
                        holder.emailText.setText(email);
                    }
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    /*
        "A ViewHolder describes an item view and metadata about its place within the RecyclerView."

        https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder
     */
    class MenuItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameText, emailText, statusText;
        LinearLayout linearLayout;

        public MenuItemHolder(@NonNull View itemView) {
            super(itemView);

            // links the attributes with the recycler_row items
            imageView = itemView.findViewById(R.id.recycler_row_imageView);
            nameText = itemView.findViewById(R.id.recycler_row_nameText);
            emailText = itemView.findViewById(R.id.recycler_row_emailText);
            statusText = itemView.findViewById(R.id.recycler_row_statusText);

            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
