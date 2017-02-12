package com.dragon4.owo.ar_trace.NaverMap;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import com.dragon4.owo.ar_trace.ARCore.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.R;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapContext;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joyeongje on 2016. 12. 31..
 */

public class FragmentMapview extends Fragment {
    private static final String LOG_TAG = "NMapViewer";
    private static final boolean DEBUG = false;

    public static FragmentMapview naverMapView;

    private LinearLayout mMapContainer;
    private NMapContext mMapContext;
    private NMapView mMapView;
    private NMapController mMapController;
    private NMapLocationManager mMapLocationManager;
    private NMapMyLocationOverlay mMyLocationOverlay;
    private NMapOverlayManager mOverlayManager;
    private NMapCompassManager mMapCompassManager;
    private NMapViewerResourceProvider mMapViewerResourceProvider;
    private NGeoPoint currentLocation;

    private static final String CLIENT_ID = "FUYe3rcT2vNtJtk4aoK2";// 애플리케이션 클라이언트 아이디 값

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        naverMapView = this;
        return inflater.inflate(R.layout.fragment_map_view, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapContainer = (LinearLayout)getActivity().findViewById(R.id.ar_mixview_naverview);

        mMapContext =  new NMapContext(super.getActivity());
        mMapContext.onCreate();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //네이버 지도객체 생성
        mMapView = (NMapView)getView().findViewById(R.id.mapView);
        mMapContext.setupMapView(mMapView);

        //clientid 등록
        mMapView.setClientId(CLIENT_ID);// 클라이언트 아이디 설정

        //setClickable true일 시, 지도에 클릭이벤트 전부 작동
        mMapView.setClickable(true);

        //지도 객체로부터 컨트롤러 추출
        mMapController = mMapView.getMapController();

        // 네이버 지도 객체에 APIKEY 지정
        //mMapView.setApiKey(API_KEY);

        // 확대/축소를 위한 줌 컨트롤러 표시 옵션 활성화
        mMapContext.setMapDataProviderListener(onDataProviderListener);

        // 지도에 대한 상태 변경 이벤트 연결
        mMapView.setOnMapStateChangeListener(onMapStateChangeListener);
    }

    private final NMapView.OnMapStateChangeListener onMapStateChangeListener = new NMapView.OnMapStateChangeListener() {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
            if(nMapError == null)
                startMyLocation();
            else
                android.util.Log.e("NMAP", "onMapInitHandler: error=" + nMapError.toString());
        }

        @Override
        public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {

        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int i) {

        }

