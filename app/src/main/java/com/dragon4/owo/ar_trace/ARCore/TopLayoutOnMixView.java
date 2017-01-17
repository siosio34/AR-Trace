package com.dragon4.owo.ar_trace.ARCore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dragon4.owo.ar_trace.ARCore.data.DataProcessor.DataConvertor;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.ARCore.gui.PaintScreen;
import com.dragon4.owo.ar_trace.NaverMap.FragmentMapview;
import com.dragon4.owo.ar_trace.R;
import com.nhn.android.maps.maplib.NGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

/**
 * Created by mansu on 2017-01-15.
 */

public class TopLayoutOnMixView {
    //리뷰를 위한 팝업뷰
    private PopupWindow mPopupWindow;

    //네이버 지도
    private FragmentMapview naverFragment;

    //가장 상단의 레이아웃
    private View mainArView;

    private Context context;
    public TopLayoutOnMixView(final Activity activity, final LayoutInflater inflater, FragmentManager manager) {
        context = activity.getApplicationContext();
        mainArView = inflater.inflate(R.layout.activity_ar_mixview, null);

        final LinearLayout parentButtonView = (LinearLayout) mainArView.findViewById(R.id.ar_mixview_parent_buttonview);
        final LinearLayout searchbar = (LinearLayout) mainArView.findViewById(R.id.ar_mixview_searchbar);
        final Button hideSearchbar = (Button) mainArView.findViewById(R.id.ar_mixview_hide_searchbar);
        final ListView searchListView = (ListView) mainArView.findViewById(R.id.ar_mixview_search_list);

        Button searchBtn = (Button) mainArView.findViewById(R.id.ar_mixview_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentButtonView.setVisibility(View.GONE);
                searchbar.setVisibility(View.VISIBLE);
            }
        });
        hideSearchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchbar.setVisibility(View.GONE);
                parentButtonView.setVisibility(View.VISIBLE);
            }
        });

        final DataConvertor dataConvertor = new DataConvertor();


        final EditText searchText = (EditText) mainArView.findViewById(R.id.ar_mixview_search_text);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, android.view.KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        String queryString = searchText.getText().toString();
                        try {
                            List<ARMarker> searchList = null;
                            String encodedQueryString = URLEncoder.encode(queryString, "UTF-8");
                            String searchURL = DataSource.createNaverSearchRequestURL(encodedQueryString);
                            String searchRawData = new HttpHandler().execute(searchURL).get();
                            Toast.makeText(context, searchRawData, Toast.LENGTH_LONG).show();
                            searchList = dataConvertor.load(searchRawData, DataSource.DATASOURCE.SEARCH, DataSource.DATAFORMAT.NAVER_SEARCH);
                            Toast.makeText(context, searchList.get(0).toString(), Toast.LENGTH_LONG).show();
                            // TODO: 2017. 1. 3.

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        break;
                    default:
                        Toast.makeText(context, "기본", Toast.LENGTH_LONG).show();
                        return false;
                }
                return true;
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String queryString = searchText.getText().toString();
                try {
                    List<ARMarker> searchList = null;
                    String encodedQueryString = URLEncoder.encode(queryString, "UTF-8");

                    String tempCallbackUrl = "http://ac.map.naver.com/ac?q=" + encodedQueryString + "&st=10&r_lt=10&r_format=json";
                    String rawData = new HttpHandler().execute(tempCallbackUrl).get();
                    Log.i("rawData", rawData);

                    JSONObject root = new JSONObject(rawData);
                    JSONArray dataArray = root.getJSONArray("items");
                    JSONArray locationData = dataArray.getJSONArray(0);

                    Log.i("dataArray", locationData.toString());

                    ArrayList<String> list = new ArrayList<>();
                    for (int index = 0; index < locationData.length(); index++)
                        list.add(locationData.getString(index).substring(2, locationData.getString(index).length() - 2));

                    SearchViewAdapter adapter = new SearchViewAdapter(inflater);
                    adapter.setDataList(list);
                    adapter.setCurrentText(charSequence.toString());
                    searchListView.setAdapter(adapter);
                    searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            // if(naverFragment != null)
                            //     naverFragment.findAndDrawRoot();
                            // else
                            //     Toast.makeText(MixView.this, "지도가 될때까지 기다려주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        searchText.addTextChangedListener(watcher);
        mainArView.findViewById(R.id.ar_mixview_search_erase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
            }
        });

        mainArView.findViewById(R.id.ar_mixview_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainArView.findViewById(R.id.ar_mixview_parent_buttonview).setVisibility(View.GONE);
                mainArView.findViewById(R.id.ar_mixview_parent_categoryview).setVisibility(View.VISIBLE);
            }
        });
        mainArView.findViewById(R.id.ar_mixview_category_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainArView.findViewById(R.id.ar_mixview_parent_categoryview).setVisibility(View.GONE);
                mainArView.findViewById(R.id.ar_mixview_parent_buttonview).setVisibility(View.VISIBLE);
            }
        });


        mainArView.findViewById(R.id.ar_mixview_write_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, WriteReviewActivity.class);

                NGeoPoint nGeoPoint = naverFragment.getCurrentLocation();
                intent.putExtra("lat",nGeoPoint.getLatitude());
                intent.putExtra("lon", nGeoPoint.getLongitude());
                activity.startActivity(intent);
                // TODO: 2017. 1. 12. 이미지뷰


            }
        });


        final Button reviewOnOffBtn = (Button) mainArView.findViewById(R.id.ar_mixview_review_onoff);
        reviewOnOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewOnOffBtn.setBackgroundResource(R.drawable.icon_others_review_off);
                reviewOnOffBtn.setBackgroundResource(R.drawable.icon_others_review_on);
            }
        });

        final LinearLayout buttonViewLayout = (LinearLayout) mainArView.findViewById(R.id.ar_mixview_buttonview);
        final Button hideBtn = (Button) mainArView.findViewById(R.id.ar_mixview_buttonview_hide);
        hideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonViewLayout.getVisibility() == View.GONE) {
                    buttonViewLayout.setVisibility(View.VISIBLE);
                    hideBtn.setBackgroundResource(R.drawable.icon_menu_up);
                } else if (buttonViewLayout.getVisibility() == View.VISIBLE) {
                    buttonViewLayout.setVisibility(View.GONE);
                    hideBtn.setBackgroundResource(R.drawable.icon_menu_down);
                }
            }
        });

        naverFragment = new FragmentMapview();
        naverFragment.setArguments(new Bundle());

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(R.id.ar_mixview_naverview, naverFragment);
        fragmentTransaction.commit();

        // 네이버 지도 추가
        // TODO: 2016. 12. 31. 배율 높이기 네이버 위치 리스너 만들기.

    }

    public View getMainArView() {
        return mainArView;
    }

    private class SearchViewAdapter extends BaseAdapter {
        private ArrayList<String> dataList = new ArrayList<>();
        private String currentText;
        private LayoutInflater inflater;

        public SearchViewAdapter(LayoutInflater inflater) {
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
            view = inflater.inflate(R.layout.layout_search_item, null);
            TextView searchItem = (TextView) view.findViewById(R.id.search_item);

            String data = dataList.get(i);
            int index = data.indexOf(currentText);

            if (index != -1) {
                SpannableStringBuilder builder = new SpannableStringBuilder();

                String before = data.substring(0, index);
                SpannableString beforeSpannable = new SpannableString(before);
                beforeSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, before.length(), 0);
                builder.append(beforeSpannable);

                String current = data.substring(index, index + currentText.length());
                SpannableString currentSpannable = new SpannableString(current);
                currentSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#88C290")), 0, current.length(), 0);
                builder.append(currentSpannable);

                String after = data.substring(index + currentText.length(), data.length());
                SpannableString afterSpannable = new SpannableString(after);
                afterSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, after.length(), 0);
                builder.append(afterSpannable);

                searchItem.setText(builder, TextView.BufferType.SPANNABLE);

                return view;
            }

            String currentTextNoSpace = currentText.replaceAll(" ", "");
            index = data.indexOf(currentTextNoSpace);

            if (index != -1) {
                SpannableStringBuilder builder = new SpannableStringBuilder();

                String before = data.substring(0, index);
                SpannableString beforeSpannable = new SpannableString(before);
                beforeSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, before.length(), 0);
                builder.append(beforeSpannable);

                String current = data.substring(index, index + currentTextNoSpace.length());
                SpannableString currentSpannable = new SpannableString(current);
                currentSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#88C290")), 0, current.length(), 0);
                builder.append(currentSpannable);

                String after = data.substring(index + currentTextNoSpace.length(), data.length());
                SpannableString afterSpannable = new SpannableString(after);
                afterSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, after.length(), 0);
                builder.append(afterSpannable);

                searchItem.setText(builder, TextView.BufferType.SPANNABLE);

                return view;
            }

            searchItem.setText(data);
            return view;
        }

        public void setDataList(ArrayList<String> dataList) {
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
