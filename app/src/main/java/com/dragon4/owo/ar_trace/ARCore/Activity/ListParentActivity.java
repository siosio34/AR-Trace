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
import android.widget.LinearLayout;
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

public class ListParentActivity extends Activity {
    private List<NaverSearchMarker> dataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_search_listview);

        DataConvertor dataConvertor = new DataConvertor();

        String queryString = getIntent().getStringExtra("searchName");
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

        dataList = new ArrayList<>();
        for(int i = 0 ; i < searchList.size(); i++)
            dataList.add((NaverSearchMarker)searchList.get(i));

        SearchListViewAdapter adapter = new SearchListViewAdapter(getLayoutInflater());
        adapter.setDataList(dataList);
        listView.setAdapter(adapter);
    }

    private void runNavi(String address) {
        String encodedQueryString = null;
        try {
            encodedQueryString = URLEncoder.encode(address, "UTF-8");
            String requestURL = DataSource.createNaverGeoAPIRequestURL(encodedQueryString);
            String rawData = new HttpHandler().execute(requestURL).get();
            Log.i("rawData", rawData);

            JSONObject root = new JSONObject(rawData);
            JSONArray dataArray = root.getJSONObject("result").getJSONArray("items");
            JSONObject dataObject = dataArray.getJSONObject(0).getJSONObject("point");

            if(dataObject != null) {

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

    protected class SearchListViewAdapter extends BaseAdapter {
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
                public void onClick(View view) {
                    runNavi(currentData.getAddress());
                }
            });
            navi.setTag(currentData.getAddress());

            
            // 리뷰 버튼 클릭시 활성화.
            LinearLayout reviewText = (LinearLayout)view.findViewById(R.id.ar_mixview_search_listview_review);
            reviewText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ListParentActivity.this, TraceActivity.class);
                    Log.i("주소", currentData.getAddress());
                    intent.putExtra("title", currentData.getAddress());
                    intent.putExtra("lat", currentData.getLatitude());
                    intent.putExtra("lon", currentData.getLongitude());
                    startActivity(intent);
                    finish();
                }
            });
            reviewText.setTag(currentData.getAddress());

            // 데이터들 보여주기
            name.setText(currentData.getTitle());
            callNumber.setText(currentData.getTelephone());
            category.setText(currentData.getCategory());


            // 신주소 구주소 전환.
            if(currentData.getRoadAddress().length() > 0) {
                address.setText(currentData.getRoadAddress());
            }
            else {
                address.setText(currentData.getAddress());
            }

            address.setTag(address.getText().toString());

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