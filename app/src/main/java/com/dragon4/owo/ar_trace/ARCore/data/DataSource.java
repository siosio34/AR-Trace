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

package com.dragon4.owo.ar_trace.ARCore.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.dragon4.owo.ar_trace.R;

// 데이터 소스를 실질적으로 다루는 클래스
public class DataSource {


    // 데이터 소스와 데이터 포맷의 열거형 변수
    public enum DATASOURCE {
        CAFE,BUSSTOP,Convenience,Restaurant,BANK,HOSPITAL,ACCOMMODATION,DOCUMENT, IMAGE, VIDEO
    };

    public enum DATAFORMAT {
        CAFE,BUSSTOP,Convenience,Restaurant,BANK,HOSPITAL,ACCOMMODATION,DOCUMENT, IMAGE, VIDEO
    };

    // 주의할것! 방대한 양의 데이터(MB단위 이상)을 산출할 때에는, 작은 반경이나 특정한 쿼리만을 사용해야한다
    /**
     * URL 부분 끝
     */

    // 아이콘들. 트위터와 버즈

    public static Bitmap cafeIcon; //카페
    public static Bitmap busIcon; // 버스
    public static Bitmap restraurantIcon; // 레스토랑
    public static Bitmap convenienceIcon; // 편의점

    // TODO: 2016. 12. 11. 아래 세개거 추가

    public static Bitmap bankIcon; // 은행
    public static Bitmap hospitalIcon; //병원
    public static Bitmap accommodationIcon; // 숙박

    // TODO: 2016. 12. 11. 도큐먼트 뿐만아니라 다른것도 분리. 

    public static Bitmap documentIcon;
    public static Bitmap imageIcon;
    public static Bitmap videoIcon;


    public DataSource() {

    }

    // 리소스로부터 각 아이콘 생성
    public static void createIcons(Resources res) {

        cafeIcon = BitmapFactory.decodeResource(res, R.drawable.icon_cafe);
       // busIcon = BitmapFactory.decodeResource(res,R.drawable.icon_metro);
       // restraurantIcon = BitmapFactory.decodeResource(res,R.drawable.icon_cafe);
       // convenienceIcon = BitmapFactory.decodeResource(res,R.drawable.icon_convenition);
       // bankIcon = BitmapFactory.decodeResource(res,R.drawable.icon_bank);
       // hospitalIcon = BitmapFactory.decodeResource(res,R.drawable.icon_hospital);
       // accommodationIcon = BitmapFactory.decodeResource(res,R.drawable.icon_hotel);
//
       // // TODO: 2016. 12. 11. icon 들 변경해야됨...
       // // 이미지 영상, 비디오 이미지 구해야됨 크기는 40 * 60 정도
       // documentIcon = BitmapFactory.decodeResource(res, R.drawable.ar_document_document_marker);
       // imageIcon = BitmapFactory.decodeResource(res,R.drawable.ar_document_image_marker);
       // videoIcon = BitmapFactory.decodeResource(res,R.drawable.ar_document_vidoe_marker);

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

            case "CONVENIENCE":
                bitmap = convenienceIcon;
                break;

            case "RESTRAUNT":
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

            case "DOCUMENT":
                bitmap = documentIcon;
                break;
            case "IMAGE":
                bitmap = imageIcon;
                break;
            case "VIDEO":
                bitmap = videoIcon;
                break;


        }
        return bitmap;
    }

    // 데이터 소스로부터 데이터 포맷을 추출
    public static DATAFORMAT dataFormatFromDataSource(DATASOURCE ds) {
        DATAFORMAT ret = DATAFORMAT.CAFE;
        // 소스 형식에 따라 포맷을 할당한다
        switch (ds) {

            // 주위 편의시설
            case CAFE:
                ret = DATAFORMAT.CAFE;
                break;

            case BUSSTOP: // 버스 정류장
                ret = DATAFORMAT.BUSSTOP;
                break;

            case Convenience:
                ret = DATAFORMAT.Convenience;
                break;

            case Restaurant:
                ret = DATAFORMAT.Restaurant;
                break;

            case BANK:
                ret = DATAFORMAT.BANK;
                break;
            
            case HOSPITAL:
                ret = DATAFORMAT.HOSPITAL;
                break;

            case ACCOMMODATION:
                ret = DATAFORMAT.ACCOMMODATION;
                break;
            
            //  파이어베이스 부분
            case DOCUMENT:
                ret = DATAFORMAT.DOCUMENT;
                break;

            case IMAGE:
                ret = DATAFORMAT.IMAGE;
                break;

            case VIDEO:
                ret = DATAFORMAT.VIDEO;
                break;

            default:
                ret = DATAFORMAT.DOCUMENT;
                break;


        }
        return ret;    // 포맷 리턴
    }


    // 각 정보들로 완성된 URL 리퀘스트를 생성
    public static String createRequestURL(DATASOURCE source, double lat, double lon, double alt, float radius) {
        String ret = "";    // 결과 스트링

        // https://dinosaur-facts.firebaseio.com/
        // https://dinosaur-facts.firebaseio.com/dinosaurs.json?orderBy="height"&startAt=3&print=pretty
        // 파일로부터 읽는 것이 아니라면
        if (!ret.startsWith("file://")) {

            // 각 소스에 따른 URL 리퀘스트를 완성한다
            switch (source) {
                // TODO: 2016. 9. 9. yj  json 작업해야됨

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


                case DOCUMENT:
                case IMAGE:
                case VIDEO:
                    ret = "https://tourseoul-451de.firebaseio.com/posts.json";
                    break;

                default:
                    ret = "https://tourseoul-451de.firebaseio.com/posts.json";
                    break;

            }

        }

        return ret;
    }

    private static final String NAVER_MAP_URL =	"http://map.naver.com/findroute2/findWalkRoute.nhn?call=route2&output=json&coord_type=naver&search=0";

    public static String createNaverMapRequestURL(double start_lon, double start_lat, double end_lon, double end_lat) {
        String ret = ""; // 결과 스트링
        ret = NAVER_MAP_URL;

        ret += "&start=" + Double.toString(start_lon) + "%2C" + Double.toString(start_lat)
                + "&destination=" + Double.toString(end_lon) + "%2C" + Double.toString(end_lat);

        return ret;
    }


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
