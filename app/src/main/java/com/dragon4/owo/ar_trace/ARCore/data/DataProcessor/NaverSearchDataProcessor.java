package com.dragon4.owo.ar_trace.ARCore.data.DataProcessor;

import android.util.Log;

import com.dragon4.owo.ar_trace.ARCore.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.SocialARMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joyeongje on 2016. 12. 31..
 */

public class NaverSearchDataProcessor implements DataProcessor {

    @Override
    public List<ARMarker> load(String rawData, DataSource.DATASOURCE datasource) throws JSONException {

        List<ARMarker> markers = new ArrayList<ARMarker>();
        JSONObject root = convertToJSON(rawData);
        JSONArray dataArray = null;

        if(root.getJSONObject("result").getJSONObject("site").has("list")) {
            dataArray = root.getJSONObject("result").getJSONObject("site").getJSONArray("list");
        }

        if(dataArray == null) {
            Log.i("Naver Search Error",": data nothing");
            return null;
        }

        JSONObject getObject = null;
        ARMarker marker = null;

        for(int i = 0 ; i < dataArray.length() ; i++ ) {
            getObject = dataArray.getJSONObject(i);
            marker = processSearchJsonObject(getObject, datasource);

            if (marker != null) {
                markers.add(marker);
            }
        }
        return markers;
    }

    private ARMarker processSearchJsonObject(JSONObject jo, DataSource.DATASOURCE datasource) throws JSONException {

        ARMarker marker = null;
        String id = jo.getString("id");
        String naverWebLink = "http://map.naver.com/local/siteview.nhn?code=" + id.substring(1);

        marker = new SocialARMarker(jo.getString("name"),
                jo.getDouble("y"),
                jo.getDouble("x"),
                0,
                naverWebLink,
                datasource,
                datasource.toString());

        marker.setID(jo.getString("id"));
        return marker;

    }

    private JSONObject convertToJSON(String rawData){
        try {
            return new JSONObject(rawData);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


}
