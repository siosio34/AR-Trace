package com.dragon4.owo.ar_trace.ARCore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by joyeongje on 2016. 9. 13..
 */
public class Document {

    public Document() {

    }

    public Document(int documentId, String userId, String content, int markerType, String state, String contentUrl,
             int contentType, int popularity, int responseWithme, int responseSeeyou, int responseNotgood,
             int commentNum, int readNum, Date createDate, Date updateDate, List<Comment> commentList, Double lat, Double lon)  {
        this.documentId = documentId;
        this.userId = userId;
        this.content = content;
        this.markerType = markerType;
        this.state = state;
        this.contentUrl = contentUrl;
        this.contentType = contentType;
        this.popularity = popularity;
        this.responseWithme = responseWithme;
        this.responseSeeyou = responseSeeyou;
        this.responseNotgood = responseNotgood;
        this.commentNum = commentNum;
        this.readNum = readNum;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.commentList = commentList;
        this.lat = lat;
        this.lon = lon;
    }

    private int documentId;
    private String userId;
    private String content; // 글내용
    private int markerType;  // 종합해서 넣을거
    private String state; // 상태 삭제된건지 아닌지
    private String contentUrl; // firebase 에서 가져올 컨텐츠 유알엘
    private int contentType; // 글, 사진, 동영상
    private int popularity; // 인기도(다 합친것)
    private int responseWithme; // 함께해요
    private int responseSeeyou; // 또봐요
    private int responseNotgood; // 별로에요
    private int commentNum; // 댓글수
    private int readNum; // 조회수
    private Date createDate; // 작성일
    private Date updateDate; // 수정일
    private List<Comment> commentList = new ArrayList<Comment>();
    private Double lat;
    private Double lon;

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMarkerType() {
        return markerType;
    }

    public void setMarkerType(int markerType) {
        this.markerType = markerType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getResponseWithme() {
        return responseWithme;
    }

    public void setResponseWithme(int responseWithme) {
        this.responseWithme = responseWithme;
    }

    public int getResponseSeeyou() {
        return responseSeeyou;
    }

    public void setResponseSeeyou(int responseSeeyou) {
        this.responseSeeyou = responseSeeyou;
    }

    public int getResponseNotgood() {
        return responseNotgood;
    }

    public void setResponseNotgood(int responseNotgood) {
        this.responseNotgood = responseNotgood;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

}
