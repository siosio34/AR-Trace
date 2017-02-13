package com.dragon4.owo.ar_trace.ARCore.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dragon4.owo.ar_trace.Network.ClientSelector;
import com.dragon4.owo.ar_trace.Network.Firebase.FirebaseClient;
import com.dragon4.owo.ar_trace.R;

/**
 * Created by Mansu on 2017-01-25.
 */

public class TraceActivity extends Activity implements View.OnClickListener {

    private ClientSelector clientSelector;
    public TraceRecyclerViewAdapter mAdapter;
    private String buildingID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_review);
        findViewById(R.id.ar_mixview_review_back).setOnClickListener(this);
        findViewById(R.id.ar_mixview_review_add).setOnClickListener(this);

        clientSelector = new FirebaseClient();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.ar_mixview_review_recyclerview);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new TraceRecyclerViewAdapter(clientSelector);
        recyclerView.setAdapter(mAdapter);

        if(getIntent().getStringExtra("title") != null) {
            Intent reviewIntent = getIntent();
            String reviewTitle = reviewIntent.getStringExtra("title");
            // use a linear layout manager

            buildingID = getIntent().getStringExtra("title");
            clientSelector.getTraceDataFromServer(buildingID, mAdapter);

            //Log.i("악",traceList.get(0).getLocationID());

            TextView textView = (TextView) findViewById(R.id.ar_mixview_review_title);
            textView.setText(reviewTitle + " 리뷰");
        }
        else {
            Bundle bundle = getIntent().getExtras();
            buildingID = bundle.getString("buildingID");
            clientSelector.getTraceDataFromServer(buildingID, mAdapter);

            //Log.i("악",traceList.get(0).getLocationID());

            TextView textView = (TextView) findViewById(R.id.ar_mixview_review_title);
            textView.setText(bundle.getString("title") + " 리뷰");
        }
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
                intent.putExtra("buildingID", buildingID);
                intent.putExtra("lat", getIntent().getDoubleExtra("lat", 0.0));
                intent.putExtra("lon", getIntent().getDoubleExtra("lon", 0.0));
                startActivity(intent);
                finish();
                break;
        }
    }
}