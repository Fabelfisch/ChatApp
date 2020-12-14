//This is the message class. Messages are locally handled as this class
package com.stuberf.chatapp;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Map;


//Message class based on https://medium.com/@mendhieemmanuel/building-real-time-android-chatroom-with-firebase-99a5b51cb4f7
public class Message {
    private String messageText;
    private String messageUserId;
    private long messageTime;

    public Message(String messageText, String messageUserId) {
        this.messageText = messageText;
        messageTime = new Date().getTime();
        this.messageUserId = messageUserId;
    }

    public Message(@NonNull Map<String, Object> result){
        this.messageUserId = result.get("messageUserId").toString();
        this.messageText = result.get("messageText").toString();
        this.messageTime = (long) result.get("messageTime");
    }


    public String getMessageText() {
        return messageText;
    }

    public String getMessageUserId() {
        return messageUserId;
    }

    public long getMessageTime() {
        return messageTime;
    }

}
