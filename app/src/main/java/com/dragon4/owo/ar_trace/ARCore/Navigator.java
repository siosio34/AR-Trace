package com.dragon4.owo.ar_trace.ARCore;

import android.content.Intent;
import android.util.Log;

import com.dragon4.owo.ar_trace.ARCore.Marker.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataProcessor.DataConvertor;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.NaverMap.FragmentMapview;
import com.nhn.android.maps.maplib.NGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mansu on 2017-01-26.
 */

public class Navigator {
    private static Navigator navigator;
    private Thread loopThread;
    private MixContext mixContext;
    private FragmentMapview naverFragment;

    public static Navigator getNavigator() {
        if(navigator != null)
            return navigator;
        else
            return null;
    }

    public Navigator(MixContext mixContext, FragmentMapview naverFragment) {

        if(navigator == null)
            navigator = this;

        this.mixContext = mixContext;
        this.naverFragment = naverFragment;
    }

    public void run(final double lat, final double lon) {

        //naverFragment.clearCategoryMarker(); // 마커 삭제 !

        loopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !MixState.enterNaviEnd) {
                    try {
                        NGeoPoint point = naverFragment.getCurrentLocation();
                        String url = DataSource.createNaverMapRequestURL(point.getLongitude(), point.getLatitude(), lon, lat);
                        Log.i("Navi URL",url);
                        String result = new NaverHttpHandler().execute(url).get();

                        // TODO: 2017. 2. 21. 최적화필요.
                        updateNaviStatus(result); // 네비게이션 가이드.
                        naverFragment.findAndDrawRoot(result); // 네이버 지도 갱신.

                        //updateNavimap(result);

                        Thread.sleep(5000);

                    } catch (ExecutionException | JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        });
        loopThread.start();
    }

    private void updateNaviStatus(String result) throws JSONException {

        //List<ARMarker> naviList = null;
        //final DataConvertor dataConvertor = new DataConvertor();
        //naviList =  dataConvertor.load(result, DataSource.DATASOURCE.NAVI, DataSource.DATAFORMAT.NAVI);

        final Intent naviBroadReceiver = new Intent();
        naviBroadReceiver.setAction("NAVI");

        String guide = parsingNaverNaviJson(result);

        if(guide.equals("END")) {
            loopThread.interrupt();
        }
        
        naviBroadReceiver.putExtra("GUIDE", guide);
        mixContext.sendBroadcast(naviBroadReceiver);
        
    }

    private String parsingNaverNaviJson(String naviStirng) throws JSONException {
        String guide;
        JSONObject jObject = new JSONObject(naviStirng);
        int distance = jObject.getJSONObject("result").getJSONObject("summary").getInt("totalDistance");

        JSONArray jArray = jObject.getJSONObject("result").getJSONArray("route").getJSONObject(0).getJSONArray("point");
        JSONObject firstRoute = jArray.getJSONObject(1);

        if(distance < 40 || firstRoute == null) { //  거리가 얼마 안남았을때
            guide = "END";
        }

        else {
            guide = firstRoute.getJSONObject("guide").getString("name");
        }
        return guide;
    }
}