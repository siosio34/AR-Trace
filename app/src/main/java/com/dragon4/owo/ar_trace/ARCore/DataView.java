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

import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import com.dragon4.owo.ar_trace.ARCore.Marker.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataHandler;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.ARCore.gui.PaintScreen;
import com.dragon4.owo.ar_trace.ARCore.gui.ScreenLine;
import com.dragon4.owo.ar_trace.ARCore.render.Camera;
import com.dragon4.owo.ar_trace.R;

import java.util.ArrayList;


/**
 * @author daniele & youngje
 */

public class DataView {

    /**
     * 현재 컨텍스트
     */
    private MixContext mixContext;

    /**
     * 뷰의 초기세팅 여부
     */
    private boolean isInit;

    /**
     * 뷰의 넓이와 높이
     */
    private int width, height;

    /**
     * 안드로이드 카메라가 아닌, 카메라 뷰의 변환 등을 다루는 객체
     **/
    private Camera cam;

    private MixState state = new MixState();    // 뷰의 현재 상태

    /**
     * 뷰는 디버그 목적으로 "얼어있울" 수 있다
     **/
    private boolean frozen;

    /**
     * 다운로드의 재시도 횟수
     */
    private int retry;

    private Location curFix;    // 수정된 현재 위치
    private DataHandler dataHandler = new DataHandler();    // 데이터 핸들러
    private float radius = 10;    // 검색 반경

    /**
     * MixView 클래스의 메뉴 항목과 메뉴 옵션들에 사용될 스트링의 상수값
     **/

    public static final int CONNECTION_ERROR_DIALOG_TEXT = R.string.connection_error_dialog;
    public static final int CONNECTION_ERROR_DIALOG_BUTTON1 = R.string.connection_error_dialog_button1;
    public static final int CONNECTION_ERROR_DIALOG_BUTTON2 = R.string.connection_error_dialog_button2;
    public static final int CONNECTION_ERROR_DIALOG_BUTTON3 = R.string.connection_error_dialog_button3;

    public static final int CONNECTION_GPS_DIALOG_TEXT = R.string.connection_GPS_dialog_text;    //이 메세지가 계속 뜬다.
    public static final int CONNECTION_GPS_DIALOG_BUTTON1 = R.string.connection_GPS_dialog_button1;
    public static final int CONNECTION_GPS_DIALOG_BUTTON2 = R.string.connection_GPS_dialog_button2;

    public static final int SEARCH_FAILED_NOTIFICATION = R.string.search_failed_notification;
    public static final int SOURCE_OPENSTREETMAP = R.string.source_openstreetmap;
    public static final int SEARCH_ACTIVE_1 = R.string.search_active_1;
    public static final int SEARCH_ACTIVE_2 = R.string.search_active_2;

    private boolean isLauncherStarted;    // 런쳐 시작 여부

    // UI에서 일어날 이벤트들의 리스트
    private ArrayList<UIEvent> uiEvents = new ArrayList<UIEvent>();

    private ScreenLine lrl = new ScreenLine();
    private ScreenLine rrl = new ScreenLine();
    private float rx = 10, ry = 20;
    private float dx = 30, dy = 40;
    private float addX = 0, addY = 0;

    /**
     * 생성자
     */
    public DataView(MixContext ctx) {
        this.mixContext = ctx;    // 데이터 뷰의 컨텍스트를 할당
    }

    // 컨텍스트를 리턴
    public MixContext getContext() {
        return mixContext;
    }

    // 런쳐 시작 여부를 리턴
    public boolean isLauncherStarted() {
        return isLauncherStarted;
    }

    // 얼어있는지 여부를 리턴
    public boolean isFrozen() {
        return frozen;
    }

    // 데이터 뷰의 얼림 설정
    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    // 검색 반경을 리턴
    public float getRadius() {
        return radius;
    }

    // 검색 반경을 세팅
    public void setRadius(float radius) {
        this.radius = radius;
    }

    // 데이터 핸들러를 리턴
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    // 디테일 뷰가 표시되었는지 리턴
    public boolean isDetailsView() {
        return state.isDetailsView();
    }

    // 디테일 뷰를 설정
    public void setDetailsView(boolean detailsView) {
        state.setDetailsView(detailsView);
    }

