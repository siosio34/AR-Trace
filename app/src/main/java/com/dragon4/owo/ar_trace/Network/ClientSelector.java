package com.dragon4.owo.ar_trace.Network;

import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.Model.User;

import java.util.List;

/**
 * Created by joyeongje on 2017. 1. 18..
 */

public interface ClientSelector {
    void uploadImageToServer(); // 파일 업로드 하는 함수
    void uploadTraceToServer(); // Trace 객체들을 올리는 함수
    List<Trace> getTraceDataFromServer(); // 서버 데이터들

    void registerUser(User user); // 회원등록
    void checkUser(String user_id); // 유저 아이디 존재여부체크
    User login(); // 로그인.

}
