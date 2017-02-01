package com.dragon4.owo.ar_trace.ARCore.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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

public class TraceActivity extends Activity implements View.OnClickListener {

    private ClientSelector clientSelector;
    private ArrayList<Trace> traceList;
    public ReviewRecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_review);
        findViewById(R.id.ar_mixview_review_back).setOnClickListener(this);
        findViewById(R.id.ar_mixview_review_add).setOnClickListener(this);

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
        textView.setText(reviewTitle + " 리뷰");


        //specify an adapter

        //TODO: 2017.01.25 need trace data list.
        //adapter.setList(traceList);

    }

    public void clickLikeBtn(View parent) {
        TextView likeNumber = (TextView)parent.findViewById(R.id.ar_mixview_review_like_number);
        likeNumber.setText(""+(Integer.parseInt(likeNumber.getText().toString()) + 1));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ar_mixview_review_back:
                finish();
                break;

            case R.id.ar_mixview_review_add:
                Intent intent = new Intent(TraceActivity.this, WriteReviewActivity.class);
                //TODO: intent에서 WriteReviewActivity에 어느걸 넘겨줘서 리뷰를 적어야하는가.
                startActivity(intent);
                break;

            case R.id.ar_mixview_review_like:
                clickLikeBtn((View)view.getParent());
                break;
        }
    }
}