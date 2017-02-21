package com.dragon4.owo.ar_trace.ARCore.Activity;

import android.os.Bundle;
import android.widget.ListView;

import com.dragon4.owo.ar_trace.ARCore.Marker.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.Marker.NaverSearchMarker;
import com.dragon4.owo.ar_trace.ARCore.Marker.SocialARMarker;
import com.dragon4.owo.ar_trace.ARCore.MixView;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joyeongje on 2017. 2. 12..s
 */

public class SearchCategoryListActivity extends ListParentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_search_category_listview);
        ListView listView = (ListView)findViewById(R.id.ar_mixview_search_listview);

        List<ARMarker> markers = MixView.getDataView().getDataHandler().getMarkerList();
        List<NaverSearchMarker> dataList = new ArrayList<>();
        for(int i = 0 ; i < markers.size(); i++) {
            SocialARMarker socialARMarker = (SocialARMarker)markers.get(i);
            NaverSearchMarker naverSearchMarker = new NaverSearchMarker(socialARMarker.getTitle(), socialARMarker.getLatitude(), socialARMarker.getLongitude(),
                    socialARMarker.getAltitude(), socialARMarker.getURL(), socialARMarker.getDatasource(),
                    socialARMarker.getFlag(), "", "", "");
            dataList.add(naverSearchMarker);
        }



        SearchListViewAdapter adapter = new SearchListViewAdapter(getLayoutInflater());
        adapter.setDataList(dataList);
        listView.setAdapter(adapter);
    }
}
