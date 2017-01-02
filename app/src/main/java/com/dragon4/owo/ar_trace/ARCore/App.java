package com.dragon4.owo.ar_trace.ARCore;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by Mansu on 2017-01-02.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "NanumBarunpenB.ttf"))
                .addBold(Typekit.createFromAsset(this, "NanumBarunpenB.ttf"))
                .addItalic(Typekit.createFromAsset(this, "NanumBarunpenB.ttf"))
                .addBoldItalic(Typekit.createFromAsset(this, "NanumBarunpenB.ttf"));
    }
}