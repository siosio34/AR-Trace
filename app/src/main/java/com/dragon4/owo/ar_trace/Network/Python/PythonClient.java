package com.dragon4.owo.ar_trace.Network.Python;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.dragon4.owo.ar_trace.ARCore.Activity.TraceRecyclerViewAdapter;
import com.dragon4.owo.ar_trace.FCM.FCMWebServerConnector;
import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.Model.User;
import com.dragon4.owo.ar_trace.Network.ClientSelector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

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

    private boolean isWorking = false;
    public boolean isWorking() {
        return isWorking;
    }

    public PythonClient() {
        gson =  new GsonBuilder().create();
        pythonServerUrl = "http://192.168.1.207:3033";
    }


    @Override
    public void uploadUserDataToServer(User currentUser, Context googleSignInContext){

        final String uploadTraceURL = pythonServerUrl + "/users/login";

        try {
            if(User.getMyInstance().getUserToken() != null)
                currentUser.setUserToken(User.getMyInstance().getUserToken());
            else
                currentUser.setUserToken(FirebaseInstanceId.getInstance().getToken());

            String response = new PythonHTTPHandler().execute(uploadTraceURL,"POST",gson.toJson(currentUser)).get();
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

        String pythonImageServerURL = "http://192.168.1.207:3030";

        final String uploadTraceURL = pythonServerUrl + "/upload";
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

        String uploadTraceURL = pythonServerUrl + "/reviews";

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
            JSONObject traceObj = new JSONObject(response);

            // TODO: 2017. 2. 6. 제이썬으로 바꾼뒤 배열에 추가하는 과정을 리턴해야된다

        } catch (InterruptedException | ExecutionException |JSONException e) {
            e.printStackTrace();
        }
        return traceList;
    }

    @Override
    public void getTraceLikeInformation(Trace trace, TraceRecyclerViewAdapter adapter, TraceRecyclerViewAdapter.TraceViewHolder reviewHolder) {

    }


    @Override
    public void sendTraceLikeToServer(final boolean isLikeClicked, Trace trace) {
        if(!isWorking) {
            isWorking = true;
            sendTraceLikeToPython(isLikeClicked,trace);

            if (isLikeClicked && trace.getUserId().compareTo(User.getMyInstance().getUserId()) != 0) {
                FCMWebServerConnector connector = new FCMWebServerConnector();
                connector.sendLikePush(trace);
            }
        }
    }

    private void sendTraceLikeToPython(final boolean isLikeClicked, final Trace trace) {

        final String traceLikeURL = pythonServerUrl;
        // TODO: 2017. 2. 16. python 좋아요 기능 구현

    }


    @Override
    public void getReviewNumberFromServer(String title, TextView reviewNumber) {


        //TODO: 2017-02-12 getReviewNumberFromServer 파이썬 버전
    }


}