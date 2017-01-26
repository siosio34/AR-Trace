package com.dragon4.owo.ar_trace.ARCore.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.dragon4.owo.ar_trace.R;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.layout_ar_mixview_search_listview_item, null);
            TextView name = (TextView) view.findViewById(R.id.ar_mixview_search_listview_item_name);
            TextView callNumber = (TextView) view.findViewById(R.id.ar_mixview_search_listview_item_call_number);
            TextView category = (TextView) view.findViewById(R.id.ar_mixview_search_listview_item_category);
            TextView address = (TextView) view.findViewById(R.id.ar_mixview_search_listview_item_address);

            final NaverSearchMarker currentData = dataList.get(i);

            // 네비 버튼 클릭시 활성화
            TextView navi = (TextView)view.findViewById(R.id.ar_mixview_search_listview_navi);
            navi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //DataConvertor dataConvertor = new DataConvertor();
                    String encodedQueryString = null;
                    try {
                        encodedQueryString = URLEncoder.encode(currentData.getAddress(), "UTF-8");
                        String requestURL = DataSource.createNaverGeoAPIRequestURL(encodedQueryString);
                        String rawData = new HttpHandler().execute(requestURL).get();
                        Log.i("rawData", rawData);

                        JSONObject root = new JSONObject(rawData);
                        JSONArray dataArray = root.getJSONObject("result").getJSONArray("items");
                        JSONObject dataObject = dataArray.getJSONObject(0).getJSONObject("point");

                        if(dataObject != null) {
                            // TODO: 2017. 1. 26. 이 코드 에러 
                            // TODO: 2017. 1. 26. 이 액티비티를 종료시키고  TopLayoutOnMixVew 즉 메인화면에서 네비를 실행시킬려면 여기서 경도,위도 보내줘야함
                            // // TODO: 2017. 1. 26. 실행해보면 알수 있듯이  TopLayoutOnMixViewActivity 이게 명시적인 액티비티가 아니라네 
                            //// TODO: 2017. 1. 26. 메니페스트에 등록 안시켜놓은거보면 내가 알던거랑 다른거같은데 이값보내는거해결좀 
                            Intent naviIntent = new Intent();
                            naviIntent.putExtra("lat", dataObject.getString("y"));
                            naviIntent.putExtra("lon", dataObject.getString("x"));
                            setResult(RESULT_OK, naviIntent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "해당 지역의 좌표를 받아올 수 없습니다", Toast.LENGTH_LONG).show();
                        }
                       // JSONArray locationData = dataArray.getJSONArray(0);
                    } catch (UnsupportedEncodingException | InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            // 데이터들 보여주기
            name.setText(currentData.getTitle());
            callNumber.setText(currentData.getTelephone());
            category.setText(currentData.getCategory());
            address.setText(currentData.getAddress());

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