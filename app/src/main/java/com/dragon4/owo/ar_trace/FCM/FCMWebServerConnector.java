package com.dragon4.owo.ar_trace.FCM;

import android.content.res.Resources;
import android.util.Log;

import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.Model.User;
import com.dragon4.owo.ar_trace.Network.Python.PythonHTTPHandler;
import com.dragon4.owo.ar_trace.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Mansu on 2017-02-11.
 */

public class FCMWebServerConnector {
    private Gson gson;
    private String webServerUrl;

    public FCMWebServerConnector() {
        //String webServerIP = "http://163.180.117.118/";
        webServerUrl = Resources.getSystem().getString(R.string.PUSH_SERVER_IP) + "PushServer.php";
        gson = new GsonBuilder().create();

    }

    public void sendLikePush(Trace trace) {
        try {
            //webServerUrl = "http://163.180.117.118/PushServer.php";
            JSONObject obj = new JSONObject();
            obj.put("userToken", trace.getUserToken());
            obj.put("userID", User.getMyInstance().getUserId());
            obj.put("userName", User.getMyInstance().getUserName());
            obj.put("buildingID", trace.getLocationID());
            obj.put("traceID", trace.getTraceID());
            obj.put("title", trace.getPlaceName());

            String response = new FCMHttpHandler().execute(webServerUrl,"POST",gson.toJson(obj)).get();
            Log.i("sendLikePush", response);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}