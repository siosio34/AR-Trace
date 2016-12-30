package com.dragon4.owo.ar_trace.Util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by joyeongje on 2016. 12. 29..
 */

/*
public class LocationManagerUtil {

    private LocationManager lm;
    private String bestLocationProvider;
    private final MixContext mixContext;
    private Location curLocation;
    private Location lastCurrentLocation; // 마지막까지 있던 곳

    public LocationManagerUtil(MixContext mixContext) {
        this.mixContext = mixContext;
        lm = (LocationManager) mixContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public void searchGPSLocation() { // 처음 지피에서 탐색

        // 초기화
        Location tempLocation = new Location("reverseGeocoded");
        tempLocation.setLatitude(37.33);
        tempLocation.setLongitude(126.58);
        tempLocation.setAltitude(300);

        try {
            requestBestLocationUpdates();
            //temporary set the current location, until a good provider is found
            if (ActivityCompat.checkSelfPermission(mixContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 만약 권한 체크 안한거면 여기서 종료
                // TODO: 2016. 12. 29. 권한체크 한거 여기서 언급해야됨
                return;
            }
            curLocation = lm.getLastKnownLocation(lm.getBestProvider(new Criteria(), true));

        } catch (Exception ex2) {
            // ex2.printStackTrace();
            curLocation = tempLocation;
            // TODO: 2016. 12. 29. 지피에스 못가져왓다는 알림창 띄우기

        }

    }

    private void requestBestLocationUpdates() {
        Criteria reqCriteria = new Criteria();
        reqCriteria.setAccuracy(Criteria.ACCURACY_FINE); // 정확성높게
        reqCriteria.setAltitudeRequired(true); // 고도값도 받게하자

        LocationManagerUtilListener locationListner = new LocationManagerUtilListener(lm,mixContext);

        //LocationManagerUtilListener newLocationListener = new LocationManagerUtilListener(lm,mixContext);
        lm.requestLocationUpdates(reqCriteria, 0, 0, locationListner);
    }



   // public void locationCallback(String provider) {
   //     Location foundLocation = lm.getLastKnownLocation(provider);
   //     if (bestLocationProvider != null) {
   //         Location bestLocation = lm
   //                 .getLastKnownLocation(bestLocationProvider);
   //         if (foundLocation.getAccuracy() < bestLocation.getAccuracy()) {
   //             curLoc = foundLocation;
   //             bestLocationProvider = provider;
   //         }
   //     } else {
   //         curLoc = foundLocation;
   //         bestLocationProvider = provider;
   //     }
   //     setLocationAtLastDownload(curLoc);
   // }

}

*/