package com.dragon4.owo.ar_trace.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by joyeongje on 2016. 9. 13..
 */
public class Comment implements Serializable {

    private int commentId;
    private int documentId;
    private String userId;
    private String userName;
    private String userImageUrl;
    private String content;
    private Date createDate;
    private int state;

    public Comment() {

    }

    public Comment(int commentId,int documentId,String userId, String userName,String userImageUrl,String content, Date createDate,int state)
    {
        this.commentId = commentId;
        this.documentId = documentId;
        this.userId = userId;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.content = content;
        this.createDate = createDate;
        this.state = state;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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
}
