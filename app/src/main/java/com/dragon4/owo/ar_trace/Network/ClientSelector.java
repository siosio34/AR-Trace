package com.dragon4.owo.ar_trace.Network;

import android.content.Context;

import com.dragon4.owo.ar_trace.ARCore.Activity.TraceRecyclerViewAdapter;
import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.Model.User;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by joyeongje on 2017. 1. 18..
 */

public interface ClientSelector {
    void uploadUserDataToServer(User user, final Context googleSignInContext);
    void uploadImageToServer(Trace trace, File file); // 파일 업로드 하는 함수
    void uploadTraceToServer(Trace trace); // Trace 객체들을 올리는 함수
    void sendTraceLikeToServer(final boolean isLikeClicked, Trace trace); //
    ArrayList<Trace> getTraceDataFromServer(String traceKey, TraceRecyclerViewAdapter mAdapter); // 서버 데이터들
}