    // 데이터 뷰의 동작을 시작
    public void doStart() {
        state.nextLStatus = MixState.NOT_STARTED;    // 다음 상태를 지정 후
        mixContext.setLocationAtLastDownload(curFix);    // 현재의 위치를 마지막 다운로드 위치로
    }

    // 초기 세팅 여부를 리턴
    public boolean isInited() {
        return isInit;
    }

    // 초기 세팅 수행
    public void init(int widthInit, int heightInit) {
        try {
            width = widthInit;
            height = heightInit;

            // 인자로 받은 넓이, 높이로 카메라 객체를 생성하고
            cam = new Camera(width, height, true);
            cam.setViewAngle(Camera.DEFAULT_VIEW_ANGLE);    // 뷰의 각도를 설정한다

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        frozen = false;    // 얼려있는 상태를 해제하고
        isInit = true;    // 세팅 플래그를 true
    }

    // 데이터 요청
    public void requestData(String url, DataSource.DATAFORMAT dataformat, DataSource.DATASOURCE datasource) {
        DownloadRequest request = new DownloadRequest();    // 다운로드 요청 객체
        // 데이터 포맷과 소스, url 등을 할당한다
        request.format = dataformat;
        request.source = datasource;
        request.url = url;
        // 완성된 요청을 다운로더에 제출한다
        mixContext.getDownloader().submitJob(request);
        state.nextLStatus = MixState.PROCESSING;    // 다음 상태는 처리중으로
    }

    // 실제로 스크린에 그려주는 메소드
    public void draw(PaintScreen dw) {
        // 카메라 객체의 회전행렬에 컨텍스트의 회전행렬을 할당
        mixContext.getRM(cam.transform);
        // 수정된 현재 위치에 컨텍스트의 현재위치를 할당
        curFix = mixContext.getCurrentLocation();

        // 카메라 객체의 회전행렬로 장치각과 방위각을 계산
        state.calcPitchBearing(cam.transform);

        // Load Layer
        // 아직 시작되지 않은 상태이고, 데이터 뷰가 얼어있지 않은 경우
        if (state.nextLStatus == MixState.NOT_STARTED && !frozen) {
            // 컨텍스트의 시작 URL 이 할당 되었을 경우
            if (mixContext.getStartUrl().length() > 0) {
                //여기로 들어가게 해놨다
            }

            // URL 이 할당되지 않았을 경우에는
            else {
                // 현재의 위치로부터 위도, 경도, 고도 값을 읽고
                double lat = curFix.getLatitude(), lon = curFix.getLongitude(), alt = curFix.getAltitude();

                for (DataSource.DATASOURCE source : DataSource.DATASOURCE.values()) {
                    // 선택된 데이터 소스로 데이터 요청을 한다
                    if (mixContext.isDataSourceSelected(source))  { // 선택된것 ~
                            requestData(DataSource.createRequestCategoryURL(source, lat, lon, alt, radius), DataSource.dataFormatFromDataSource(source), source);
                            Log.i("데이터소스", source.toString());
                    }

                }

            }

            // 위의 절차를 거치고도 활성화 된 데이터 소스가 아무것도 없는 경우
            if (state.nextLStatus == MixState.NOT_STARTED)
                state.nextLStatus = MixState.DONE;    // 다음 상태는 완료 상태로


        } else if (state.nextLStatus == MixState.PROCESSING) {    // 처리중인 상태일 경우
            // 컨텍스트로부터 다운로드 관리자를 읽어옴
            DownloadManager dm = mixContext.getDownloader();
            DownloadResult dRes;    // 다운로드 결과

            // 다운로드 관리자에 등록된 모든 결과에 대해서
            while ((dRes = dm.getNextResult()) != null) {
                // 에러가 일어난 상황이지만 재시도 횟수가 2회 이하일 경우
                if (dRes.error && retry < 3) {
                    retry++;    // 재시도 횟수를 늘리고 에러 리퀘스트를 다시 제출한다
                    mixContext.getDownloader().submitJob(dRes.errorRequest);

                }

                // 에러가 없는 경우
                if (!dRes.error) {

                    // 데이터 핸들러에 마커를 추가 한다
                    Log.i(MixView.TAG, "Adding Markers");

                    dataHandler.addMarkers(dRes.getARMarkers());
                    dataHandler.onLocationChanged(curFix);    // 위치를 재설정
                    mixContext.mixView.drawCategoryMarkers(dRes.getARMarkers());

                }
            }
            if (dm.isDone()) {    // 다운로드 관리자의 작업이 끝난 경우
                retry = 0;
                // 재시도 횟수 초기화
                state.nextLStatus = MixState.DONE;    // 다음 상태는 완료로
            }
        }
		/* 마커 업데이트 */
        dataHandler.updateActivationStatus(mixContext);    // 활성화 상태를 갱신


        // 각각의 마커에 적용
        //Log.i("마커들 갯수",Integer.toString(dataHandler.getMarkerCount()));

        for (int i = dataHandler.getMarkerCount() - 1; i >= 0; i--) {
            ARMarker ma = dataHandler.getMarker(i);

                // 마커 활성화 그리고 일정반경안이면 업데이트.
                if (ma.isActive() && (ma.getDistance() / 1000f < radius)) {

                    //addX, addY는 화면에 찍히는 좌표, 더오른쪽으로 할거면 x를 더하고 더 높이 띄울거면 y를 더한다
                    if (!frozen) {
                        ma.calcPaint(cam, addX, addY-100);
                    }

                    ma.draw(dw);
                }
        }




		/* 다음 UI 이벤트를 받는다 */
        UIEvent evt = null;
        synchronized (uiEvents) {
            if (uiEvents.size() > 0) {
                evt = uiEvents.get(0);
                uiEvents.remove(0);
            }
        }
        // 이벤트의 처리
        if (evt != null) {
            switch (evt.type) {
                case UIEvent.CLICK:
                    handleClickEvent((ClickEvent) evt);
                    break;
            }
        }
        state.nextLStatus = MixState.PROCESSING;    // 다음 상태는 처리중으로
    }


    // 클릭 이벤트 제어
    boolean handleClickEvent(ClickEvent evt) {
        boolean evtHandled = false;    // 이벤트 처리중 플래그

        // Handle event
        if (state.nextLStatus
                == MixState.DONE) {    // 다음상태가 완료일 때(다운로드 완료)

            // 일치하는 첫 번째 마커가 이벤트를 작동할 것이다
            for (int i = 0; i < dataHandler.getMarkerCount() && !evtHandled; i++) {
                ARMarker pm = dataHandler.getMarker(i);

                // 클릭 이벤트 처리를 시도한다. ARMarker, MixState 를 참고
                evtHandled = pm.fClick(evt.x, evt.y, mixContext, state);

            }
        }
        return evtHandled;    // 성공했을 경우 true 를 리턴
    }


    // 클릭 이벤트의 처리. UI 이벤트 리스트에 추가한다
    public void clickEvent(float x, float y) {
        synchronized (uiEvents) {
            uiEvents.add(new ClickEvent(x, y));
        }
    }

    // 키 이벤트의 처리. UI 이벤트 리스트에 추가한다
    public void keyEvent(int keyCode) {
        synchronized (uiEvents) {
            uiEvents.add(new KeyEvent(keyCode));
        }
    }

    // 리스트에 등록된 이벤트들을 클리어
    public void clearEvents() {
        synchronized (uiEvents) {
            uiEvents.clear();
        }
    }
}

// UI 이벤트 클래스
class UIEvent {
    // 싱크를 제어하기 위해 상수로 구분
    public static final int CLICK = 0;
    public static final int KEY = 1;

    public int type;    // 타입을 저장
}

// 클릭 이벤트 클래스
class ClickEvent extends UIEvent {
    public float x, y;    // 클릭된 좌표

    // 생성자. 타입과 좌표를 지정
    public ClickEvent(float x, float y) {
        this.type = CLICK;
        this.x = x;
        this.y = y;
    }

    // 문자열 형태로 이벤트의 내용을 변환
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

// 키 이벤트 클래스
class KeyEvent extends UIEvent {
    public int keyCode;    // 입력된 키 코드

    // 생성자. 타입과 키 코드를 지정
    public KeyEvent(int keyCode) {
        this.type = KEY;
        this.keyCode = keyCode;
    }

    // 문자열 형태로 이벤트의 내용을 변환
    @Override
    public String toString() {
        return "(" + keyCode + ")";
    }
}
