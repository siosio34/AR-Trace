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
 * edited by joyeongje on 2016. 12. 31..
 */

package com.dragon4.owo.ar_trace.ARCore.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.dragon4.owo.ar_trace.R;

import static com.google.firebase.analytics.FirebaseAnalytics.Event.SEARCH;

// 데이터 소스를 실질적으로 다루는 클래스
public class DataSource {


    // 데이터 소스와 데이터 포맷의 열거형 변수
    public enum DATASOURCE {
        CAFE,BUSSTOP,Convenience,Restaurant,BANK,HOSPITAL,ACCOMMODATION,DOCUMENT, IMAGE, VIDEO,SEARCH,NAVI
    };

    public enum DATAFORMAT {
        NAVER_CATEGORY,NAVER_SEARCH,NAVI,FIREBASE
    };

    public static Bitmap cafeIcon; //카페
    public static Bitmap busIcon; // 버스
    public static Bitmap restraurantIcon; // 레스토랑
    public static Bitmap convenienceIcon; // 편의점
    public static Bitmap bankIcon; // 은행
    public static Bitmap hospitalIcon; //병원
    public static Bitmap accommodationIcon; // 숙박

    public static Bitmap documentIcon;
    public static Bitmap imageIcon;
    public static Bitmap videoIcon;

    private static final String NAVER_MAP_URL =	"http://map.naver.com/findroute2/findWalkRoute.nhn?call=route2&output=json&coord_type=naver&search=0";

    public DataSource() {

    }

