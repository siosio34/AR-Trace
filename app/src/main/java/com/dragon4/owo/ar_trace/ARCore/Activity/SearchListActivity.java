package com.dragon4.owo.ar_trace.ARCore.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dragon4.owo.ar_trace.ARCore.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.HttpHandler;
import com.dragon4.owo.ar_trace.ARCore.NaverSearchMarker;
import com.dragon4.owo.ar_trace.ARCore.data.DataProcessor.DataConvertor;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.Network.Firebase.FirebaseClient;
import com.dragon4.owo.ar_trace.R;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mansu on 2017-01-24.
 */

public class SearchListActivity extends Activity implements View.OnClickListener {
    private TextView searchName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_search_listview);

        DataConvertor dataConvertor = new DataConvertor();

        String queryString = getIntent().getStringExtra("searchName");
        searchName = (TextView)findViewById(R.id.ar_mixview_search_listview_search_text);
        searchName.setText(queryString);

        List<ARMarker> searchList = null;
        String encodedQueryString = null;
        try {
            encodedQueryString = URLEncoder.encode(queryString, "UTF-8");
            String searchURL = DataSource.createNaverSearchRequestURL(encodedQueryString);
            String searchRawData = new HttpHandler().execute(searchURL).get();
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

        //TODO: 2017.01.24 리스트 데이터 가져와서 추가할 것.
        List<NaverSearchMarker> dataList = new ArrayList<>();
        for(int i = 0 ; i < searchList.size(); i++) {
            dataList.add((NaverSearchMarker)searchList.get(i));
        }

        SearchListViewAdapter adapter = new SearchListViewAdapter(getLayoutInflater());
        adapter.setDataList(dataList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        //TODO: 2017.01.24 버튼이벤트들 추가할것.
        switch (view.getId()) {
            case R.id.ar_mixview_search_listview_search_erase:
                searchName.setText("");
                break;

            case R.id.ar_mixview_search_listview_navi:
                break;

            case R.id.ar_mixview_search_listview_review:
                Intent intent = new Intent(SearchListActivity.this, ReviewActivity.class);
                //TODO: 2017.01.25 need to pass trace data list
                startActivity(intent);
                break;
        }
    }

    private class SearchListViewAdapter extends BaseAdapter {
        private List<NaverSearchMarker> dataList = new ArrayList<>();
        private String currentText;
        private LayoutInflater inflater;

        public SearchListViewAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.layout_ar_mixview_search_listview_item, null);
            TextView name = (TextView) view.findViewById(R.id.ar_mixview_search_listview_item_name);
            TextView callNumber = (TextView) view.findViewById(R.id.ar_mixview_search_listview_item_call_number);
            TextView category = (TextView) view.findViewById(R.id.ar_mixview_search_listview_item_category);
            TextView address = (TextView) view.findViewById(R.id.ar_mixview_search_listview_item_address);

            name.setText(dataList.get(i).getTitle());
            callNumber.setText(dataList.get(i).getTelephone());
            category.setText(dataList.get(i).getCategory());
            address.setText(dataList.get(i).getAddress());


            return view;
        }

        public void setDataList(List<NaverSearchMarker> dataList) {
            this.dataList = dataList;
        }

        public String getCurrentText() {
            return currentText;
        }

        public void setCurrentText(String currentText) {
            this.currentText = currentText;
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


}