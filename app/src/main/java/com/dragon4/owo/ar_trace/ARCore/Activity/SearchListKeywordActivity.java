package com.dragon4.owo.ar_trace.ARCore.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.dragon4.owo.ar_trace.ARCore.Marker.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.NaverHttpHandler;
import com.dragon4.owo.ar_trace.ARCore.Marker.NaverSearchMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataProcessor.DataConvertor;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mansu on 2017-02-12.
 */

public class SearchListKeywordActivity extends ListParentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_search_listview);

        DataConvertor dataConvertor = new DataConvertor();

        String queryString = getIntent().getStringExtra("searchName");
        final TextView searchName = (TextView)findViewById(R.id.ar_mixview_search_listview_search_text);
        searchName.setText(queryString);
        searchName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchName.setText("");
            }
        });

        List<ARMarker> searchList = null;
        String encodedQueryString = null;
        try {
            encodedQueryString = URLEncoder.encode(queryString, "UTF-8");
            String searchURL = DataSource.createNaverSearchRequestURL(encodedQueryString);
            String searchRawData = new NaverHttpHandler().execute(searchURL).get();
            searchList = dataConvertor.load(searchRawData, DataSource.DATASOURCE.SEARCH, DataSource.DATAFORMAT.NAVER_SEARCH);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Toast.makeText(context, searchRawData, Toast.LENGTH_LONG).show();
        ListView listView = (ListView)findViewById(R.id.ar_mixview_search_listview);

        List<NaverSearchMarker> dataList = new ArrayList<>();
        for(int i = 0 ; i < searchList.size(); i++)
            dataList.add((NaverSearchMarker)searchList.get(i));

        SearchListViewAdapter adapter = new SearchListViewAdapter(getLayoutInflater());
        adapter.setDataList(dataList);
        listView.setAdapter(adapter);
    }
}