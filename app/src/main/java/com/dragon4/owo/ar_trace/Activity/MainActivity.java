package com.dragon4.owo.ar_trace.Activity;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dragon4.owo.ar_trace.*;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 8001;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_READ = 8002;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_WRITE = 8003;
    Intent permissionIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.dragon4.owo.ar_trace.R.layout.activity_main);
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // 지피에스 권한 요청
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            }
        } else { // 23 이하인 경우는
            // TODO: 2016. 12. 29. 권한 체크 안하고 바로 넘어가도됨
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 사용자가 승낙햇을때.
                    // TODO: 2016. 12. 29. 다음 화면으로 넘어가게 처리해야됨
                } else {

                }
                return;
            }
        }
    }
}
