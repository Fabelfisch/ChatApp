package com.stuberf.chatapp;

import android.graphics.Bitmap;

public class Singleton {
    private Bitmap image;
    private static Singleton singleton;

    private Singleton() {

    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap chosenFoodImage) {
        this.image = chosenFoodImage;
    }

    public static Singleton getInstance() {
        if(singleton == null) {
            singleton = new Singleton();
        }
        return singleton;
    }
}

