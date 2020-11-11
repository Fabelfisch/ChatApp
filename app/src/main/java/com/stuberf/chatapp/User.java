package com.stuberf.chatapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import com.stuberf.chatapp.MainActivity;
import com.stuberf.chatapp.R;
import com.stuberf.chatapp.Singleton;
import com.stuberf.chatapp.ui.login.LoginActivity;

import java.util.HashMap;

public class User {
    private String mail, name = "No name set", status = "No status", bitmapLink = "gs://chatapp-3ce15.appspot.com/Avatar.bmp", visible="no";

    public User(String mail){
        this.mail = mail;
    }

    public User(HashMap hashMap){
        this.mail = hashMap.get("mail").toString();
        this.name = hashMap.get("name").toString();
        this.visible = hashMap.get("visible").toString();
        this.status = hashMap.get("status").toString();
        this.bitmapLink = hashMap.get("bitmapLink").toString();
    }
    public HashMap<String, Object> getHashMap(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("mail", this.mail);
        hashMap.put("name", this.name);
        hashMap.put("status", this.status);
        hashMap.put("bitmapLink", bitmapLink);
        hashMap.put("visible", visible);
        return hashMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBitmapLink() {
        return bitmapLink;
    }

    public void setBitmapLink(String bitmapLink) {
        this.bitmapLink = bitmapLink;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
