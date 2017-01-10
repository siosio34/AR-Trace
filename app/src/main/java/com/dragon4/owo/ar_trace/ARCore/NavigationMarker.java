package com.dragon4.owo.ar_trace.ARCore;

import android.location.Location;

import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.ARCore.gui.PaintScreen;

/**
 * Created by joyeongje on 2017. 1. 9..
 */

public class NavigationMarker extends ARMarker {

    public static final int MAX_OBJECTS=1;	// 최대 객체 수

    // 네비게이션 마커 고정
    private String startingPoint; // 현재 시작점
    private String endingPoint; // 도착점
    private int time; // 총 걸리는 시간

    // 마커 제이썬 리스트 마다 다름.
    private String type; // 좌,우,직진,횡단보도
    private Integer xList; // x좌표리스트.
    private Integer yList; // y좌표리스트.
    private String guideList; // 가는 길 가이드 ex) 직선방향으로 5m!

    // 생성자. 타이틀, 위도, 경도, 고도, 그리고 URL과 데이터 소스를 인자로 받는다
    public NavigationMarker(String title, double latitude, double longitude,
                            double altitude, String URL, DataSource.DATASOURCE datasource) {
        super(title, latitude, longitude, altitude, URL, datasource);

    }

    // 마커 위치 갱신
    @Override
    public void update(Location curGPSFix) {

        super.update(curGPSFix);

        // TODO: 2017. 1. 9. 위치 업데이트
    }

    @Override
    public void draw(PaintScreen dw) {
        drawTextBlock(dw,datasource);

        if(isVisible) {
            // TODO: 2017. 1. 9. 비트맵 이미지 할당(되면은 3d도)
            // 방향에따라 분배
            switch (type) {
                case "straight":
                    break;
                case "left":
                    break;
                case "right":
                    break;
                case "road":
                    break;
                default:
                    break;
            }

        }

    }

    @Override
    public int getMaxObjects() {
        return MAX_OBJECTS;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getEndingPoint() {
        return endingPoint;
    }

    public void setEndingPoint(String endingPoint) {
        this.endingPoint = endingPoint;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Integer getxList() {
        return xList;
    }

    public void setxList(Integer xList) {
        this.xList = xList;
    }

    public Integer getyList() {
        return yList;
    }

    public void setyList(Integer yList) {
        this.yList = yList;
    }

    public String getGuideList() {
        return guideList;
    }

    public void setGuideList(String guideList) {
        this.guideList = guideList;
    }
}


