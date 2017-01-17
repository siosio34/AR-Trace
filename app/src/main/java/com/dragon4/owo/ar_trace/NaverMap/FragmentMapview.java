package com.dragon4.owo.ar_trace.NaverMap;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.Fragment;

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
                Toast.makeText(getContext(), placeMark.toString(), Toast.LENGTH_LONG).show();
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

    private String testJSON = "{\n" +
            "  \"result\": {\n" +
            "    \"summary\": {\n" +
            "      \"returnCode\": 0,\n" +
            "      \"totalDistance\": 1464,\n" +
            "      \"totalTime\": 22,\n" +
            "      \"speed\": 4,\n" +
            "      \"startPoint\": {\n" +
            "        \"name\": \"경희대학교 국제캠퍼스\",\n" +
            "        \"x\": 349627363,\n" +
            "        \"y\": 149161374,\n" +
            "        \"px\": 349627346,\n" +
            "        \"py\": 149161641,\n" +
            "        \"dist\": 0\n" +
            "      },\n" +
            "      \"endPoint\": {\n" +
            "        \"name\": \"홈플러스 영통점\",\n" +
            "        \"x\": 349620903,\n" +
            "        \"y\": 149170722,\n" +
            "        \"px\": 349620928,\n" +
            "        \"py\": 149170975,\n" +
            "        \"dist\": 0\n" +
            "      }\n" +
            "    },\n" +
            "    \"route\": [\n" +
            "      {\n" +
            "        \"name\": \"RK_GENERAL\",\n" +
            "        \"guide\": \"~\",\n" +
            "        \"distance\": 0,\n" +
            "        \"time\": 0,\n" +
            "        \"point\": [\n" +
            "          {\n" +
            "            \"name\": \"경희대학교 국제캠퍼스\",\n" +
            "            \"key\": \"start\",\n" +
            "            \"x\": 349627363,\n" +
            "            \"y\": 149161374,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 0,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"출발\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 0,\n" +
            "              \"time\": 0\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"ldKILZ5H9YaOAjTQ48DuFg==\",\n" +
            "              \"pan\": 85,\n" +
            "              \"lat\": 149161641,\n" +
            "              \"lng\": 349627335,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"0\",\n" +
            "            \"x\": 349627047,\n" +
            "            \"y\": 149161633,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"349627346,149161641 349627073,149161625 349626904,149161684 349626847,149161758 349626810,149161798\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 1,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"약 58m 이동\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"약 58m 이동\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 58,\n" +
            "              \"time\": 1\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"Ai36Cv1bLYHuB6rum7jDdw==\",\n" +
            "              \"pan\": 98,\n" +
            "              \"lat\": 149161628,\n" +
            "              \"lng\": 349627064,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"1\",\n" +
            "            \"x\": 349626810,\n" +
            "            \"y\": 149161798,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 3,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"오른쪽에서 2번째 도로 방향으로\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"오른쪽에서 2번째 도로 방향으로\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 0,\n" +
            "              \"time\": 0\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"aLGyYQLpSp7AzrZAYsrCfQ==\",\n" +
            "              \"pan\": -156,\n" +
            "              \"lat\": 149161798,\n" +
            "              \"lng\": 349626810,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"2\",\n" +
            "            \"x\": 349626202,\n" +
            "            \"y\": 149163774,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"349626810,149161798 349626732,149162520 349626701,149162603 349626654,149162668 349626534,149162767 349626381,149162845 349626328,149162901 349626239,149163046 349626206,149163730 349626143,149164587 349626110,149165444 349626113,149165728 349626057,149165808 349626055,149165900 349626053,149165953\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 1,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"약 437m 이동\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"약 437m 이동\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 437,\n" +
            "              \"time\": 7\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"H/rjmCnCaeqg/YJaj3V3Og==\",\n" +
            "              \"pan\": 176,\n" +
            "              \"lat\": 149163821,\n" +
            "              \"lng\": 349626200,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"3\",\n" +
            "            \"x\": 349626053,\n" +
            "            \"y\": 149165953,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 2,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"왼쪽 방향으로\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"왼쪽 방향으로\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 0,\n" +
            "              \"time\": 0\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"aOJVaWCoPypzZzLND7dbRg==\",\n" +
            "              \"pan\": 90,\n" +
            "              \"lat\": 149165953,\n" +
            "              \"lng\": 349626053,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"4\",\n" +
            "            \"x\": 349625003,\n" +
            "            \"y\": 149165946,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"349626053,149165953 349625274,149165944 349624772,149165949 349623956,149165907\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 1,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"<b>횡단보도</b>까지 약 209m 이동(덕영대로)\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"약 209m 이동\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 209,\n" +
            "              \"time\": 4\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"PeWazqzZ8Duki7PvZ4uNxw==\",\n" +
            "              \"pan\": 90,\n" +
            "              \"lat\": 149165946,\n" +
            "              \"lng\": 349625011,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"5\",\n" +
            "            \"x\": 349623956,\n" +
            "            \"y\": 149165907,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"349623956,149165907 349623899,149166286 349623886,149166289 349623864,149166302 349623859,149166317 349623852,149166342 349623598,149166318\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 8,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"<b>2개의</b> <b>횡단보도</b>를 이용하여 <b>신형인쇄영통점</b> 방면으로 횡단\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"횡단\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 0,\n" +
            "              \"time\": 0\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"aiQo/8zsQnbwSh/upXp+4w==\",\n" +
            "              \"pan\": 86,\n" +
            "              \"lat\": 149165907,\n" +
            "              \"lng\": 349623956,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"6\",\n" +
            "            \"x\": 349623622,\n" +
            "            \"y\": 149166772,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"349623598,149166318 349623622,149166762 349623636,149167018 349623642,149167187 349623637,149167227\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 1,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"<b>현대종합장식</b>까지 약 90m 이동(영일로)\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"약 90m 이동\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 90,\n" +
            "              \"time\": 2\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"Vp32PSPmduAYRgVf/7c9mA==\",\n" +
            "              \"pan\": 181,\n" +
            "              \"lat\": 149166815,\n" +
            "              \"lng\": 349623624,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"7\",\n" +
            "            \"x\": 349623637,\n" +
            "            \"y\": 149167227,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 2,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"<b>현대종합장식</b> 앞에서 왼쪽 방향으로\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"왼쪽 방향으로\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 0,\n" +
            "              \"time\": 0\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"2Rw0+Mp6D62P7b7SyPxviQ==\",\n" +
            "              \"pan\": 107,\n" +
            "              \"lat\": 149167233,\n" +
            "              \"lng\": 349623634,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"8\",\n" +
            "            \"x\": 349622476,\n" +
            "            \"y\": 149168132,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"349623637,149167227 349623625,149167252 349623556,149167317 349623067,149167692 349622797,149167890 349622773,149167906 349622751,149167925 349622724,149167940 349622660,149167987 349622613,149168024 349622026,149168492 349621690,149168761 349621567,149168854 349621550,149168867 349621427,149168957 349621312,149169040\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 1,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"<b>살구골현대7단지아파트730동 앞</b> <b>횡단보도</b>까지 약 290m 이동(매영로)\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"약 290m 이동\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 290,\n" +
            "              \"time\": 5\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"Y/ksuu8C1tQ0uQ70a1fZkw==\",\n" +
            "              \"pan\": 307,\n" +
            "              \"lat\": 149168134,\n" +
            "              \"lng\": 349622475,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"9\",\n" +
            "            \"x\": 349621312,\n" +
            "            \"y\": 149169040,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"349621312,149169040 349621369,149169126 349621424,149169212\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 8,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"<b>횡단보도</b>를 이용하여 <b>KT영통지사</b> 방면으로 횡단\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"횡단\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 0,\n" +
            "              \"time\": 0\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"PF7qajQ/fGTmP4ZMq3jalw==\",\n" +
            "              \"pan\": 126,\n" +
            "              \"lat\": 149169013,\n" +
            "              \"lng\": 349621350,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"10\",\n" +
            "            \"x\": 349620773,\n" +
            "            \"y\": 149169777,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"349621424,149169212 349620793,149169690 349620771,149169724 349620768,149169749 349620776,149169789 349620719,149169832 349620648,149169885 349620619,149169885 349620592,149169888 349620560,149169904 349620346,149170083 349620162,149170238 349620133,149170275 349620113,149170328\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 1,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"<b>홈플러스수원영통점</b>까지 <b>1</b>개의 횡단보도를 지나 약 173m 이동\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"약 173m 이동\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 173,\n" +
            "              \"time\": 3\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"f6U2mS8Tkr/RR7IOQZhZqw==\",\n" +
            "              \"pan\": 127,\n" +
            "              \"lat\": 149169724,\n" +
            "              \"lng\": 349620771,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"11\",\n" +
            "            \"x\": 349620113,\n" +
            "            \"y\": 149170328,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 3,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"<b>홈플러스수원영통점</b> 앞에서 오른쪽 방향으로\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"오른쪽 방향으로\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 0,\n" +
            "              \"time\": 0\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"t1B4f3c1zbw0NDhHt8r5iQ==\",\n" +
            "              \"pan\": 127,\n" +
            "              \"lat\": 149170328,\n" +
            "              \"lng\": 349620113,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"12\",\n" +
            "            \"x\": 349620323,\n" +
            "            \"y\": 149170680,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"349620113,149170328 349620123,149170389 349620153,149170454 349620190,149170506 349620319,149170675 349620579,149171010\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 1,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"<b>이디야커피영통코레일점</b>까지 약 82m 이동(봉영로)\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"약 82m 이동\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 82,\n" +
            "              \"time\": 2\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"t7KejfY+Kmdha9BCud82yg==\",\n" +
            "              \"pan\": 217,\n" +
            "              \"lat\": 149170675,\n" +
            "              \"lng\": 349620319,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"13\",\n" +
            "            \"x\": 349620579,\n" +
            "            \"y\": 149171010,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 3,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"<b>이디야커피영통코레일점</b> 앞에서 오른쪽 방향으로\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"오른쪽 방향으로\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 0,\n" +
            "              \"time\": 0\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"MIWg2ZXyb7kJ72H8XtW6Gg==\",\n" +
            "              \"pan\": 37,\n" +
            "              \"lat\": 149170952,\n" +
            "              \"lng\": 349620534,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"\",\n" +
            "            \"key\": \"14\",\n" +
            "            \"x\": 349620579,\n" +
            "            \"y\": 149171010,\n" +
            "            \"direct\": 1,\n" +
            "            \"path\": \"349620579,149171010 349620928,149170975\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 13,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"홈플러스수원영통점 내부 통행로를 이용하여 약 35m 이동\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"약 35m 이동\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 35,\n" +
            "              \"time\": 1\n" +
            "            },\n" +
            "            \"panorama\": {\n" +
            "              \"id\": \"MIWg2ZXyb7kJ72H8XtW6Gg==\",\n" +
            "              \"pan\": 37,\n" +
            "              \"lat\": 149170952,\n" +
            "              \"lng\": 349620534,\n" +
            "              \"tilt\": 0\n" +
            "            }\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"RK_GENERAL\",\n" +
            "        \"guide\": \"~\",\n" +
            "        \"distance\": 0,\n" +
            "        \"time\": 0,\n" +
            "        \"point\": [\n" +
            "          {\n" +
            "            \"name\": \"홈플러스 영통점\",\n" +
            "            \"key\": \"end\",\n" +
            "            \"x\": 349620903,\n" +
            "            \"y\": 149170722,\n" +
            "            \"direct\": 0,\n" +
            "            \"path\": \"\",\n" +
            "            \"guide\": {\n" +
            "              \"no\": 0,\n" +
            "              \"value\": 0,\n" +
            "              \"name\": \"도착\",\n" +
            "              \"message\": \"\",\n" +
            "              \"pinLabel\": \"\"\n" +
            "            },\n" +
            "            \"road\": {\n" +
            "              \"distance\": 0,\n" +
            "              \"time\": 0\n" +
            "            },\n" +
            "            \"panorama\": null\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"bound\": \"349620113,149171010,349627363,149161374\"\n" +
            "  }\n" +
            "}";

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

                            //create POI data overlay
                            NMapPOIdataOverlay poIdataOverlay = mOverlayManager.createPOIdataOverlay(poIdata, null);

                            //show all POI data
                            //poIdataOverlay.showAllPOIdata(0);

                            //set event listener to the overlay
                            //poIdataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

                            //register callout overlay listener to customizeit.
                            //mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);

                            //set path data points
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