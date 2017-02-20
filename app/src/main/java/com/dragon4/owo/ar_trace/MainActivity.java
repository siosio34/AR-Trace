package com.dragon4.owo.ar_trace;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dragon4.owo.ar_trace.ARCore.Activity.GoogleSignActivity;
import com.dragon4.owo.ar_trace.Configure.ClientInstance;
import com.google.firebase.iid.FirebaseInstanceId;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.dragon4.owo.ar_trace.R.layout.activity_main);

        // Client 설정 - Firebase
        ClientInstance.setInstanceClient("FIREBASE");
        // Client 설정 - Python
        //ClientInstance.setInstanceClient("PYTHON");

        Intent SignInIntent = new Intent(MainActivity.this,GoogleSignActivity.class);
        startActivity(SignInIntent);
    }

}
