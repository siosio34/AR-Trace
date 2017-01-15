package com.dragon4.owo.ar_trace.ARCore;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Mansu on 2016-12-08.
 */

public class FileUploadDialog extends ProgressDialog {
    public FileUploadDialog(Context context) {
        super(context);
    }

    public FileUploadDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void show() {
        setMessage("Uploading...");
        setCanceledOnTouchOutside(false);
        setIndeterminate(false);
        setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        setProgress(0);
        super.show();
    }
}