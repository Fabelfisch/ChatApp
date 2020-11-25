package com.stuberf.chatapp;

import android.webkit.HttpAuthHandler;

import java.util.Date;
import java.util.HashMap;
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

    public Message(Map<String, Object> result){
        this.messageUserId = result.get("messageUserId").toString();
        this.messageText = result.get("messageText").toString();
        this.messageTime = (long) result.get("messageTime");
    }

    public HashMap<String, Object> getHash(){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("messageUserId", messageUserId);
        hashMap.put("messageText", messageText);
        hashMap.put("messageTime", messageTime);
        return hashMap;
    }


    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(String messageUserId) {
        this.messageUserId = messageUserId;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
