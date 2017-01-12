package com.dragon4.owo.ar_trace.ARCore;

import android.location.Location;

import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.ARCore.gui.PaintScreen;

/**
 * Created by joyeongje on 2017. 1. 9..
 */

public class NavigationMarker extends ARMarker {

    public static final int MAX_OBJECTS=1;	// 최대 객체 수

    private int time; // 총 걸리는 시간
    private int totalDistance; // 총 거리
    // 마커 제이썬 리스트 마다 다름.
    private String type; // 좌,우,직진,횡단보도

    private String guide; // 가는 길 가이드 ex) 직선방향으로 5m!
    private int x;
    private int y;

    // 생성자. 타이틀, 위도, 경도, 고도, 그리고 URL과 데이터 소스를 인자로 받는다
    public NavigationMarker(String title, double latitude, double longitude,
                            double altitude, String URL, DataSource.DATASOURCE datasource) {
        super(title, latitude, longitude, altitude, URL, datasource);

    }

    // 마커 위치 갱신
    @Override
    public void update(Location curGPSFix) {

        // super.update(curGPSFix);
        // TODO: 2017. 1. 12.  ar로 방향보여줄거면 작업하면됨.
        // TODO: 2017. 1. 9. 위치 업데이트
    }

    @Override
    public void draw(PaintScreen dw) {
        // TODO: 2017. 1. 12.  ar로 방향보여줄거면 작업하면됨.


       // drawTextBlock(dw,datasource);
//
       // if(isVisible) {
       //     // TODO: 2017. 1. 9. 비트맵 이미지 할당(되면은 3d도)
       //     // 방향에따라 분배
       //     switch (type) {
       //         case "straight":
       //             break;
       //         case "left":
       //             break;
       //         case "right":
       //             break;
       //         case "road":
       //             break;
       //         default:
       //             break;
       //     }
//
       // }
    }

    @Override
    public int getMaxObjects() {
        return MAX_OBJECTS;
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

    public String getGuideList() {
        return guide;
    }

    public void setGuideList(String guide) {
        this.guide = guide;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}


