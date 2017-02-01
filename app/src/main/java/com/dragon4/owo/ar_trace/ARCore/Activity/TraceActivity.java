package com.dragon4.owo.ar_trace.ARCore.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dragon4.owo.ar_trace.ARCore.ReviewRecyclerViewAdapter;
import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.Network.ClientSelector;
import com.dragon4.owo.ar_trace.Network.Firebase.FirebaseClient;
import com.dragon4.owo.ar_trace.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedInputStream;

/**
 * Created by Mansu on 2017-01-25.
 */

public class TraceActivity extends Activity {

    ClientSelector clientSelector;

     private ArrayList<Trace> traceList;
     public ReviewRecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_review);

        Intent reviewIntent = getIntent();
        String reviewTitle = reviewIntent.getStringExtra("title");

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.ar_mixview_review_recyclerview);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ReviewRecyclerViewAdapter();
        recyclerView.setAdapter(mAdapter);

        clientSelector = new FirebaseClient();
        clientSelector.getTraceDataFromServer("경기도 성남시 분당구 삼평동 703-3", mAdapter);

        //Log.i("악",traceList.get(0).getLocationID());

        TextView textView = (TextView) findViewById(R.id.ar_mixview_review_title);
        textView.setText(reviewTitle);


        //specify an adapter

        //TODO: 2017.01.25 need trace data list.
        //adapter.setList(traceList);

    }
}