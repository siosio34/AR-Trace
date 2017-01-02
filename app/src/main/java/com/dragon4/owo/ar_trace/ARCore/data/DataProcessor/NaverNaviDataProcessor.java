package com.dragon4.owo.ar_trace.ARCore.data.DataProcessor;

import com.dragon4.owo.ar_trace.ARCore.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by joyeongje on 2016. 12. 31..
 */

public class NaverNaviDataProcessor implements DataProcessor {
    @Override
    public List<ARMarker> load(String rawData, DataSource.DATASOURCE dataformat) throws JSONException {
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
