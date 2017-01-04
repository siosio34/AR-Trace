package com.dragon4.owo.ar_trace.ARCore.data.DataProcessor;

import com.dragon4.owo.ar_trace.ARCore.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.Model.Navi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by joyeongje on 2016. 12. 31..
 */

public class NaverNaviDataProcessor {

    public Navi load(String rawData) throws JSONException {

        JSONObject root = convertToJSON(rawData);
        JSONArray dataArray = null;

        return null;
    }



    private JSONObject convertToJSON(String rawData){
        try {
            return new JSONObject(rawData);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


}
