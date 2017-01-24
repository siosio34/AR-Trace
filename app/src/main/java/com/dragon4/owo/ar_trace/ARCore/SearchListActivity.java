package com.dragon4.owo.ar_trace.ARCore;

import android.app.Activity;
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

import com.dragon4.owo.ar_trace.Network.Firebase.FirebaseClient;
import com.dragon4.owo.ar_trace.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mansu on 2017-01-24.
 */

public class SearchListActivity extends Activity implements View.OnClickListener {
    private TextView searchName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_search_listview);
        searchName = (TextView)findViewById(R.id.ar_mixview_search_listview_search_text);
        searchName.setText(getIntent().getStringExtra("searchName"));

        ListView listView = (ListView)findViewById(R.id.ar_mixview_search_listview);

        //TODO: 2017.01.24 리스트 데이터 가져와서 추가할 것.
        List<ARMarker> dataList = new ArrayList<>();
        for(int i=0; i<10; i++)
            dataList.add(new ARMarker() {
                @Override
                public int getMaxObjects() {
                    return 0;
                }
            });

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
                break;
        }
    }

    private class SearchListViewAdapter extends BaseAdapter {
        private List<ARMarker> dataList = new ArrayList<>();
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
            TextView kind = (TextView) view.findViewById(R.id.ar_mixview_search_listview_item_kind);

            //TODO: 2017.01.24 데이터 추가 부분
            /*
            name.setText(dataList.get(i).getTitle());
            callNumber.setText(dataList.get(i).get);
            kind.setText(dataList.get(i).);
            */
            return view;
        }

        public void setDataList(List<ARMarker> dataList) {
            this.dataList = dataList;
        }

        public String getCurrentText() {
            return currentText;
        }

        public void setCurrentText(String currentText) {
            this.currentText = currentText;
        }

    }
}