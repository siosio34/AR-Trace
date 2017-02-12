package com.dragon4.owo.ar_trace.Network.Python;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dragon4.owo.ar_trace.ARCore.Activity.TraceRecyclerViewAdapter;
import com.dragon4.owo.ar_trace.FCM.FCMWebServerConnector;
import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.Model.User;
import com.dragon4.owo.ar_trace.Network.ClientSelector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by joyeongje on 2017. 1. 20..
 */

public class PythonClient implements ClientSelector{

    private Gson gson;
    private String pythonServerUrl;

    public PythonClient() {
        gson =  new GsonBuilder().create();
        pythonServerUrl = "http://192.168.1.14:3331/";
    }


    @Override
    public void uploadUserDataToServer(User currentUser, Context googleSignInContext){

        final String uploadTraceURL = pythonServerUrl + "upload";

        pythonServerUrl = "";

        try {
            String response = new PythonHTTPHandler().execute(pythonServerUrl,"POST",gson.toJson(currentUser)).get();
            Log.i("uploadTraceInstance",response);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        final Intent loginReceiver = new Intent();
        loginReceiver.setAction("LOGIN_SUCCESS");
        googleSignInContext.sendBroadcast(loginReceiver);
    }

    @Override
    public void uploadImageToServer(Trace trace, final File file) {

        final String uploadTraceURL = pythonServerUrl + "upload";
        final String encodeFormat = "UTF-8";

        new Thread(new Runnable() {

            MultipartUtility multipartUtility = null;

            @Override
            public void run() {

                try {
                    multipartUtility = new MultipartUtility(uploadTraceURL,encodeFormat);
                    multipartUtility.addFilePart("file",new File(file.getPath()));

                    String response = multipartUtility.finish();

                    if(response != null)
                        Log.i("responseTest",response);
                    else
                        Log.i("responseTest","fail");

                    // 파라미터 추가가능
                    // multipartUtility.addFormField("key","value");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @Override
    public void uploadTraceToServer(Trace trace) {

        String uploadTraceURL = pythonServerUrl + "review";

        try {
            String response = new PythonHTTPHandler().execute(uploadTraceURL,"POST",gson.toJson(trace)).get();
            Log.i("uploadTraceInstance",response);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ArrayList<Trace> getTraceDataFromServer(String traceKey, TraceRecyclerViewAdapter mAdapter) {
        pythonServerUrl = "";
        // 하나의 장소에 대해서 리뷰들을 가져오는것.
        final ArrayList<Trace> traceList = new ArrayList<>();
        mAdapter.setList(traceList);

        try {

            String response = new PythonHTTPHandler().execute(pythonServerUrl, "GET").get();

            if(response.length() > 0) { }

            else
                return null;

            // TODO: 2017. 2. 6. 제이썬으로 바꾼뒤 배열에 추가하는 과정을 리턴해야된다

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        return traceList;
    }

    @Override
    public void sendTraceLikeToServer(final boolean isLikeClicked, Trace trace) {
        //TODO: 2017-02-11 sendLikeToServer 파이썬 버전
        if(isLikeClicked && trace.getUserId().compareTo(User.getMyInstance().getUserId()) != 0) {
            FCMWebServerConnector connector = new FCMWebServerConnector();
            connector.sendLikePush(trace);
        }
    }
}
