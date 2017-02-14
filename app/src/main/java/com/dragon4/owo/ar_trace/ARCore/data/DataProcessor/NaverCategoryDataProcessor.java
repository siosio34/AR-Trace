package com.dragon4.owo.ar_trace.ARCore.data.DataProcessor;

import android.util.Log;

import com.dragon4.owo.ar_trace.ARCore.Marker.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.Marker.SocialARMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joyeongje on 2016. 12. 31..
 */

public class NaverCategoryDataProcessor implements DataProcessor{

    private String deleteSpecialCharacter(String str) {
        // TODO: 2017. 2. 14. 다른 특수문자 있을경우 생각해야됨.
        return str.replace("."," ");
    }


    @Override
    public List<ARMarker> load(String rawData, DataSource.DATASOURCE datasource) throws JSONException {

        List<ARMarker> markers = new ArrayList<ARMarker>();
        JSONObject root = convertToJSON(rawData);
        JSONArray dataArray = null;

        if (root.has("result") && root.getJSONObject("result").has("site")) // 연결 가능한 링크를 가졌을시
            dataArray = root.getJSONObject("result").getJSONArray("site");


        else if(root.has("result") && root.getJSONObject("result").has("station") ) { // 버스, 역 마커일경우 따로 처리해줘야됨
            dataArray = root.getJSONObject("result").getJSONArray("station");
        }


        if(dataArray == null) {
            Log.i("Naver Category Error",": data nothing");
            return null;
        }
        JSONObject getObject = null;
        ARMarker marker = null;

        for(int i = 0 ; i < dataArray.length() ; i++ ) {
            getObject = dataArray.getJSONObject(i);
            if (datasource != DataSource.DATASOURCE.BUSSTOP) {
                marker = processCategoryJsonObject(getObject, datasource);
            } else {
                marker = processBusJsonObject(getObject, datasource);
                Log.i("marker Count", marker.getTitle());
            }
            if (marker != null) {
                markers.add(marker);
            }
        }

        return markers;
    }

    public ARMarker processCategoryJsonObject(JSONObject jo, DataSource.DATASOURCE datasource) throws JSONException {
        ARMarker marker = null;

        String title = jo.getString("name");
        title = deleteSpecialCharacter(title);

        String id = jo.getString("id");
        String naverWebLink = "http://map.naver.com/local/siteview.nhn?code=" + id.substring(1);

        marker = new SocialARMarker(title,
                jo.getDouble("y"),
                jo.getDouble("x"),
                0,
                naverWebLink,
                datasource,
                datasource.toString());


        marker.setID(jo.getString("id"));
        return marker;
    }

    public ARMarker processBusJsonObject(JSONObject jo, DataSource.DATASOURCE datasource) throws JSONException {
        ARMarker marker = null;

        String title = jo.getString("stationDisplayName");
        title = deleteSpecialCharacter(title);

        String link = jo.getString("stationDisplayID");
        String[] sTemp = link.split("-");
        link = "";
        String webBusLink;

        for(int i =0 ; i<sTemp.length; i++) {
            link += sTemp[i];
        }

        webBusLink = "http://lab.khlug.org/manapie/bus_arrival.php?station=" + link;
        Log.i("webBusLink",webBusLink);

        marker = new SocialARMarker(title,
                jo.getDouble("y"),
                jo.getDouble("x"),
                0,
                webBusLink,
                datasource,
                datasource.toString());

        marker.setID(jo.getString("stationID"));
        return marker;
    }



    private JSONObject convertToJSON(String rawData){
        try {
            Log.i("rawdata",rawData);
            return new JSONObject(rawData);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
