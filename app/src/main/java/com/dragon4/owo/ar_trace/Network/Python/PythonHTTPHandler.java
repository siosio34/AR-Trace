package com.dragon4.owo.ar_trace.Network.Python;

import android.os.AsyncTask;
import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by joyeongje on 2017. 2. 6..
 */

public class PythonHTTPHandler extends AsyncTask<String, Void, String> {


    @Override
    protected String doInBackground(String... params) {

        String urlStr = params[0];
        String requestMethodParams = params[1];
        String requestString = params[2]; // 쿼리 값이나
        HttpURLConnection connection= null;

        String responseStr = "";

        try {

            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);

            if(requestMethodParams.equals("GET")) {
                connection.setRequestMethod("GET");
            }

            else if(requestMethodParams.equals("POST")) {
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                // 데이터 전송
                JSONObject traceObject = new JSONObject(requestString);
                OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                outputStream.write(traceObject.toString().getBytes());
                outputStream.flush();outputStream.close();

            }

            int ResponseCode = connection.getResponseCode();
            if(ResponseCode == HttpURLConnection.HTTP_OK) {
                // 정상 처리 되었을때..
                InputStream in = new BufferedInputStream(connection.getInputStream());
                responseStr = convertStreamToString(in);
                in.close();
            }

            connection.disconnect();


        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }


        return responseStr;
    }



    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(sb.toString(),Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(sb.toString()).toString();
        }

    }
}
