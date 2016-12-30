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

import android.util.Log;

import com.dragon4.owo.ar_trace.ARCore.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.MixView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

// JSON 파일을 다루는 클래스
public class Json extends DataHandler {

    public static final int MAX_JSON_OBJECTS = 100;    // JSON 객체의 최대 수

    // 각종 데이터를 로드
    public List<ARMarker> load(JSONObject root, DataSource.DATAFORMAT dataformat) {
        // 데이터를 읽는데 사용할 JSON 객체와 데이터행렬, 마커들
        JSONObject jo = null;
        JSONArray dataArray = null;
        List<ARMarker> markers = new ArrayList<ARMarker>();
        ARMarker.markersList.clear();

        try {
            if (root.has("result") && root.getJSONObject("result").has("site")) // 연결 가능한 링크를 가졌을시
                dataArray = root.getJSONObject("result").getJSONArray("site");

            else if(root.has("result") && root.getJSONObject("result").has("station") ) { // 버스, 역 마커일경우 따로 처리해줘야됨
                dataArray = root.getJSONObject("result").getJSONArray("station");
            }

            else { // 파이어 베이스 hasymap을 jsonArray 로 변경해주는 코드
                String jsonArr = "[";
                Iterator iterator = root.keys();
                while(iterator.hasNext()) {
                    String key = (String)iterator.next();
                    JSONObject data = root.getJSONObject(key);
                    jsonArr+=data.toString();
                    jsonArr+=",";
                }
                jsonArr = jsonArr.substring(0, jsonArr.length()-1)+"]";
                dataArray = new JSONArray(jsonArr.toString());

            }

            // 데이터행렬에 데이터들이 있다면
            if (dataArray != null) {
                // 일단 로그 생성. 데이터 포맷을 기록한다
                Log.i(MixView.TAG, "processing " + dataformat + " JSON Data Array");
                // 최대 객체 수와 실제 데이터 길이를 비교해 최소치를 탑으로 지정
                int top = Math.min(MAX_JSON_OBJECTS, dataArray.length());

                // 각 데이터들에 대한 처리
                for (int i = 0; i < top; i++) {
                    // 처리할 JSON 객체를 할당
                    jo = dataArray.getJSONObject(i);
                    Log.i("JSON값", jo.toString());

                    ARMarker ma = null;
                    // 데이터 포맷에 따른 처리
                    switch (dataformat) {

                        case CAFE:
                            ma = processCAFEJSONObject(jo);
                            break;

                        case BUSSTOP:
                            ma = processBusJSONObject(jo);
                            break;

                        case Restaurant:
                            ma = processRestaurantJSONObject(jo);
                            break;

                        case Convenience:
                            ma = processConvenienceJSONObject(jo);
                            break;

                        case BANK:
                            ma = processBankJSONObject(jo);
                            break;

                        case HOSPITAL:
                            ma = processHospitalJSONObject(jo);
                            break;

                        case ACCOMMODATION:
                            ma = processAccomdationJSONObject(jo);
                            break;

                        case DOCUMENT:
                        case VIDEO:
                        case IMAGE:
                            ma = processDocumentObject(jo);
                            break;

                    }
                    // 마커 추가
                    if (ma != null) {
                        markers.add(ma);
                        ARMarker.markersList.add(ma);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 모든 마커가 추가된 리스트를 리턴
        return markers;
    }

    public ARMarker processBusJSONObject(JSONObject jo) throws JSONException {

        ARMarker ma = null;

        if (jo.has("x") && jo.has("y") && jo.has("stationDisplayID") && jo.has("stationDisplayName")) {// 버스정류장 정보일 경우

            String link = jo.getString("stationDisplayID");
            String[] sTemp = link.split("-");
            link = "";
            String tempLink;

            for(int i =0 ; i<sTemp.length; i++) {
                link += sTemp[i];
            }

            tempLink = ("http://lab.khlug.org/manapie/bus_arrival.php?station=" + link);
            Log.i("linkbasic",tempLink);

            ma = new SocialARMarker(
                    jo.getString("stationDisplayName"),
                    jo.getDouble("y"),
                    jo.getDouble("x"),
                    0,
                    tempLink,
                    DataSource.DATASOURCE.BUSSTOP,"BUSSTOP");
            // TODO: 2016-05-31 이 부분은 나중에 웹페이지에 연결을 하던지 하자

        }
        return ma;
    }

    public ARMarker processBankJSONObject(JSONObject jo)  throws JSONException {
        ARMarker ma = null;


        // 형식에 맞는지 검사. 타이틀과 위도, 경도, 고도 태그를 찾는다
        if (jo.has("x") && jo.has("y") && jo.has("name")) {
            Log.v(MixView.TAG, "processing Mixare JSON object");    // 로그 출력

            String linkTemp = null;
            linkTemp = jo.getString("id");

            String linkbasic = "http://map.naver.com/local/siteview.nhn?code=" + linkTemp.substring(1);
            Log.i("linkbasic",linkbasic);

            // 할당된 값들로 마커 생성, // 일단은 경도, 위도, 이름만.
            // 맨뒤에값은 플래그 일단 Flag 0 는 카페정보
            ma = new SocialARMarker(
                    jo.getString("name"),
                    jo.getDouble("y"),
                    jo.getDouble("x"),
                    0,
                    linkbasic,
                    DataSource.DATASOURCE.BANK,"BANK");
        }
        return ma;    // 마커 리턴
    }

    public ARMarker processHospitalJSONObject(JSONObject jo)  throws JSONException {
        ARMarker ma = null;


        // 형식에 맞는지 검사. 타이틀과 위도, 경도, 고도 태그를 찾는다
        if (jo.has("x") && jo.has("y") && jo.has("name")) {
            Log.v(MixView.TAG, "processing Mixare JSON object");    // 로그 출력

            String linkTemp = null;
            linkTemp = jo.getString("id");

            String linkbasic = "http://map.naver.com/local/siteview.nhn?code=" + linkTemp.substring(1);
            Log.i("linkbasic",linkbasic);

            // 할당된 값들로 마커 생성, // 일단은 경도, 위도, 이름만.
            // 맨뒤에값은 플래그 일단 Flag 0 는 카페정보
            ma = new SocialARMarker(
                    jo.getString("name"),
                    jo.getDouble("y"),
                    jo.getDouble("x"),
                    0,
                    linkbasic,
                    DataSource.DATASOURCE.HOSPITAL,"HOSPITAL");
        }
        return ma;    // 마커 리턴
    }

    public ARMarker processAccomdationJSONObject(JSONObject jo)  throws JSONException {
        ARMarker ma = null;


        // 형식에 맞는지 검사. 타이틀과 위도, 경도, 고도 태그를 찾는다
        if (jo.has("x") && jo.has("y") && jo.has("name")) {
            Log.v(MixView.TAG, "processing Mixare JSON object");    // 로그 출력

            String linkTemp = null;
            linkTemp = jo.getString("id");

            String linkbasic = "http://map.naver.com/local/siteview.nhn?code=" + linkTemp.substring(1);
            Log.i("linkbasic",linkbasic);

            // 할당된 값들로 마커 생성, // 일단은 경도, 위도, 이름만.
            // 맨뒤에값은 플래그 일단 Flag 0 는 카페정보
            ma = new SocialARMarker(
                    jo.getString("name"),
                    jo.getDouble("y"),
                    jo.getDouble("x"),
                    0,
                    linkbasic,
                    DataSource.DATASOURCE.ACCOMMODATION,"ACCOMMODATION");
        }
        return ma;    // 마커 리턴
    }



    public ARMarker processConvenienceJSONObject(JSONObject jo)  throws JSONException {
        ARMarker ma = null;


        // 형식에 맞는지 검사. 타이틀과 위도, 경도, 고도 태그를 찾는다
        if (jo.has("x") && jo.has("y") && jo.has("name")) {
            Log.v(MixView.TAG, "processing Mixare JSON object");    // 로그 출력

            String linkTemp = null;
            linkTemp = jo.getString("id");

            String linkbasic = "http://map.naver.com/local/siteview.nhn?code=" + linkTemp.substring(1);
            Log.i("linkbasic",linkbasic);

            // 할당된 값들로 마커 생성, // 일단은 경도, 위도, 이름만.
            // 맨뒤에값은 플래그 일단 Flag 0 는 카페정보
            ma = new SocialARMarker(
                    jo.getString("name"),
                    jo.getDouble("y"),
                    jo.getDouble("x"),
                    0,
                    linkbasic,
                    DataSource.DATASOURCE.Convenience,"CONVENICE");
        }
        return ma;    // 마커 리턴
    }

    public ARMarker processRestaurantJSONObject(JSONObject jo) throws JSONException {
        ARMarker ma = null;

        // 형식에 맞는지 검사. 타이틀과 위도, 경도, 고도 태그를 찾는다
        if (jo.has("x") && jo.has("y") && jo.has("name")) {

            String linkTemp = null;
            linkTemp = jo.getString("id");

            String linkbasic = "http://map.naver.com/local/siteview.nhn?code=" + linkTemp.substring(1);
            Log.i("linkbasic",linkbasic);


            // 할당된 값들로 마커 생성, // 일단은 경도, 위도, 이름만.
            // 맨뒤에값은 플래그 일단 Flag 0 는 카페정보
            ma = new SocialARMarker(
                    jo.getString("name"),
                    jo.getDouble("y"),
                    jo.getDouble("x"),
                    0,
                    linkbasic,
                    DataSource.DATASOURCE.Restaurant, "RESTRAUNT");
        }
        return ma;    // 마커 리턴
    }

    private ARMarker processDocumentObject(JSONObject jo) throws JSONException {

        ARMarker ma = null;
        int contentType = jo.getInt("contentType");
        DataSource.DATASOURCE thisDatasource = DataSource.DATASOURCE.DOCUMENT;

        long documentCreateDate = jo.getJSONObject("createDate").getLong("time");
        Date createdate = new Date( documentCreateDate );
        long documentEditDate = jo.getJSONObject("updateDate").getLong("time");
        Date editDdate = new Date( documentEditDate );

        String contentUrl = null;
        List<Comment> comments = new ArrayList<Comment>();

        if(contentType == 1)
            thisDatasource = DataSource.DATASOURCE.IMAGE;

        else if(contentType == 2)
            thisDatasource = DataSource.DATASOURCE.VIDEO;

        if(jo.has("contentUrl"))
            contentUrl = jo.getString("contentUrl");

        if(jo.has("commentList")) {
            JSONArray commentArray = jo.getJSONArray("commentList");
            Log.i("댓글 목록이다.",commentArray.toString());
            for(int i = 0 ; i < commentArray.length(); i++) {
                Comment loadComment = new Comment();
                JSONObject jobj = commentArray.getJSONObject(i);
                loadComment.setCommentId(jobj.getInt("commentId"));
                loadComment.setContent(jobj.getString("content"));
                long commentEditDate = jobj.getJSONObject("createDate").getLong("time");
                Date commentDdate = new Date( commentEditDate );
                loadComment.setCreateDate(commentDdate);
                loadComment.setUserId(jobj.getString("userId"));
                loadComment.setUserName(jobj.getString("userName"));
                loadComment.setUserImageUrl(jobj.getString("userImageUrl"));
                comments.add(loadComment);

            }
        }

        ma = new DocumentARMarker(jo.getString("content"),jo.getDouble("lat"),jo.getDouble("lon"),0,contentUrl,thisDatasource,jo.getInt("documentId"),jo.getString("userId"),contentType,
                jo.getInt("popularity"),jo.getInt("responseWithme"),jo.getInt("responseSeeyou"),jo.getInt("responseNotgood"),jo.getInt("commentNum"),jo.getInt("readNum"),createdate,
                editDdate,comments);


        return ma;

    }


    public ARMarker processCAFEJSONObject(JSONObject jo)  throws JSONException {
        ARMarker ma = null;

       // 형식에 맞는지 검사. 타이틀과 위도, 경도, 고도 태그를 찾는다
       if (jo.has("x") && jo.has("y") && jo.has("name")) {

           String linkTemp = null;
           linkTemp = jo.getString("id");
           String linkbasic = "http://map.naver.com/local/siteview.nhn?code=" + linkTemp.substring(1);
           Log.i("linkbasic",linkbasic);

           // 할당된 값들로 마커 생성, // 일단은 경도, 위도, 이름만.
           // 맨뒤에값은 플래그 일단 Flag 0 는 카페정보
           // link 이거 url
           ma = new SocialARMarker(
                   jo.getString("name"),
                   jo.getDouble("y"),
                   jo.getDouble("x"),
                   0,
                   linkbasic,
                   DataSource.DATASOURCE.CAFE, "CAFE");
       }
       return ma;    // 마커 리턴
   }


    // html 엔트리의 해쉬맵
    private static HashMap<String, String> htmlEntities;

    static {
        htmlEntities = new HashMap<String, String>();
        htmlEntities.put("&lt;", "<");
        htmlEntities.put("&gt;", ">");
        htmlEntities.put("&amp;", "&");
        htmlEntities.put("&quot;", "\"");
        htmlEntities.put("&agrave;", "à");
        htmlEntities.put("&Agrave;", "À");
        htmlEntities.put("&acirc;", "â");
        htmlEntities.put("&auml;", "ä");
        htmlEntities.put("&Auml;", "Ä");
        htmlEntities.put("&Acirc;", "Â");
        htmlEntities.put("&aring;", "å");
        htmlEntities.put("&Aring;", "Å");
        htmlEntities.put("&aelig;", "æ");
        htmlEntities.put("&AElig;", "Æ");
        htmlEntities.put("&ccedil;", "ç");
        htmlEntities.put("&Ccedil;", "Ç");
        htmlEntities.put("&eacute;", "é");
        htmlEntities.put("&Eacute;", "É");
        htmlEntities.put("&egrave;", "è");
        htmlEntities.put("&Egrave;", "È");
        htmlEntities.put("&ecirc;", "ê");
        htmlEntities.put("&Ecirc;", "Ê");
        htmlEntities.put("&euml;", "ë");
        htmlEntities.put("&Euml;", "Ë");
        htmlEntities.put("&iuml;", "ï");
        htmlEntities.put("&Iuml;", "Ï");
        htmlEntities.put("&ocirc;", "ô");
        htmlEntities.put("&Ocirc;", "Ô");
        htmlEntities.put("&ouml;", "ö");
        htmlEntities.put("&Ouml;", "Ö");
        htmlEntities.put("&oslash;", "ø");
        htmlEntities.put("&Oslash;", "Ø");
        htmlEntities.put("&szlig;", "ß");
        htmlEntities.put("&ugrave;", "ù");
        htmlEntities.put("&Ugrave;", "Ù");
        htmlEntities.put("&ucirc;", "û");
        htmlEntities.put("&Ucirc;", "Û");
        htmlEntities.put("&uuml;", "ü");
        htmlEntities.put("&Uuml;", "Ü");
        htmlEntities.put("&nbsp;", " ");
        htmlEntities.put("&copy;", "\u00a9");
        htmlEntities.put("&reg;", "\u00ae");
        htmlEntities.put("&euro;", "\u20a0");
    }

    // HTML 아스키 값들을 다시 복원. 변환할 소스와 시작점을 인자로 받는다
    public String unescapeHTML(String source, int start) {
        int i, j;    // 임시 변수

        // &와 ;의 위치로 값들을 읽는다
        i = source.indexOf("&", start);
        if (i > -1) {
            j = source.indexOf(";", i);
            if (j > i) {
                // 검색된 위치에서 값을 읽어옴
                String entityToLookFor = source.substring(i, j + 1);
                String value = (String) htmlEntities.get(entityToLookFor);

                // 값이 있을 시 복원작업 시작. 재귀호출 이용
                if (value != null) {
                    source = new StringBuffer().append(source.substring(0, i))
                            .append(value).append(source.substring(j + 1))
                            .toString();
                    return unescapeHTML(source, i + 1); // recursive call
                }
            }
        }
        return source;    // 복원된 소스 리턴
    }
}

