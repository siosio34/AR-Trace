package com.dragon4.owo.ar_trace.ARCore;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.R;

import java.util.ArrayList;

/**
 * Created by Mansu on 2017-01-25.
 */

public class ReviewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_review);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.ar_mixview_review_recyclerview);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //specify an adapter
        ReviewRecyclerViewAdapter adapter = new ReviewRecyclerViewAdapter();
        //TODO: 2017.01.25 need trace data list.
        adapter.setList(new ArrayList<Trace>());
        recyclerView.setAdapter(adapter);
    }
}