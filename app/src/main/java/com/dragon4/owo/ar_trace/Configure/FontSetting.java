package com.dragon4.owo.ar_trace.Configure;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by Mansu on 2017-01-02.
 */

public class FontSetting extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "NanumBarunpenR.ttf"))
                .addBold(Typekit.createFromAsset(this, "NanumBarunpenB.ttf"))
                .addItalic(Typekit.createFromAsset(this, "NanumBarunpenB.ttf"))
                .addBoldItalic(Typekit.createFromAsset(this, "NanumBarunpenB.ttf"));
    }
}