        @Override
        public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

        }
    };

    private final NMapActivity.OnDataProviderListener onDataProviderListener = new NMapActivity.OnDataProviderListener() {

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

            if (errInfo != null) {
                Log.e("myLog", "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());
                Toast.makeText(getContext(), errInfo.toString(), Toast.LENGTH_LONG).show();
                return;
            }else{
               // Toast.makeText(getContext(), placeMark.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };

    private void startMyLocation() {
        mMapViewerResourceProvider = new NMapViewerResourceProvider(getContext());

        // create overlay manager
        mOverlayManager = new NMapOverlayManager(getContext(), mMapView, mMapViewerResourceProvider);
        // register callout overlay listener to customize it.
        mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);
        // register callout overlay view listener to customize it.
        mOverlayManager.setOnCalloutOverlayViewListener(onCalloutOverlayViewListener);

        //compass manager
        mMapCompassManager = new NMapCompassManager(getActivity());

        //location Manager
        mMapLocationManager = new NMapLocationManager(getContext());
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        if(mMyLocationOverlay != null) {
            if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
                mOverlayManager.addOverlay(mMyLocationOverlay);
			}

			if (mMapLocationManager.isMyLocationEnabled()) {
				if (!mMapView.isAutoRotateEnabled()) {
					mMyLocationOverlay.setCompassHeadingVisible(true);

					mMapCompassManager.enableCompass();

					mMapView.setAutoRotateEnabled(true, false);
					mMapContainer.requestLayout();
				} else {
					stopMyLocation();
				}

				mMapView.postInvalidate();
			} else {
                boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
                if (!isMyLocationEnabled) {
                    Toast.makeText(getContext(), "Please enable a My Location source in system settings", Toast.LENGTH_LONG).show();
                    Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(goToSettings);
                    return;
                }
			}
        }
    }

    private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {

        @Override
        public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem,
                                                         Rect itemBounds) {

            // handle overlapped items
            if (itemOverlay instanceof NMapPOIdataOverlay) {
                NMapPOIdataOverlay poiDataOverlay = (NMapPOIdataOverlay)itemOverlay;

                // check if it is selected by touch event
                if (!poiDataOverlay.isFocusedBySelectItem()) {
                    int countOfOverlappedItems = 1;

                    NMapPOIdata poiData = poiDataOverlay.getPOIdata();
                    for (int i = 0; i < poiData.count(); i++) {
                        NMapPOIitem poiItem = poiData.getPOIitem(i);

                        // skip selected item
                        if (poiItem == overlayItem) {
                            continue;
                        }

                        // check if overlapped or not
                        if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
                            countOfOverlappedItems++;
                        }
                    }

                    if (countOfOverlappedItems > 1) {
                        String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
                        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
            }

            // use custom old callout overlay
            if (overlayItem instanceof NMapPOIitem) {
                NMapPOIitem poiItem = (NMapPOIitem)overlayItem;

                if (poiItem.showRightButton()) {
                    return new NMapCalloutCustomOldOverlay(itemOverlay, overlayItem, itemBounds,
                            mMapViewerResourceProvider);
                }
            }

            // use custom callout overlay
            return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

            // set basic callout overlay
            //return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
        }
    };

    private final NMapPOIdataOverlay.OnFloatingItemChangeListener onPOIdataFloatingItemChangeListener = new NMapPOIdataOverlay.OnFloatingItemChangeListener() {

        @Override
        public void onPointChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            NGeoPoint point = item.getPoint();

            if (DEBUG) {
                Log.i(LOG_TAG, "onPointChanged: point=" + point.toString());
            }

            mMapContext.findPlacemarkAtLocation(point.longitude, point.latitude);

            item.setTitle(null);

        }
    };

    private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

        @Override
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onCalloutClick: title=" + item.getTitle());
            }

            // [[TEMP]] handle a click event of the callout
            Toast.makeText(getContext(), "onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            if (DEBUG) {
                if (item != null) {
                    Log.i(LOG_TAG, "onFocusChanged: " + item.toString());
                } else {
                    Log.i(LOG_TAG, "onFocusChanged: ");
                }
            }
        }
    };

    private final NMapOverlayManager.OnCalloutOverlayViewListener onCalloutOverlayViewListener = new NMapOverlayManager.OnCalloutOverlayViewListener() {

        @Override
        public View onCreateCalloutOverlayView(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

            if (overlayItem != null) {
                // [TEST] 말풍선 오버레이를 뷰로 설정함
                String title = overlayItem.getTitle();
                if (title != null && title.length() > 5) {
                    return new NMapCalloutCustomOverlayView(getContext(), itemOverlay, overlayItem, itemBounds);
                }
            }

            // null을 반환하면 말풍선 오버레이를 표시하지 않음
            return null;
        }

    };

    private void stopMyLocation() {
        if (mMyLocationOverlay != null) {
            mMapLocationManager.disableMyLocation();

            if (mMapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mMapView.setAutoRotateEnabled(false, false);

                mMapContainer.requestLayout();
            }
        }
    }

    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

			if (mMapController != null) {
				mMapController.animateTo(myLocation);
			}
            Log.d("myLog", "myLocation  lat " + myLocation.getLatitude());
            Log.d("myLog", "myLocation  lng " + myLocation.getLongitude());

            mMapContext.findPlacemarkAtLocation(myLocation.getLongitude(), myLocation.getLatitude());
            currentLocation = myLocation;

            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
            Toast.makeText(getContext(), "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
            Toast.makeText(getContext(), "Your current location is unavailable area.", Toast.LENGTH_LONG).show();
            stopMyLocation();
        }

    };

    public NGeoPoint getCurrentLocation() {
        return currentLocation;
    }

    private HashMap<Integer, NMapPOIdataOverlay> poidataOverlayHashMap = new HashMap<>();
    public void removeCategoryMarkers(int naverCategory) {
        mOverlayManager.removeOverlay(poidataOverlayHashMap.get(naverCategory));
        poidataOverlayHashMap.remove(naverCategory);
        mOverlayManager.populate();
    }

    public void drawCategoryMarkers(List<ARMarker> markerList, int naverCategory) {
        NMapPOIdata poIdata = new NMapPOIdata(2, mMapViewerResourceProvider);

        poIdata.beginPOIdata(markerList.size());
        for(ARMarker marker : markerList)
            poIdata.addPOIitem(marker.getLongitude(), marker.getLatitude(), "", naverCategory, 0);
        poIdata.endPOIdata();

        //create POI data overlay
        NMapPOIdataOverlay poidataOverlay = mOverlayManager.createPOIdataOverlay(poIdata, null);
        poidataOverlay.showAllPOIdata(0);
        poidataOverlayHashMap.put(naverCategory, poidataOverlay);
    }

    public void clearCategoryMarker() {
        mOverlayManager.clearOverlays();
    }

    public void findAndDrawRoot(final String naviJson) {
            //find location
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currentLocation != null) {
                        double lat = currentLocation.getLatitude();
                        double lon = currentLocation.getLongitude();

                        try {
                            JSONObject test = new JSONObject(naviJson);
                            JSONObject result = test.getJSONObject("result");
                            JSONArray route = result.getJSONArray("route");
                            JSONObject start = route.getJSONObject(0).getJSONArray("point").getJSONObject(0);
                            JSONObject end = route.getJSONObject(route.length() - 1).getJSONArray("point").getJSONObject(0);

                            int markerId = NMapPOIflagType.PIN;
                            NMapPOIdata poIdata = new NMapPOIdata(2, mMapViewerResourceProvider);

                            poIdata.beginPOIdata(2);
                            poIdata.addPOIitem(start.getInt("x"), start.getInt("y"), "출발지", NMapPOIflagType.FROM, 0);
                            poIdata.addPOIitem(end.getInt("x"), end.getInt("y"), "도착지", NMapPOIflagType.TO, 0);

                            poIdata.endPOIdata();

                            NMapPOIdataOverlay poIdataOverlay = mOverlayManager.createPOIdataOverlay(poIdata, null);

                            NMapPathData pathData = new NMapPathData(route.length());
                            pathData.initPathData();
                            JSONArray path = route.getJSONObject(0).getJSONArray("point");
                            for (int i = 0; i < path.length(); i++) {
                                JSONObject data = path.getJSONObject(i);
                                pathData.addPathPoint(data.getInt("x"), data.getInt("y"), NMapPathLineStyle.TYPE_SOLID);
                            }
                            pathData.addPathPoint(end.getInt("x"), end.getInt("y"), NMapPathLineStyle.TYPE_DASH);
                            pathData.endPathData();
                            NMapPathDataOverlay pathDataOverlay = mOverlayManager.createPathDataOverlay(pathData);

                            pathDataOverlay.showAllPathData(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        Toast.makeText(getContext(), "현재 위치를 알 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            });

    }

    public void cancelNavi() {
        mOverlayManager.clearOverlays();
    }

    @Override
    public void onStart(){
        super.onStart();
        mMapContext.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapContext.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapContext.onPause();
    }
    @Override
    public void onStop() {
        mMapContext.onStop();
        super.onStop();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        mMapContext.onDestroy();
        super.onDestroy();
    }
}