    // 리소스로부터 각 아이콘 생성
    public static void createIcons(Resources res) {

        cafeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_ar_cafe);
        busIcon = BitmapFactory.decodeResource(res,R.drawable.ic_ar_bus);
        restraurantIcon = BitmapFactory.decodeResource(res,R.drawable.ic_ar_restaurant);
        convenienceIcon = BitmapFactory.decodeResource(res,R.drawable.ic_ar_convenience_store);
        bankIcon = BitmapFactory.decodeResource(res,R.drawable.ic_ar_bank);
        hospitalIcon = BitmapFactory.decodeResource(res,R.drawable.ic_ar_hospital);
        accommodationIcon = BitmapFactory.decodeResource(res,R.drawable.ic_ar_lodgment);


    }


    // 아이콘 비트맵의 게터
    public static Bitmap getBitmap(String ds) {
        Bitmap bitmap = null;
        switch (ds) {
            case "CAFE":
                bitmap = cafeIcon;
                break;

            case "BUSSTOP":
                bitmap = busIcon;
                break;

            case "Convenience":
                bitmap = convenienceIcon;
                break;

            case "Restaurant":
                bitmap = restraurantIcon;
                break;

            case "BANK":
                bitmap = bankIcon;
                break;

            case "ACCOMMODATION":
                bitmap = accommodationIcon;
                break;

            case "HOSPITAL":
                bitmap = hospitalIcon;
                break;

        }
        return bitmap;
    }

    // 데이터 소스로부터 데이터 포맷을 추출
    public static DATAFORMAT dataFormatFromDataSource(DATASOURCE ds) {
        DATAFORMAT ret;
        switch (ds) {

            // 주위 편의시설
            case CAFE:
            case BUSSTOP: // 버스 정류장
            case Convenience:
            case Restaurant:
            case BANK:
            case HOSPITAL:
            case ACCOMMODATION:
                ret = DATAFORMAT.NAVER_CATEGORY;
                break;
            case SEARCH:
                ret = DATAFORMAT.NAVER_SEARCH;
                break;
            case NAVI:
                ret = DATAFORMAT.NAVI;
                break;
            // 파이어베이스 부분
            case DOCUMENT:
            case IMAGE:
            case VIDEO:
                ret = DATAFORMAT.FIREBASE;
                break;

            default:
                ret = DATAFORMAT.NAVER_CATEGORY;
                break;
        }
        return ret;    // 포맷 리턴
    }

    // 각 정보들로 완성된 URL 리퀘스트를 생성


    public static String createRequestCategoryURL(DATASOURCE source, double lat, double lon, double alt, float radius) {
        String ret = "";    // 결과 스트링

            // 각 소스에 따른 URL 리퀘스트를 완성한다
            switch (source) {

                // 네이버 웹페이지에서 가져오는 정보
                case CAFE:
                    ret = "http://map.naver.com/search2/interestSpot.nhn?type=CAFE&boundary=" + Double.toString(lon - 0.02) + "%3B" +
                            Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.02) +
                            "%3B" + Double.toString(lat + 0.01) + "&pageSize=100";
                    break;

                case BUSSTOP: // 버스 정류장
                    ret = "http://map.naver.com/search2/searchBusStopWithinRectangle.nhn?bounds="+ Double.toString(lon - 0.02) + "%3B" +
                            Double.toString(lat - 0.01) +"%3B" +  Double.toString(lon + 0.02) + "%3B"
                            + Double.toString(lat + 0.01) +"&count=100&level12";
                    break;

                case Convenience: // 편의점
                    ret = "http://map.naver.com/search2/interestSpot.nhn?type=STORE&boundary=" + Double.toString(lon - 0.02) + "%3B" +
                            Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.02) +
                            "%3B" + Double.toString(lat + 0.01) + "&pageSize=100";
                    break;

                case Restaurant: //식당
                    ret =  "http://map.naver.com/search2/interestSpot.nhn?type=DINING_KOREAN&boundary=" + Double.toString(lon - 0.02) + "%3B" +
                            Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.02) +
                            "%3B" + Double.toString(lat + 0.01) + "&pageSize=100";
                    break;


                case BANK: //은행
                    ret =  "http://map.naver.com/search2/interestSpot.nhn?type=BANK&boundary=" + Double.toString(lon - 0.02) + "%3B" +
                            Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.02) +
                            "%3B" + Double.toString(lat + 0.01) + "&pageSize=100";
                    break;

                case ACCOMMODATION:
                    ret =  "http://map.naver.com/search2/interestSpot.nhn?type=ACCOMMODATION&boundary=" + Double.toString(lon - 0.02) + "%3B" +
                            Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.02) +
                            "%3B" + Double.toString(lat + 0.01) + "&pageSize=100";
                    break;

                case HOSPITAL:
                    ret =  "http://map.naver.com/search2/interestSpot.nhn?type=HOSPITAL&boundary=" + Double.toString(lon - 0.02) + "%3B" +
                            Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.02) +
                            "%3B" + Double.toString(lat + 0.01) + "&pageSize=100";
                    break;
            }

        return ret;
    }

    public static String createBuildIDRequestURL(String _query) {
        String ret;
        ret = "http://192.168.1.41:8009/buildinginfo" + "?buildName=" + _query;
        Log.i("다음검색 url test",ret);
        return ret;

    }

    public static String createNaverSearchCallBackURL(String _query) {
        String ret;
        ret = "http://ac.map.naver.com/ac?q=" + _query +
                "&st=10&r_lt=10&r_format=json";
        return ret;
    }

    // 일정 장소 하나 검색
    public static String createNaverSearchRequestURL(String _query) {
        String ret;
        ret = "https://openapi.naver.com/v1/search/local.json?" +
                "query=" + _query +
                "&display=10&start=1&sort=random";

        Log.i("검색 url test",ret);

        return ret;
    }

    // 지도 경로 검색
    public static String createNaverMapRequestURL(double start_lon, double start_lat, double end_lon, double end_lat) {
        String ret;
        ret = NAVER_MAP_URL;

        ret += "&start=" + Double.toString(start_lon) + "%2C" + Double.toString(start_lat)
                + "&destination=" + Double.toString(end_lon) + "%2C" + Double.toString(end_lat);
        return ret;
    }

    /*

     case DOCUMENT:
                case IMAGE:
                case VIDEO:
                    ret = "https://tourseoul-451de.firebaseio.com/posts.json";
                    break;

                default:
                    ret = "https://tourseoul-451de.firebaseio.com/posts.json";
                    break;


     */

    // 각 소스에 따른 색을 리턴
    public static int getColor(DATASOURCE datasource) {
        int ret;
        switch (datasource) {

            default:
                ret = Color.GREEN;
                break;
        }
        return ret;
    }

}
