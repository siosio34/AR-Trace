package com.dragon4.owo.ar_trace.Model;

import java.util.Date;
import java.util.List;

/**
 * Created by joyeongje on 2016. 12. 31..
 */

public class User {

    private String userId;
    private String userName;
    private String userEmail;
    private String userImageURL;
    private String userToken;
    private List<TracePointer> userTraceList;

    private static User currentUser = new User();

    public static User getMyInstance() {
        return currentUser;
    }
    public static void setMyInstance(User user) { currentUser = user;}

    public User() {}

    public User(String userId, String userName, String userEmail, String userImageURL) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userImageURL = userImageURL;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public List<TracePointer> getUserTraceList() {
        return userTraceList;
    }

    public void setUserTraceList(List<TracePointer> userTraceList) {
        this.userTraceList = userTraceList;
    }
}
