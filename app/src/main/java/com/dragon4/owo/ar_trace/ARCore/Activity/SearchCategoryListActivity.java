package com.dragon4.owo.ar_trace.ARCore.Activity;

import android.os.Bundle;
import android.widget.ListView;

import com.dragon4.owo.ar_trace.R;

/**
 * Created by joyeongje on 2017. 2. 12..
 */

public class SearchCategoryListActivity extends ListParentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_search_keyword_listview);

        ListView listView = (ListView)findViewById(R.id.ar_mixview_search_listview);

        // TODO: 2017. 2. 12. 현재 카테고리 마커 넣고
        // 리스트뷰랑 어댑터 추가


    }



}
