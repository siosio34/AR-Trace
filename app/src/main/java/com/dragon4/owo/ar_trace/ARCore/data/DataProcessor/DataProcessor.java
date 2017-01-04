package com.dragon4.owo.ar_trace.ARCore.data.DataProcessor;

import com.dragon4.owo.ar_trace.ARCore.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;

import org.json.JSONException;

import java.util.List;

/**
 * Created by joyeongje on 2016. 12. 31..
 */

public interface DataProcessor {
    List<ARMarker> load(String rawData, DataSource.DATASOURCE datasource) throws JSONException;

}
