package com.stuberf.chatapp;
//Recycler class based on https://medium.com/@mendhieemmanuel/building-real-time-android-chatroom-with-firebase-99a5b51cb4f7

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.MessageItemHolder> {

    String userId;
    private final int MESSAGE_IN_VIEW_TYPE  = 1;
    private final int MESSAGE_OUT_VIEW_TYPE = 2;
    private ArrayList<Map<String, Object>> messages;

    public MessageRecyclerAdapter(ArrayList<Map<String,Object>> messages, String userId){
        this.messages = messages;
        this.userId = userId;
    }

    @Override
    public int getItemViewType(int position) {
        //if message userId matches current userid, set view type 1 else set view type 2
        if (userId.equals(new Message(messages.get(position)).getMessageUserId())) {
            return MESSAGE_OUT_VIEW_TYPE;
        }

        return MESSAGE_IN_VIEW_TYPE;
    }

    @NonNull
    @Override
    public MessageItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
        We're using two different layouts. One for messages from others and the other for user's messages
         */
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType==MESSAGE_IN_VIEW_TYPE){
            view = layoutInflater.inflate(R.layout.mssg, parent, false);
        }
        else{
            view = layoutInflater.inflate(R.layout.mssg_out, parent, false);
        }
        return new MessageItemHolder(view);
    }


    public void onBindViewHolder(@NonNull MessageItemHolder holder, int position) {
        //Bind values from Message to the viewHolder
        Message message = new Message(messages.get(position));
        holder.mText.setText(message.getMessageText());
        holder.mTime.setText(DateFormat.format("dd MMM  (h:mm a)", message.getMessageTime()));
        if (userId.equals(message.getMessageUserId())) {
            holder.layout.setBackgroundResource(R.drawable.box_ui);
            holder.mUser.setText("you");
        } else {
            holder.layout.setBackgroundResource(R.drawable.box_ui_in);
            holder.mUser.setText(message.getMessageUserId());
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageItemHolder extends RecyclerView.ViewHolder{

        TextView mText;
        TextView mTime;
        TextView mUser;
        ConstraintLayout layout;
        public MessageItemHolder(@NonNull View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.messageTextView);
            mTime = itemView.findViewById(R.id.dateTextView);
            mUser = itemView.findViewById(R.id.usernameTextView);

            layout = itemView.findViewById(R.id.mssgConstraint);
        }
    }
}
