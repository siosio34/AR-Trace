package com.dragon4.owo.ar_trace.ARCore;

import android.content.Intent;
import android.util.Log;

import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.NaverMap.FragmentMapview;
import com.nhn.android.maps.maplib.NGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        final Intent naviBroadReceiver = new Intent();
        naviBroadReceiver.setAction("NAVI");

        loopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !MixState.enterNaviEnd) {
                    try {
                        NGeoPoint point = naverFragment.getCurrentLocation();
                        String url = DataSource.createNaverMapRequestURL(point.getLongitude(), point.getLatitude(), lon, lat);
                        String result = "";
                        String guide = "";
                        result = new HttpHandler().execute(url).get();
                        FragmentMapview.naverMapView.findAndDrawRoot(result);
                        guide = parsingNaverNaviJson(result);

                        if (!guide.equals("end")) {
                            // 브로드 캐스트 리시버로 전달하는 부분
                            naviBroadReceiver.putExtra("GUIDE", guide);
                            mixContext.sendBroadcast(naviBroadReceiver);
                        } else {
                            guide = "목적지에 가까워져 네비게이션이 자동종료됩니다.";
                            naviBroadReceiver.putExtra("GUIDE", guide);
                            mixContext.sendBroadcast(naviBroadReceiver);
                            loopThread.interrupt();
                        }
                        Thread.sleep(5000); // 5초마다 갱신

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        });
        loopThread.start();
    }

    private String parsingNaverNaviJson(String naviStirng) throws JSONException {
        String temp;
        JSONObject jObject = new JSONObject(naviStirng);
        int distance = jObject.getJSONObject("result").getJSONObject("summary").getInt("totalDistance");

        if(distance < 40) {
            temp = "end";
            return temp;
        }

        JSONArray jArray = jObject.getJSONObject("result").getJSONArray("route").getJSONObject(0).getJSONArray("point");
        JSONObject firstRoute = jArray.getJSONObject(1);

        if(firstRoute == null) {
            temp = "end";
            return temp;
        }

        else {
            temp = firstRoute.getJSONObject("guide").getString("name");
            Log.i("temp",temp);

        }
        return temp;
    }
}