/*
 * Copyright (C) 2010- Peer internet solutions
 *
 * This file is part of mixare.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package com.dragon4.owo.ar_trace.ARCore;
/*
 * Copyright (C) 2010- Peer internet solutions
 *
 * This file is part of mixare.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

/**
 * edited by joyeongje,Mansu on 2016. 12. 31..
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dragon4.owo.ar_trace.ARCore.Activity.SearchCategoryListActivity;
import com.dragon4.owo.ar_trace.ARCore.Activity.SearchKeywordListActivity;
import com.dragon4.owo.ar_trace.ARCore.Activity.TraceActivity;
import com.dragon4.owo.ar_trace.ARCore.Activity.WriteReviewActivity;
import com.dragon4.owo.ar_trace.ARCore.Marker.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.Marker.Compatibility;
import com.dragon4.owo.ar_trace.ARCore.Marker.SocialARMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataHandler;
import com.dragon4.owo.ar_trace.ARCore.data.DataProcessor.DataConvertor;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.ARCore.gui.PaintScreen;
import com.dragon4.owo.ar_trace.ARCore.render.Matrix;
import com.dragon4.owo.ar_trace.Configure.ClientInstance;
import com.dragon4.owo.ar_trace.FCM.FCMMessagingService;
import com.dragon4.owo.ar_trace.NaverMap.FragmentMapview;
import com.dragon4.owo.ar_trace.NaverMap.NMapPOIflagType;
import com.dragon4.owo.ar_trace.R;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.hardware.SensorManager.SENSOR_DELAY_GAME;

// 메인에 보여지게 될 믹스뷰(액티비티) 클래스

public class MixView extends FragmentActivity implements SensorEventListener, LocationListener, OnTouchListener {

    private Sensor orientationSensor;
    private SensorManager sensorMgr_ori;

    public static double gpsLat = 0;//gps에서 수시로 받아오는 값
    public static double gpsLong = 0;

    // 카메라 스크린과 증강된 뷰
    private CameraSurface camScreen;
    private AugmentedView augScreen;

    private boolean isInited;    // 초기 세팅이 되어 있는가
    private MixContext mixContext;    // 메인 컨텍스트
    static PaintScreen dWindow;        // 스크린 윈도우
    private static DataView dataView;
    private Thread downloadThread;    // 마커의 내용을 다운로드 할 스레드

    // 연산에 사용될 임시 변수들
    private float RTmp[] = new float[9];
    private float Rot[] = new float[9];
    private float I[] = new float[9];
    private float grav[] = new float[3];
    private float mag[] = new float[3];

    // 센서와 위치 정보를 읽기 위함
    private SensorManager sensorMgr;
    private List<Sensor> sensors;
    private Sensor sensorGrav, sensorMag;
    private LocationManager locationMgr;
    private boolean isGpsEnabled;

    // 연산에 사용될 매트릭스 객체들
    private int rHistIdx = 0;
    private Matrix tempR = new Matrix();
    private Matrix finalR = new Matrix();
    private Matrix smoothR = new Matrix();
    private Matrix histR[] = new Matrix[60];
    private Matrix m1 = new Matrix();
    private Matrix m2 = new Matrix();
    private Matrix m3 = new Matrix();
    private Matrix m4 = new Matrix();

    private WakeLock mWakeLock;    // 화면이 점멸되지 않게 하기 위함

    private boolean fError;    // 에러 여부

    // 나침반 에러
    private int compassErrorDisplayed = 0;

    // MixView를 덮는 하나의 뷰 & 기능들
    public TopLayoutOnMixView topLayoutOnMixView;

    //네비게이터 역할을 하는 클래스 추가
    private Navigator navigator;

    public static final int SHOW_TRACE = 3;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    static public DataView getDataView() {
        return dataView;
    }

    // 노티피케이션 텍스트 뷰
    private TextView searchNotificationTxt;

    // 로그에 쓰일 태그
    public static final String TAG = "Mixare";


    // 내부 저장공간에 저장될 프레퍼런스에 쓰일 이름
    public static final String PREFS_NAME = "MyPrefsFileForMenuItems";

    // GPS 사용이 가능한지 여부를 리턴
    public boolean isGpsEnabled() {
        return isGpsEnabled;
    }

    // 에러 처리 메소드
    public void doError(Exception ex1) {
        if (!fError) {
            fError = true;    // 에러 플래그를 true

            setErrorDialog();    // 에러 다이얼로그 호출

            ex1.printStackTrace();    // 에러 내용을 출력
            try {
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }

        try {
            augScreen.invalidate();    // 스크린의 갱신을 시도
        } catch (Exception ignore) {
        }
    }

    // 에러 발생시
    public void killOnError() throws Exception {
        if (fError)
            throw new Exception();
    }

    // 리페인트. 데이터 뷰와 페인트 스크린 객체를 새로 생성한다
    public void repaint() {
        dataView = new DataView(mixContext);
        dWindow = new PaintScreen();
    }

    // 에러 다이얼로그
    public void setErrorDialog() {
        // 얼럿 다이얼로그의 빌더 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 빌더에 메세지 등을 세팅
        builder.setMessage(getString(DataView.CONNECTION_ERROR_DIALOG_TEXT));
        builder.setCancelable(false);

        // 각 버튼의 처리
        /*Retry*/
        builder.setPositiveButton(DataView.CONNECTION_ERROR_DIALOG_BUTTON1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                fError = false;
                //TODO improve
                try {
                    repaint();    // 리페인트 호출
                } catch (Exception ex) {    // 실패시 에러 처리
                    doError(ex);
                }
            }
        });
        /*Open settings*/
        builder.setNeutralButton(DataView.CONNECTION_ERROR_DIALOG_BUTTON2, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // 무선 설정 인텐트를 호출
                Intent intent1 = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivityForResult(intent1, 42);
            }
        });
		/*Close application*/
        builder.setNegativeButton(DataView.CONNECTION_ERROR_DIALOG_BUTTON3, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.exit(0);    // 어플리케이션 강제 종료
            }
        });
        AlertDialog alert = builder.create();
        alert.show();    // 얼럿 다이얼로그 호출
    }

    public void navercategoryClicked(View v) throws ExecutionException, InterruptedException {

        //  if (dataView.isFrozen())
        //   dataView.setFrozen(false);

        DataSource.DATASOURCE selectedDatasource = null;

        DataConvertor dataConvertor = new DataConvertor();

        // TODO: 2016. 12. 9. 다른 카테고리 추가해야된다.
        switch (v.getId()) {
            case R.id.ar_mixview_cafe:
                selectedDatasource = DataSource.DATASOURCE.CAFE;
                break;

            case R.id.ar_mixview_bus:
                selectedDatasource = DataSource.DATASOURCE.BUSSTOP;
                break;

            case R.id.ar_mixview_convenience_store:
                selectedDatasource = DataSource.DATASOURCE.Convenience;
                break;

            case R.id.ar_mixview_restaurant:
                selectedDatasource = DataSource.DATASOURCE.Restaurant;
                break;

            case R.id.ar_mixview_bank:
                selectedDatasource = DataSource.DATASOURCE.BANK;
                break;

            case R.id.ar_mixview_hospital:
                selectedDatasource = DataSource.DATASOURCE.HOSPITAL;
                break;

            case R.id.ar_mixview_lodgment:
                selectedDatasource = DataSource.DATASOURCE.ACCOMMODATION;
                break;

            default:
                selectedDatasource = null;
                break;
        }

        if (selectedDatasource == null)
            Toast.makeText(mixContext, "지원하는 데이터소스없음", Toast.LENGTH_SHORT).show();

        else {
            Location location = mixContext.getCurrentLocation();

            dataView.getDataHandler().clearMarkerList(); // 마커들 삭제
            topLayoutOnMixView.naverFragment.clearCategoryMarker(); // 지도에 있는 마커들 삭제

            for (DataSource.DATASOURCE dataSource : DataSource.DATASOURCE.values()) {

                if(dataSource != selectedDatasource)
                    mixContext.setDataSource(dataSource,false);
                else
                    mixContext.setDataSource(selectedDatasource,!mixContext.isDataSourceSelected(selectedDatasource));
            }



            //dataView.getDataHandler().cleaMarketListUnCheckedDataSource(selectedDatasource); // 마커 삭제
            Log.i(selectedDatasource.toString(),"삭제됨");
            if(mixContext.isDataSourceSelected(selectedDatasource)) {
                dataView.requestData(DataSource.createRequestCategoryURL(selectedDatasource, location.getLatitude(), location.getLongitude(),
                        location.getAltitude(), 20), DataSource.dataFormatFromDataSource(selectedDatasource), selectedDatasource);
            }


        }
    }

    // 네비 데이터 브로드캐스트 리시버
    private BroadcastReceiver naviRecevicer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("NAVI")) {
                String guide = intent.getStringExtra("GUIDE");
                Snackbar.make(getWindow().getDecorView().getRootView(), guide, Snackbar.LENGTH_INDEFINITE)
                        .setAction("종료", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "네비게이션을 종료합니다.", Toast.LENGTH_SHORT).show();
                                MixState.enterNaviEnd = true;
                            }
                        }).show();
            }
        }
    };

    // 뷰 생성시
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        DataSource.createIcons(getResources());

        try {
            // 전원관리자
            final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            // 화면이 꺼지지 않게 하기위한 웨이크 락
            this.mWakeLock = pm.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
            // 위치관리자의 설정
            locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // 위치정보 업데이트 설정. 2번째 인자 시간(1/1000s), 3번째 인자 거리(m)에 따라 갱신한다
            locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, this);

            //orientation sensor 설정
            sensorMgr_ori = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            orientationSensor = sensorMgr_ori.getDefaultSensor(Sensor.TYPE_ORIENTATION);

            killOnError();    // 에러 여부를 체크한다
            requestWindowFeature(Window.FEATURE_NO_TITLE);    // 타이틀 바가 없는 윈도우 형태로

            // 프레임 레이아웃을 사용한다
            FrameLayout frameLayout = new FrameLayout(this);

            // 최소 넓이를 설정하고 줌 바를 등록, 여백을 설정한다
            frameLayout.setMinimumWidth(3000);
            frameLayout.setPadding(10, 0, 10, 10);

            // 카메라 스크린과 증강 스크린을 생성하고
            camScreen = new CameraSurface(this);
            augScreen = new AugmentedView(this);
            setContentView(camScreen);    // 카메라 스크린을 컨텐트 뷰로 등록 후

            // 증강 스크린을 덧씌워 준다
            addContentView(augScreen, new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


            // 최종적으로 더해지는 것은 위에서 설정한 프레임 레이아웃.
            // 레이아웃 파라메터를 통해 표면의 가장 위이자 공간의 가장 아래에 추가 된다
            addContentView(frameLayout, new FrameLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM));

            topLayoutOnMixView = new TopLayoutOnMixView(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addContentView(topLayoutOnMixView.mainArView, params);

            // 이거 맨위에 놔두면 topLayoutOnMixView 객체 없어서 터짐
            handleIntent(getIntent());    // 인텐트 제어

            // 초기 세팅된 상태가 아니라면
            if (!isInited) {
                mixContext = new MixContext(this);    // 컨텍스트 생성
                // 다운로드관리자 등록
                mixContext.downloadManager = new DownloadManager(mixContext);

                //네비게이터 역할을 하는 클래스 추가
                navigator = new Navigator(mixContext, topLayoutOnMixView.naverFragment);

                // 페인트 스크린과 데이터 뷰의 생성
                dWindow = new PaintScreen();
                dataView = new DataView(mixContext);

                isInited = true;    // 세팅 플래그 true

            }

            if (mixContext.isActualLocation() == false) {
                Toast.makeText(this, getString(DataView.CONNECTION_GPS_DIALOG_TEXT), Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            doError(ex);    // 예외 발생시 에러 처리
        }

        // 네비 토스트 띄우는 거시다
        IntentFilter naviBraodFilter = new IntentFilter();
        naviBraodFilter.addAction("NAVI");
        registerReceiver(naviRecevicer, naviBraodFilter);
    }

    // 인텐트 제어
    private void handleIntent(Intent intent) {
        // 검색 버튼을 눌렀을 경우
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);    // 쿼리 생성
            //  doMixSearch(query);    // 마커로부터 검색
        }
        else if(intent.getExtras() != null){
            FCMMessagingService.clear(intent.getStringExtra("traceID"));
            Intent traceIntent = new Intent(this, TraceActivity.class);
            traceIntent.putExtras(intent.getExtras());
            topLayoutOnMixView.mainArView.setVisibility(View.GONE);
            startActivityForResult(traceIntent, SHOW_TRACE);
        }
    }

    // 새 인텐트 생성시
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }


    private void doMixSearch(String query) {
        DataHandler jLayer = dataView.getDataHandler();    // 데이터 핸들러 등록

        // 검색 결과를 저장 할 리스트
        ArrayList<ARMarker> searchResults = new ArrayList<ARMarker>();

        // 검색된 항목이 1개 이상 있을 경우
        if (jLayer.getMarkerCount() > 0) {
            // 검색된 항목들을 결과 리스트에 추가
            for (int i = 0; i < jLayer.getMarkerCount(); i++) {
                ARMarker ma = jLayer.getMarker(i);
                if (ma.getTitle().toLowerCase().indexOf(query.toLowerCase()) != -1) {
                    searchResults.add(ma);
    				/*타이틀에 상응하는 웹사이트들*/
                }
            }
        }
        // 결과 리스트에 하나라도 값이 있을 경우
        if (searchResults.size() > 0) {
            dataView.setFrozen(true);    // 데이터 뷰를 얼리고
            jLayer.setMarkerList(searchResults);    // 결과를 핸들러에 할당
        } else    // 결과가 없을 경우엔 토스트 출력
            Toast.makeText(this, getString(DataView.SEARCH_FAILED_NOTIFICATION), Toast.LENGTH_LONG).show();
    }

    // 중단 되었을 경우
    @Override
    protected void onPause() {
        super.onPause();

        try {
            this.mWakeLock.release();    // 웨이크 락을 풀어준다

            // 각 센서 관리자의 등록을 해제
            try {
                sensorMgr.unregisterListener(this, sensorGrav);
            } catch (Exception ignore) {
            }
            try {
                sensorMgr.unregisterListener(this, sensorMag);
            } catch (Exception ignore) {
            }
            try {
                sensorMgr_ori.unregisterListener(this, orientationSensor);
            } catch (Exception ignore) {

            }
            sensorMgr = null;

            // 위치 관리자의 등록을 해제
            try {
                locationMgr.removeUpdates(this);
            } catch (Exception ignore) {
            }
            locationMgr = null;

            // 다운로드 관리자를 중지
            try {
                mixContext.downloadManager.stop();
            } catch (Exception ignore) {
            }

            // 에러 발생시에는 종료
            if (fError) {
                finish();
            }
        } catch (Exception ex) {
            doError(ex);    // 예외 발생시 에러 처리
        }
    }

    // 재개 되었을 경우. 대부분의 처리는 이곳에서...
    @Override
    protected void onResume() {
        super.onResume();

        try {
            this.mWakeLock.acquire();    // 웨이크 록

            killOnError();    // 에러 여부를 체크한다
            mixContext.mixView = this;    // 컨텍스트의 믹스뷰를 설정하고
            dataView.doStart();            // 데이터뷰 활성화
            dataView.clearEvents();        // 이벤트 클리어


            double angleX, angleY;        // 앵글의 x, y


			/*연산에 사용될 매트릭스 값들을 설정한다*/
            angleX = Math.toRadians(-90);
            m1.set(1f, 0f, 0f, 0f, (float) Math.cos(angleX), (float) -Math
                    .sin(angleX), 0f, (float) Math.sin(angleX), (float) Math
                    .cos(angleX));

            angleX = Math.toRadians(-90);
            angleY = Math.toRadians(-90);
            m2.set(1f, 0f, 0f, 0f, (float) Math.cos(angleX), (float) -Math
                    .sin(angleX), 0f, (float) Math.sin(angleX), (float) Math
                    .cos(angleX));
            m3.set((float) Math.cos(angleY), 0f, (float) Math.sin(angleY),
                    0f, 1f, 0f, (float) -Math.sin(angleY), 0f, (float) Math
                            .cos(angleY));

            m4.toIdentity();

            for (int i = 0; i < histR.length; i++) {
                histR[i] = new Matrix();
            }
			/*매트릭스 값 설정 완료*/

            // 센서 관리자 설정
            sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

            // 각 센서들 등록
            // 가속도 센서
            sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
            if (sensors.size() > 0) {
                sensorGrav = sensors.get(0);
            }

            // 지자기 센서
            sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
            if (sensors.size() > 0) {
                sensorMag = sensors.get(0);
            }
            //// TODO: 2016-06-01
            if (sensors.size() > 0) {
                sensors = sensorMgr_ori.getSensorList(Sensor.TYPE_ORIENTATION);
                orientationSensor = sensors.get(0);
            }
            // 센서 관리자에 레지스터리스너를 이용해 각 센서들을 등록
            sensorMgr.registerListener(this, sensorGrav, SENSOR_DELAY_GAME);
            sensorMgr.registerListener(this, sensorMag, SENSOR_DELAY_GAME);

            if (orientationSensor != null) {
                sensorMgr_ori.registerListener(this, orientationSensor, sensorMgr_ori.SENSOR_DELAY_GAME);
            }

            try {
                // 위치제공자의 기준(Criteria)
                // http://developer.android.com/reference/android/location/Criteria.html
                Criteria c = new Criteria();

                // 정확도 설정
                c.setAccuracy(Criteria.ACCURACY_FINE);
                //c.setBearingRequired(true);

                // 위치관리자를 할당 후
                locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                // 인자 값에 따라 위치 갱신을 요청한다. 2번째가 시간, 3번째가 거리
                locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 4, this);

                // 최적의 위치제공자를 찾고, 할당 가능한지 파악한다
                String bestP = locationMgr.getBestProvider(c, true);
                isGpsEnabled = locationMgr.isProviderEnabled(bestP);

                // gps, 네트워크가 먹통일 때의 기본 위치 설정
                Location hardFix = new Location("reverseGeocoded");

                try {
                    // 위치 관리자로부터 gps, 네트워크의 마지막으로 알려진 장소를 얻어 옮
                    Location gps = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location network = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    // 위치 값을 얻어오게 되면 현재 위치를 지정
                    // 우선순위는 gps > 네트워크 > 기본값
                    if (gps != null)
                        mixContext.curLoc = gps;
                    else if (network != null)
                        mixContext.curLoc = network;
                    else
                        mixContext.curLoc = hardFix;

                } catch (Exception ex2) {    // 예외 발생 시
                    ex2.printStackTrace();    // 메세지를 내보내고
                    mixContext.curLoc = hardFix;    // 현재 위치는 기본값으로
                }
                // 최종적으로 지정된 값으로 컨텍스트의 위치를 세팅
                mixContext.setLocationAtLastDownload(mixContext.curLoc);

                // 지자기장의 설정. 밀리초 단위로 지정된 위치를 기준으로 삼는다
                GeomagneticField gmf = new GeomagneticField((float) mixContext.curLoc
                        .getLatitude(), (float) mixContext.curLoc.getLongitude(),
                        (float) mixContext.curLoc.getAltitude(), System
                        .currentTimeMillis());

                // 지자기장의 값으로 매트릭스를 세팅
                angleY = Math.toRadians(-gmf.getDeclination());
                m4.set((float) Math.cos(angleY), 0f,
                        (float) Math.sin(angleY), 0f, 1f, 0f, (float) -Math
                                .sin(angleY), 0f, (float) Math.cos(angleY));
                mixContext.declination = gmf.getDeclination();
            } catch (Exception ex) {
                Log.d("mixare", "GPS Initialize Error", ex);    // 초기화 에러 로그
            }
            // 다운로드 스레드의 활성화
            downloadThread = new Thread(mixContext.downloadManager);
            downloadThread.start();


        } catch (Exception ex) {
            doError(ex);    // 에러 처리

            try {
                // 각각의 센서를 해제
                if (sensorMgr != null) {
                    sensorMgr.unregisterListener(this, sensorGrav);
                    sensorMgr.unregisterListener(this, sensorMag);
                    sensorMgr = null;
                }
                // 위치 관리자도 해제 해 준다
                if (locationMgr != null) {
                    locationMgr.removeUpdates(this);
                    locationMgr = null;
                }
                // 컨텍스트의 다운로드 관리자도 정지
                if (mixContext != null) {
                    if (mixContext.downloadManager != null)
                        mixContext.downloadManager.stop();
                }
            } catch (Exception ignore) {
            }
        }


        // 알림 텍스트의 처리
        // 데이터 뷰가 얼지 않았을 때(기본 상태일 때) 내용을 표시하고 터치 가능하게 함
        if (dataView.isFrozen() && searchNotificationTxt == null) {
            searchNotificationTxt = new TextView(this);
            searchNotificationTxt.setWidth(dWindow.getWidth());
            searchNotificationTxt.setPadding(10, 2, 0, 0);
            searchNotificationTxt.setText(getString(DataView.SEARCH_ACTIVE_1) + " " + mixContext.getDataSourcesStringList() + getString(DataView.SEARCH_ACTIVE_2));
            ;
            searchNotificationTxt.setBackgroundColor(Color.DKGRAY);
            searchNotificationTxt.setTextColor(Color.WHITE);

            searchNotificationTxt.setOnTouchListener(this);
            addContentView(searchNotificationTxt, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        } else if (!dataView.isFrozen() && searchNotificationTxt != null) {
            searchNotificationTxt.setVisibility(View.GONE);
            searchNotificationTxt = null;
        }
    }

    // 센서값이 변경될 경우 처리
    public void onSensorChanged(SensorEvent evt) {
        try {
            //			killOnError();

            // 가속도 센서, 지자기 센서 각각의 경우 지정된 변수에 값을 넣는다
            if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                grav[0] = evt.values[0];
                grav[1] = evt.values[1];
                grav[2] = evt.values[2];

                augScreen.postInvalidate();    // 변경을 알림
            } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mag[0] = evt.values[0];
                mag[1] = evt.values[1];
                mag[2] = evt.values[2];

                augScreen.postInvalidate();    // 변경을 알림
            }

            // 회전 행렬값들을 저장
            SensorManager.getRotationMatrix(RTmp, I, grav, mag);
            // 축 변경(?)
            SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, Rot);

            // 임시 행렬에 값들을 지정
            tempR.set(Rot[0], Rot[1], Rot[2], Rot[3], Rot[4], Rot[5], Rot[6], Rot[7],
                    Rot[8]);

            // 단위 행렬로 세팅하고 행렬 곱 연산 후, 역행렬로 만든다
            finalR.toIdentity();
            finalR.prod(m4);
            finalR.prod(m1);
            finalR.prod(tempR);
            finalR.prod(m3);
            finalR.prod(m2);
            finalR.invert();

            // 이후 부분에 대해서는 더 분석이 필요할 것 같다...
            histR[rHistIdx].set(finalR);
            rHistIdx++;
            if (rHistIdx >= histR.length)
                rHistIdx = 0;

            smoothR.set(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
            for (int i = 0; i < histR.length; i++) {
                smoothR.add(histR[i]);
            }
            smoothR.mult(1 / (float) histR.length);

            synchronized (mixContext.rotationM) {
                mixContext.rotationM.set(smoothR);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 터치 이벤트 처리
    @Override
    public boolean onTouchEvent(MotionEvent me) {
        try {
            killOnError();    // 에러 여부를 판단

            // 눌려진 부분의 x, y 좌표를 저장한다
            float xPress = me.getX();
            float yPress = me.getY();

            // 손을 뗏을 경우 데이터 뷰의 클릭이벤트 처리
            if (me.getAction() == MotionEvent.ACTION_UP) {
                dataView.clickEvent(xPress, yPress);
            }

            return true;

        } catch (Exception ex) {    // 예외 처리
            //doError(ex);
            ex.printStackTrace();
            return super.onTouchEvent(me);
        }
    }

    // 위치제공자가 불능의 경우
    public void onProviderDisabled(String provider) {
        if(locationMgr != null)
            isGpsEnabled = locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // 위치제공자가 사용 가능한 경우. 굳이 따로 나눌 필요가 있었을까?
    public void onProviderEnabled(String provider) {
        if(locationMgr != null)
            isGpsEnabled = locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // 상태 변화시 처리. 일단은 비워둔다
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    // 위치가 변경되었을 경우
    public void onLocationChanged(Location location) {
        try {
            killOnError();    // 에러 여부 체크
            gpsLat = location.getLatitude();
            gpsLong = location.getLongitude();

            // 변경된 위치의 로그를 생성한다. 위도와 경도를 기록
            Log.v(TAG, "Location Changed: " + location.getProvider() + " lat: " + location.getLatitude() + " lon: " + location.getLongitude() + " alt: " + location.getAltitude() + " acc: " + location.getAccuracy());

            // 위치 제공자가 동일할 경우
            if (LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
                // 현재의 위치를 변경된 위치로 지정
                synchronized (mixContext.curLoc) {
                    mixContext.curLoc = location;
                }
                // 데이터 뷰가 얼어있지 않다면
                if (!dataView.isFrozen())    // 데이터뷰의 데이터 핸들러에 위치변경 통보
                    dataView.getDataHandler().onLocationChanged(location);

				/* 만약 마지막으로 데이터를 다운로드한 곳으로부터 반경 3km 이상 *
				 * 떨어진 곳으로 이동한다면, 데이터를 새로 다운로드 해야 한다	 */
                // 마지막으로 다운로드된 위치를 저장
                Location lastLoc = mixContext.getLocationAtLastDownload();
                if (lastLoc == null)    // 기록된 위치가 없다면
                    mixContext.setLocationAtLastDownload(location);    // 변경된 위치를 마지막 위치로 지정
                else {
                    // 경계 설정
                    float threshold = dataView.getRadius() * 1000f / 3f;
                    Log.v(TAG, "Location Change: " + " threshold " + threshold + " distanceto " + location.distanceTo(lastLoc));
                    // 설정 경계를 넘어가면 데이터를 다시 다운로드한다
                    if (location.distanceTo(lastLoc) > threshold) {
                        Log.d(TAG, "Restarting download due to location change");
                        dataView.doStart();
                    }
                }
                isGpsEnabled = true;    // GPS는 가능한 상태로
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 정확도가 변경되었을 경우
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 자기장 센서의 값이 명확치 않을 경우
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE && compassErrorDisplayed == 0) {
            for (int i = 0; i < 2; i++) {
                // 나침반 데이터가 부정확합니다. 나침반을 수정하십시오
                Toast.makeText(mixContext, "Compass data unreliable. Please recalibrate compass.", Toast.LENGTH_LONG).show();
            }
            compassErrorDisplayed++;
        }
    }

    // 터치시 발생. onTouchEvent 보다 먼저 발생한다
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        dataView.setFrozen(false);    // 데이터뷰 해동
        if (searchNotificationTxt != null) {    // 알림텍스트가 있다면
            searchNotificationTxt.setVisibility(View.GONE);    // 사라지게 함
            searchNotificationTxt = null;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        topLayoutOnMixView.mainArView.setVisibility(View.VISIBLE);
        if(requestCode == TopLayoutOnMixView.WRITE_REVIEW) {

        }
        else if(resultCode == RESULT_OK && requestCode == TopLayoutOnMixView.SEARCH_LIST) {
            double lat = Double.valueOf(data.getExtras().getString("lat")).doubleValue();
            double lon = Double.valueOf(data.getExtras().getString("lon")).doubleValue();

            //검색리스트에서 길찾기를 눌렀을 경우 네비안내
            if(navigator != null)
                navigator.run(lat, lon);
            else
                Toast.makeText(this, "네비게이션 기능을 실행할 수 없습니다.", Toast.LENGTH_LONG).show();
        }
        else if(resultCode == RESULT_OK && requestCode == MixView.SHOW_TRACE) {
        }
    }

    @Override
    public void onBackPressed() {
        if(!topLayoutOnMixView.onBackPressed())
            finish();
    }

    public void drawCategoryMarkers(List<ARMarker> markerList) {

        int naverCategory = -1;
        switch (markerList.get(0).getDatasource()) {
            case CAFE:
                naverCategory = NMapPOIflagType.CAFE;
                break;

            case BUSSTOP:
                naverCategory = NMapPOIflagType.BUS;
                break;

            case Convenience:
                naverCategory = NMapPOIflagType.CONVENIENCE_STORE;
                break;

            case Restaurant:
                naverCategory = NMapPOIflagType.RESTAURANT;
                break;

            case BANK:
                naverCategory = NMapPOIflagType.BANK;
                break;

            case HOSPITAL:
                naverCategory = NMapPOIflagType.HOSPITAL;
                break;

            case ACCOMMODATION:
                naverCategory = NMapPOIflagType.LODGEMENT;
                break;
        }

        topLayoutOnMixView.naverFragment.drawCategoryMarkers(markerList, naverCategory);
    }
}

/**
 * @author daniele
 */

// 카메라 뷰(카메라 서페이스) 클래스
class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
    MixView app;    // 메인 뷰
    SurfaceHolder holder;    // 서페이스 홀더
    Camera camera;


    // 생성자
    CameraSurface(Context context) {
        super(context);

        try {
            app = (MixView) context;    // 컨텍스트(메인 뷰)를 등록

            // 홀더를 읽어오고 콜백을 등록한다
            holder = getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);    // 푸쉬버퍼 사용
        } catch (Exception ex) {

        }
    }

    // 서페이스 생성시
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // 카메라가 열려있다면 정지하고 해제
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ignore) {
                }
                try {
                    camera.release();
                } catch (Exception ignore) {
                }
                camera = null;
            }

            camera = Camera.open();    // 카메라 오픈
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);    // 서페이스 홀더에 영상을 출력할 곳 지정
        } catch (Exception ex) {
            try {    // 예외 발생시 카메라 정지 및 해제
                if (camera != null) {
                    try {
                        camera.stopPreview();
                    } catch (Exception ignore) {
                    }
                    try {
                        camera.release();
                    } catch (Exception ignore) {
                    }
                    camera = null;
                }
            } catch (Exception ignore) {

            }
        }
    }

    // 서페이스 파괴시
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {    // 카메라 정지 및 해제
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ignore) {
                }
                try {
                    camera.release();
                } catch (Exception ignore) {
                }
                camera = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 서페이스 변경시
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
            // 일단 카메라의 파라메터 값들을 읽어온다
            Camera.Parameters parameters = camera.getParameters();
            try {
                // 지원되는 카메라의 사이즈 리스트
                List<Camera.Size> supportedSizes = null;

                // 이하의 코드는 안드로이드 1.6버전 미만에서는 작동하지 않는다
                // 그럼에도 불구하고 카메라는 돌아가겠지만...

                // 파라메터로부터 지원되는 프리뷰 사이즈들을 읽어온다
                supportedSizes = Compatibility.getSupportedPreviewSizes(parameters);

                // 프리뷰 형태의 팩터
                float ff = (float) w / h;
                Log.d("Mixare", "Screen res: w:" + w + " h:" + h + " aspect ratio:" + ff);

                //holder for the best form factor and size
                // 최적의 형태의 팩터와 사이즈의 홀더를 구한다
                float bff = 0;
                int bestw = 0;
                int besth = 0;
                Iterator<Camera.Size> itr = supportedSizes.iterator();


                // 반복자(이터레이터)를 이용해 최적의 사이즈를 찾는다
                while (itr.hasNext()) {
                    Camera.Size element = itr.next();

                    // 현재 형태의 팩터 (current form factor)
                    float cff = (float) element.width / element.height;

                    // 각 요소들의 변수값을 로그로 기록
                    Log.d("Mixare", "Candidate camera element: w:" + element.width + " h:" + element.height + " aspect ratio:" + cff);

                    // 현재 요소가 여태까지의 최적의 결과를 대체할 수 있는지 체크
                    // 현재 형태의 요소는 최적의 팩터에 가까울 것이고
                    // 프리뷰의 넓이는 스크린 넓이보다 작고, 최적의 넓이보다는 넓어야 한다
                    // * 이 조합은 보다 나은 해법을 보장하게 될 것이다
                    if ((ff - cff <= ff - bff) && (element.width <= w) && (element.width >= bestw)) {
                        bff = cff;
                        bestw = element.width;
                        besth = element.height;
                    }
                }
                // 최종적인 결과 수치를 로그에 기록한다
                Log.d("Mixare", "Chosen camera element: w:" + bestw + " h:" + besth + " aspect ratio:" + bff);

                // 몇몇 삼성 제품에서는 최적의 넓이와 높이가 0으로 판명날 것이다.
                // 이는 그 제품들에서는 최소의 프리뷰 사이즈가 스크린 사이즈보다 크기 때문인데,
                // 이런 경우에는 디폴트 값(480*320)을 주도록 한다
                if ((bestw == 0) || (besth == 0)) {
                    Log.d("Mixare", "Using default camera parameters!");
                    bestw = 480;
                    besth = 320;
                }
                parameters.setPreviewSize(bestw, besth);    // 프리뷰 사이즈 최종 설정
            } catch (Exception ex) {    // 예외 발생시에도 디폴트 값으로...
                parameters.setPreviewSize(480, 320);
            }

            // 모든 값이 입력된 파라메터를 카메라에 적용하고
            camera.setParameters(parameters);
            camera.startPreview();    // 프리뷰를 시작한다
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

