//this activity gets called whenever there is no internet connection and just displays this info
package com.stuberf.chatapp;
//haveNetwork function from https://www.tutorialspoint.com/how-to-check-internet-connection-availability-on-android
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import com.stuberf.chatapp.ui.login.LoginActivity;

public class NoConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
    }

    //if the retry button is clicked, the connection is checked again and if everything is alright, the user can proceed
    public void retry(View view){
        if(haveNetwork()){
            startActivity(new Intent(NoConnectionActivity.this, LoginActivity.class));
        }
    }

    private boolean haveNetwork(){
        boolean have_WIFI= false;
        boolean have_MobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo info:networkInfos){
            if (info.getTypeName().equalsIgnoreCase("WIFI"))if (info.isConnected())have_WIFI=true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE DATA"))if (info.isConnected())have_MobileData=true;
        }
        return have_WIFI||have_MobileData;
    }
}