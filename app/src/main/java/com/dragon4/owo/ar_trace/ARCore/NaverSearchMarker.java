package com.dragon4.owo.ar_trace.ARCore;

import com.dragon4.owo.ar_trace.ARCore.data.DataSource;

/**
 * Created by joyeongje on 2017. 1. 25..
 */

public class NaverSearchMarker extends ARMarker {

    public static final int MAX_OBJECTS=20;	// 최대 객체 수

    private String category;
    private String telephone;
    private String address;
    private String roadAddress;

    public NaverSearchMarker(String title, double latitude, double longitude,
                          double altitude, String URL, DataSource.DATASOURCE datasource,
                             String category,String telephone,String address, String roadAddress) {
        super(title, latitude, longitude, altitude, URL, datasource);
        this.category = category;
        this.telephone = telephone;
        this.address = address;
        this.roadAddress = roadAddress;

    }
    @Override
    public int getMaxObjects() {
        return MAX_OBJECTS;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }
}