// 증강된 뷰
class AugmentedView extends View {
    MixView app;    // 메인 뷰

    // 생성자
    public AugmentedView(Context context) {
        super(context);

        try {
            app = (MixView) context;    // 컨텍스트(메인 뷰)를 등록

            app.killOnError();    // 에러 여부를 체크
        } catch (Exception ex) {
            app.doError(ex);
        }
    }

    // 그려지는 부분
    @Override
    protected void onDraw(Canvas canvas) {
        try {

            app.killOnError();    // 에러 여부를 체크

            // 캔버스의 넓이만큼 뷰로 설정(뷰의 넓이만큼을 캔버스로 이용한다)
            MixView.dWindow.setWidth(canvas.getWidth());
            MixView.dWindow.setHeight(canvas.getHeight());
            MixView.dWindow.setCanvas(canvas);    // 캔버스와 연결

            // 데이터 뷰가 초기화 되지 않았을 경우엔 초기화 처리
            if (!MixView.getDataView().isInited()) {
                MixView.getDataView().init(MixView.dWindow.getWidth(), MixView.dWindow.getHeight());
            }


            // 데이터 뷰의 데이터들을 윈도우에 그린다
            MixView.getDataView().draw(MixView.dWindow);

        } catch (Exception ex) {
            app.doError(ex);
        }
    }
}

