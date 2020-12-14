//This is the user class. Users are locally handled as this class
package com.stuberf.chatapp;

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

    public String getBitmapLink() {
        return bitmapLink;
    }

    public String getVisible() {
        return visible;
    }

    public String getMail() {
        return mail;
    }

}
