package com.dragon4.owo.ar_trace.ARCore.data.DataProcessor;

import android.util.Log;

import com.dragon4.owo.ar_trace.ARCore.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.NavigationMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by joyeongje on 2016. 12. 31..
 */

public class NaverNaviDataProcessor implements DataProcessor {

    int totalTime;
    int totalDistance;
    //String startPoint;
    //String endPoint;

    public List<ARMarker> load(String rawData, DataSource.DATASOURCE datasource) throws JSONException {

        List<ARMarker> markers = new ArrayList<ARMarker>();
        JSONObject root = convertToJSON(rawData);
        JSONArray dataArray = null;
        JSONArray pointArray = null;

        //String startPoint = root.getJSONObject("startPoint").getString("name");
        //String endPoint = root.getJSONObject("endPoint").getString("name");
        dataArray = root.getJSONObject("result").getJSONArray("route");
        totalTime = root.getJSONObject("result").getJSONObject("summary").getInt("totalTime");
        totalDistance = root.getJSONObject("result").getJSONObject("summary").getInt("totalDistance");

        if(dataArray == null) {
            Log.i("naverNaviData Error", ":data nothing");
            return null;
        }
        pointArray = dataArray.getJSONObject(0).getJSONArray("point");

        JSONObject getObject = null;
        NavigationMarker marker = null;

        for(int i = 0; i<pointArray.length();i++) {

            getObject = pointArray.getJSONObject(i);
            marker = processNaviJsonObject(getObject,datasource);

            if (marker != null) {
                markers.add(marker);
            }
        }

        return markers;
    }

    private NavigationMarker processNaviJsonObject(JSONObject jo,DataSource.DATASOURCE datasource) throws JSONException {

        NavigationMarker marker = null;

        marker = new NavigationMarker(jo.getJSONObject("guide").getString("name"),
                jo.getInt("y"),
                jo.getInt("x"),
                0,
                null,
                datasource
                );

        String route = getRoute(jo.getJSONObject("guide").getString("pinLabel"));
        marker.setType(route);
        marker.setTime(totalTime);
        marker.setTotalDistance(totalDistance);

        return marker;
    }

    private String getRoute(String guide) {
        if(guide.contains("왼쪽")) return "left";
        else if(guide.contains("오른쪽")) return "right";
        else if(guide.contains("횡단")) return "road;";
        else if(guide.contains("이동")) return "straight";
        else return "spot"; // 시작점 끝점
    }

    private JSONObject convertToJSON(String rawData){
        try {
            return new JSONObject(rawData);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


}
