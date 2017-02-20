package com.dragon4.owo.ar_trace.Model;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joyeongje on 2016. 12. 31..
 */

public class Trace {

    private String locationID; // 장소 id 부모노드가 될예정
    private String traceID; // 흔적 id 이미지 아이디 가 될예정
    private String content; // 글 내용
    private String imageURL;
    private String thumbnailURL; // 썸네일 유알엘
    private int likeNum; // 좋아요 갯수
    private double lat;
    private double lon;
    private String placeName; // 주소를 경도 위도로
    private long writeDate; // 글쓴시간

    private String userImageUrl; // user Image URL
    private String userName; // 유저이름
    private String userToken; // 유저토큰
    private String userId; // 유저아이디

    @Exclude
    private HashMap<String, Boolean> likeUserList;

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public long getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(long writeDate) {
        this.writeDate = writeDate;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public HashMap<String, Boolean> getLikeUserList() {
        return likeUserList;
    }

    public void setLikeUserList(HashMap<String, Boolean> likeUserList) {
        this.likeUserList = likeUserList;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
