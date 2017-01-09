package com.dragon4.owo.ar_trace.ARCore.data.DataProcessor;

import android.util.Log;

import com.dragon4.owo.ar_trace.ARCore.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.ARCore.Navi;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joyeongje on 2016. 12. 31..
 */

public class DataConvertor {

    private List<DataProcessor> dataProcessors = new ArrayList<DataProcessor>();
    private DataProcessor dataProcessor;

    public List<ARMarker> load(String rawResult, DataSource.DATASOURCE datasource, DataSource.DATAFORMAT dataformat) {

        dataProcessor = selectDataProcessor(dataformat);
        if(dataProcessor != null) {
            try {
                return dataProcessor.load(rawResult, datasource);
            }
            catch (JSONException e) {
                Log.i("JSON error", "DataConvertor class");
            }
        }
        return null;
    }

    public Navi load(String rawResult) {

        NaverNaviDataProcessor naviDataProcessor = new NaverNaviDataProcessor();
        try {
            return naviDataProcessor.load(rawResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DataProcessor selectDataProcessor(DataSource.DATAFORMAT dataformat) {
        DataProcessor selectedProcessor = null;
        switch (dataformat) {
            case NAVER_CATEGORY:
                selectedProcessor = new NaverCategoryDataProcessor();
                break;

            case NAVER_SEARCH:
                selectedProcessor = new NaverSearchDataProcessor();
                break;

            /*
            case NAVI:
                selectedProcessor = new NaverNaviDataProcessor();
                break;
                */

            case FIREBASE:
                selectedProcessor = new FirebaseDataProcessor();
                break;
        }
        return selectedProcessor;
    }



}

