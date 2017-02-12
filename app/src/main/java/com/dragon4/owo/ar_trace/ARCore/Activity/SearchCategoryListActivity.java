package com.dragon4.owo.ar_trace.ARCore.Activity;

import android.os.Bundle;
import android.widget.ListView;

import com.dragon4.owo.ar_trace.ARCore.Marker.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.Marker.NaverSearchMarker;
import com.dragon4.owo.ar_trace.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joyeongje on 2017. 2. 12..
 */

public class SearchCategoryListActivity extends ListParentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_search_keyword_listview);
        ListView listView = (ListView)findViewById(R.id.ar_mixview_search_listview);

        HashMap<String, List<ARMarker>> markerMap = (HashMap<String, List<ARMarker>>)getIntent().getSerializableExtra("markerList");
        List<ARMarker> markerList = markerMap.get("markerList");
        List<NaverSearchMarker> dataList = new ArrayList<>();
        for(int i = 0 ; i < markerList.size(); i++)
            dataList.add((NaverSearchMarker)markerList.get(i));

        SearchListViewAdapter adapter = new SearchListViewAdapter(getLayoutInflater());
        adapter.setDataList(dataList);
        listView.setAdapter(adapter);
    }



}