class TopLayoutOnMixView {
    static public String TAG = "TopLayoutOnMixView";
    public static final int WRITE_REVIEW = 1;
    public static final int SEARCH_LIST = 2;

    //네이버 지도
    public FragmentMapview naverFragment;

    //뷰들
    private LinearLayout parentButtonView;
    private LinearLayout parentCategoryView;
    private LinearLayout buttonViewLayout;

    //검색바 뷰들
    private LinearLayout searchbar;
    private Button searchBtn;
    private Button hideSearchbar;
    private EditText searchText;

    //가장 상단의 레이아웃
    public View mainArView;

    //expansion, reduction level
    private int naver_map_width = 130;
    private int naver_map_height = 130;

    private LayoutInflater inflater;
    private Context context;

    public TopLayoutOnMixView(final Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        mainArView = inflater.inflate(R.layout.activity_ar_mixview, null);

        initButtonViews();
        initNaverMap();
        initSearchbar();
    }

    public void initButtonViews() {
        parentButtonView = (LinearLayout) mainArView.findViewById(R.id.ar_mixview_parent_buttonview);
        parentCategoryView = (LinearLayout) mainArView.findViewById(R.id.ar_mixview_parent_categoryview);

        mainArView.findViewById(R.id.ar_mixview_search_erase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
            }
        });

        mainArView.findViewById(R.id.ar_mixview_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentButtonView.setVisibility(View.GONE);
                parentCategoryView.setVisibility(View.VISIBLE);
            }
        });
        mainArView.findViewById(R.id.ar_mixview_category_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentCategoryView.setVisibility(View.GONE);
                parentButtonView.setVisibility(View.VISIBLE);
            }
        });


        mainArView.findViewById(R.id.ar_mixview_write_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: 2017. 2. 12. wrieReviewActivity
                // SearchActivity 로 먼저가야됨

                NGeoPoint nGeoPoint = naverFragment.getCurrentLocation();
                if(nGeoPoint == null)
                    Toast.makeText(context, "아직 나의 위치가 갱신되지 않았습니다.", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(context, SearchCategoryListActivity.class);

                    intent.putExtra("lat", nGeoPoint.getLatitude());
                    intent.putExtra("lon", nGeoPoint.getLongitude());
                    mainArView.setVisibility(View.GONE);
                    ((Activity) context).startActivityForResult(intent, WRITE_REVIEW);
                }
            }
        });

        buttonViewLayout = (LinearLayout) mainArView.findViewById(R.id.ar_mixview_buttonview);
        final Button hideBtn = (Button) mainArView.findViewById(R.id.ar_mixview_buttonview_hide);
        hideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonViewLayout.getVisibility() == View.GONE) {
                    buttonViewLayout.setVisibility(View.VISIBLE);
                    hideBtn.setBackgroundResource(R.drawable.icon_menu_up);
                } else if (buttonViewLayout.getVisibility() == View.VISIBLE) {
                    buttonViewLayout.setVisibility(View.GONE);
                    hideBtn.setBackgroundResource(R.drawable.icon_menu_down);
                }
            }
        });
    }

    public void initSearchbar() {
        final ListView searchListView = (ListView) mainArView.findViewById(R.id.ar_mixview_search_list);

        searchbar = (LinearLayout) mainArView.findViewById(R.id.ar_mixview_searchbar);
        hideSearchbar = (Button) mainArView.findViewById(R.id.ar_mixview_hide_searchbar);
        searchBtn = (Button) mainArView.findViewById(R.id.ar_mixview_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentButtonView.setVisibility(View.GONE);
                searchbar.setVisibility(View.VISIBLE);
            }
        });
        hideSearchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchbar.setVisibility(View.GONE);
                parentButtonView.setVisibility(View.VISIBLE);
            }
        });

        searchText = (EditText) mainArView.findViewById(R.id.ar_mixview_search_text);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, android.view.KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        String queryString = searchText.getText().toString();
                        Intent intent = new Intent(context, SearchKeywordListActivity.class);
                        intent.putExtra("searchName", queryString);
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mainArView.getWindowToken(), 0);
                        mainArView.setVisibility(View.GONE);
                        ((Activity)context).startActivityForResult(intent, SEARCH_LIST);
                        break;
                    default:
                        Toast.makeText(context, "기본", Toast.LENGTH_LONG).show();
                        return false;
                }
                return true;
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String queryString = searchText.getText().toString();
                        try {
                            List<ARMarker> searchList = null;
                            String encodedQueryString = URLEncoder.encode(queryString, "UTF-8");

                            String tempCallbackUrl = "http://ac.map.naver.com/ac?q=" + encodedQueryString + "&st=10&r_lt=10&r_format=json";
                            String rawData = new NaverHttpHandler().execute(tempCallbackUrl).get();
                            Log.i("rawData", rawData);

                            JSONObject root = new JSONObject(rawData);
                            JSONArray dataArray = root.getJSONArray("items");
                            JSONArray locationData = dataArray.getJSONArray(0);

                            Log.i("dataArray", locationData.toString());

                            final ArrayList<String> list = new ArrayList<>();
                            for (int index = 0; index < locationData.length(); index++)
                                list.add(locationData.getString(index).substring(2, locationData.getString(index).length() - 2));

                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SearchViewAdapter adapter = new SearchViewAdapter(inflater);
                                    adapter.setDataList(list);
                                    adapter.setCurrentText(charSequence.toString());
                                    searchListView.setAdapter(adapter);
                                    searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            String queryString = list.get(i);
                                            searchText.setText(queryString);
                                        }
                                    });
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        searchText.addTextChangedListener(watcher);
    }

    public void initNaverMap() {
        naverFragment = new FragmentMapview();
        naverFragment.setArguments(new Bundle());

        final Button expandView = (Button)mainArView.findViewById(R.id.ar_mixview_naverview_expand);
        expandView.setOnClickListener(new View.OnClickListener() {
            boolean isFull = false;
            @Override
            public void onClick(View view) {
                RelativeLayout navermapWrapper = (RelativeLayout)mainArView.findViewById(R.id.ar_mixview_naverview_wrapper);
                if(isFull) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, naver_map_width, Resources.getSystem().getDisplayMetrics()),
                            (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, naver_map_height, Resources.getSystem().getDisplayMetrics()));
                    int tenMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, Resources.getSystem().getDisplayMetrics());
                    params.setMargins(0, tenMargin, tenMargin, 0);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    navermapWrapper.setLayoutParams(params);
                    expandView.setBackgroundResource(R.drawable.ic_minimap_zoom_in);
                    isFull = false;
                }
                else {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    params.setMargins(0, 0, 0, 0);
                    navermapWrapper.setLayoutParams(params);
                    expandView.setBackgroundResource(R.drawable.ic_minimap_zoom_out);
                    isFull = true;
                }
            }
        });

        try {
            FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.ar_mixview_naverview, naverFragment);
            fragmentTransaction.commit();
        } catch(Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Only FragmentActivity can use naver maps");
        }
    }

    public void cancelSearchbar() {
        searchText.setText("");
        searchbar.setVisibility(View.GONE);
    }

    public boolean onBackPressed() {
        if(searchbar.getVisibility() != View.GONE) {
            cancelSearchbar();
            parentButtonView.setVisibility(View.VISIBLE);
            return true;
        }
        else if(parentCategoryView.getVisibility() != View.GONE) {
            parentCategoryView.setVisibility(View.GONE);
            parentButtonView.setVisibility(View.VISIBLE);
            return true;
        }
        else if(buttonViewLayout.getVisibility() != View.GONE) {
            buttonViewLayout.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private class SearchViewAdapter extends BaseAdapter {
        private ArrayList<String> dataList = new ArrayList<>();
        private String currentText;
        private LayoutInflater inflater;

        public SearchViewAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.layout_search_item, null);
            TextView searchItem = (TextView) view.findViewById(R.id.search_item);

            String data = dataList.get(i);
            int index = data.indexOf(currentText);

            if (index != -1) {
                SpannableStringBuilder builder = new SpannableStringBuilder();

                String before = data.substring(0, index);
                SpannableString beforeSpannable = new SpannableString(before);
                beforeSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, before.length(), 0);
                builder.append(beforeSpannable);

                String current = data.substring(index, index + currentText.length());
                SpannableString currentSpannable = new SpannableString(current);
                currentSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#88C290")), 0, current.length(), 0);
                builder.append(currentSpannable);

                String after = data.substring(index + currentText.length(), data.length());
                SpannableString afterSpannable = new SpannableString(after);
                afterSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, after.length(), 0);
                builder.append(afterSpannable);

                searchItem.setText(builder, TextView.BufferType.SPANNABLE);

                return view;
            }

            String currentTextNoSpace = currentText.replaceAll(" ", "");
            index = data.indexOf(currentTextNoSpace);

            if (index != -1) {
                SpannableStringBuilder builder = new SpannableStringBuilder();

                String before = data.substring(0, index);
                SpannableString beforeSpannable = new SpannableString(before);
                beforeSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, before.length(), 0);
                builder.append(beforeSpannable);

                String current = data.substring(index, index + currentTextNoSpace.length());
                SpannableString currentSpannable = new SpannableString(current);
                currentSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#88C290")), 0, current.length(), 0);
                builder.append(currentSpannable);

                String after = data.substring(index + currentTextNoSpace.length(), data.length());
                SpannableString afterSpannable = new SpannableString(after);
                afterSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, after.length(), 0);
                builder.append(afterSpannable);

                searchItem.setText(builder, TextView.BufferType.SPANNABLE);

                return view;
            }

            searchItem.setText(data);
            return view;
        }

        public void setDataList(ArrayList<String> dataList) {
            this.dataList = dataList;
        }

        public String getCurrentText() {
            return currentText;
        }

        public void setCurrentText(String currentText) {
            this.currentText = currentText;
        }
    }
}