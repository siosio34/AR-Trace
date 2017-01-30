package com.dragon4.owo.ar_trace.ARCore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dragon4.owo.ar_trace.ARCore.Activity.GoogleSignActivity;
import com.dragon4.owo.ar_trace.R;


public class MixareActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout
                .activity_armain);

        Intent arIntent = new Intent(MixareActivity.this,GoogleSignActivity.class);
        startActivity(arIntent);

    }
}