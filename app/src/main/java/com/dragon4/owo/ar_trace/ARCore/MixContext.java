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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

//adding support for https connections
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.ARCore.render.Matrix;

// 컨텍스트랩퍼를 확장하는 컨텍스트 클래스
public class MixContext extends ContextWrapper {

    // 뷰와 컨텍스트
    public MixView mixView;
    Context ctx;

    boolean isURLvalid = true;    // URL이 유효한지 여부
    Random rand;    // 랜덤 수치를 생성하기 위함

    DownloadManager downloadManager;    // 다운로드 관리자

    Location curLoc;    // 현재 위치
    Location locationAtLastDownload;    // 마지막으로 다운로드된 위치
    Matrix rotationM = new Matrix();    // 회전연산에 사용될 행렬

    float declination = 0f;    // 경사, 적위
    private boolean actualLocation = false;

    LocationManager locationMgr;        // 위치 관리자

    // 각 데이터소스의 선택 여부를 저장할 해쉬맵
    private HashMap<DataSource.DATASOURCE, Boolean> selectedDataSources = new HashMap<DataSource.DATASOURCE, Boolean>();

    // 생성자. 어플리케이션의 컨텍스트를 받는다
    public MixContext(Context appCtx) {
        super(appCtx);

        // 메인 뷰와 컨텍스트를 할당
        this.mixView = (MixView) appCtx;
        this.ctx = appCtx.getApplicationContext();

        // 회전행렬을 일단 단위행렬로 세팅
        rotationM.toIdentity();

        int locationHash = 0;    // 위치 해쉬값

        for (DataSource.DATASOURCE datasource : DataSource.DATASOURCE.values()) {
            selectedDataSources.put(datasource, false);
        }

        try {
            // 메인 컨텍스트의 위치 제공자로부터 위치 관리자 등록
            locationMgr = (LocationManager) appCtx.getSystemService(Context.LOCATION_SERVICE);

            // GPS 로부터 마지막으로 선택된 위치값을 등록
            Location lastFix = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // 설정된 위치값이 없다면 네트워크로부터 마지막으로 선택된 위치값을 등록
            if (lastFix == null) {
                lastFix = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            // lastFix 값이 할당되었을 경우
            if (lastFix != null) {
                // 위치 해쉬값을 이용하여
                locationHash = ("HASH_" + lastFix.getLatitude() + "_" + lastFix.getLongitude()).hashCode();

                // 실 시간과 시차 등을 계산
                long actualTime = new Date().getTime();
                long lastFixTime = lastFix.getTime();
                long timeDifference = actualTime - lastFixTime;

                actualLocation = timeDifference <= 1200000;    //20 min --- 300000 milliseconds = 5 min
            } else
                actualLocation = false;


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 계산된 위치 해쉬값으로 랜덤값 발생
        rand = new Random(System.currentTimeMillis() + locationHash);
    }

    // 현재의 GPS 정보를 리턴. 위치 관리자로부터 현재의 위치를 반환한다
    public Location getCurrentGPSInfo() {
        return curLoc != null ? curLoc : locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    // GPS 사용 가능 여부 리턴
    public boolean isGpsEnabled() {
        return mixView.isGpsEnabled();
    }

    // 정확한 위치가 맞는지 리턴
    public boolean isActualLocation() {
        return actualLocation;
    }

    // 사용중인 다운로드 관리자 리턴
    public DownloadManager getDownloader() {
        return downloadManager;
    }

    // 위치 관리자를 지정
    public void setLocationManager(LocationManager locationMgr) {
        this.locationMgr = locationMgr;
    }

    // 사용중인 위치 관리자를 리턴
    public LocationManager getLocationManager() {
        return locationMgr;
    }

    // 시작 Url 경로를 리턴한다
    public String getStartUrl() {
        return "";
    }

    // 인자로 받는 dest 에 회전 행렬을 세팅
    public void getRM(Matrix dest) {
        synchronized (rotationM) {
            dest.set(rotationM);
        }
    }

    // 현재의 위치를 리턴
    public Location getCurrentLocation() {
        synchronized (curLoc) {
            return curLoc;
        }
    }

    // 웹페이지를 로드
    public void loadMixViewWebPage(String url) throws Exception {

                WebView webview = new WebView(mixView);    // 웹 뷰
                webview.getSettings().setJavaScriptEnabled(true);    // 자바스크립트 허용
                webview.getSettings().setDomStorageEnabled(true);
                // URL 을 연결하여 웹 뷰 클라이언트를 세팅
                webview.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                     }

                 });

        // 다이얼로그를 생성
        Dialog d = new Dialog(mixView) {
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                    this.dismiss();
                return true;
            }
        };

        // 웹 뷰를 다이얼로그 연결한다
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setGravity(Gravity.BOTTOM);
        d.addContentView(webview, new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM));

        d.show();    // 다이얼로그 출력

        webview.loadUrl(url);    // 웹 뷰에 url 로드
    }

       // 데이터 소스 세팅
    public void setDataSource(DataSource.DATASOURCE source, Boolean selection) {
        selectedDataSources.put(source, selection);    // 선택된 데이터 소스의 상태를 세팅

      //  // 변경된 사항을 프레퍼런스에 세팅하고 적용한다
      //  SharedPreferences settings = getSharedPreferences(MixView.PREFS_NAME, 0);
      //  SharedPreferences.Editor editor = settings.edit();
      //  // 선택된거 넣고 이게 트룬지 펄슨지 체크한다.
      //  editor.putBoolean(source.toString(), selection);
      //  editor.commit();

        // setDataSource(source, !selectedDataSources.get(source));
    }

    // 특정 데이터 소스가 선택된 상태인지 리턴
    public Boolean isDataSourceSelected(DataSource.DATASOURCE source) {
        return selectedDataSources.get(source);
    }



    // 선택된 데이터 소스 리스트를 스트링 형태로 리턴
    public String getDataSourcesStringList() {
        String ret = "";    // 결과 스트링
        boolean first = true;    // 첫번째 항목인지 여부(쉼표 찍기위해 구분)
        // 데이터 소스들을 점검
        for (DataSource.DATASOURCE source : DataSource.DATASOURCE.values()) {
            if (isDataSourceSelected(source)) {    // 선택된 경우 결과에 추가
                if (!first) {
                    ret += ", ";
                }
                ret += source.toString();
                first = false;
            }
        }
        return ret;    // 결과 스트링을 리턴
    }

    // 마지막으로 다운로드된 위치를 리턴
    public Location getLocationAtLastDownload() {
        return locationAtLastDownload;
    }

    // 마지막으로 다운로드된 위치를 세팅
    public void setLocationAtLastDownload(Location locationAtLastDownload) {
        this.locationAtLastDownload = locationAtLastDownload;
    }


}
