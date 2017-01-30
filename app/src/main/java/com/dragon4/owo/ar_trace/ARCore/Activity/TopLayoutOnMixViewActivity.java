package com.dragon4.owo.ar_trace.ARCore.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.util.TypedValue;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dragon4.owo.ar_trace.ARCore.ARMarker;
import com.dragon4.owo.ar_trace.ARCore.Activity.SearchListActivity;
import com.dragon4.owo.ar_trace.ARCore.Activity.WriteReviewActivity;
import com.dragon4.owo.ar_trace.ARCore.HttpHandler;
import com.dragon4.owo.ar_trace.ARCore.data.DataProcessor.DataConvertor;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
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

/**
 * Created by mansu on 2017-01-15.
 */

public class TopLayoutOnMixViewActivity {
    public static int WRITE_REVIEW = 1;
    public static int SEARCH_LIST = 2;

    //네이버 지도
    private FragmentMapview naverFragment;

    //가장 상단의 레이아웃
    private View mainArView;

    //expansion, reduction level
    private int naver_map_level = 0;
    private int naver_map_max_level = 2;
    private int[][] naver_map_size = {{130,130},{260,260}};

    private Context context;
    public TopLayoutOnMixViewActivity(final Activity activity, final LayoutInflater inflater, FragmentManager manager) {
        context = activity.getApplicationContext();
        mainArView = inflater.inflate(R.layout.activity_ar_mixview, null);


        final LinearLayout parentButtonView = (LinearLayout) mainArView.findViewById(R.id.ar_mixview_parent_buttonview);
        final LinearLayout searchbar = (LinearLayout) mainArView.findViewById(R.id.ar_mixview_searchbar);
        final Button hideSearchbar = (Button) mainArView.findViewById(R.id.ar_mixview_hide_searchbar);
        final ListView searchListView = (ListView) mainArView.findViewById(R.id.ar_mixview_search_list);

        mainArView.findViewById(R.id.ar_mixview_naverview_expand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(naver_map_level < naver_map_max_level) {
                    naver_map_level++;
                    if(naver_map_level == naver_map_max_level) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mainArView.findViewById(R.id.ar_mixview_naverview_wrapper).getLayoutParams();
                        int tenMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, Resources.getSystem().getDisplayMetrics());
                        params.setMargins(0, tenMargin, tenMargin, 0);
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        mainArView.findViewById(R.id.ar_mixview_naverview_wrapper).setLayoutParams(params);
                    }
                    else {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mainArView.findViewById(R.id.ar_mixview_naverview_wrapper).getLayoutParams();
                        params.setMargins(0, 0, 0, 0);
                        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, naver_map_size[naver_map_level][0], Resources.getSystem().getDisplayMetrics());
                        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, naver_map_size[naver_map_level][1], Resources.getSystem().getDisplayMetrics());
                        mainArView.findViewById(R.id.ar_mixview_naverview_wrapper).setLayoutParams(params);
                    }
                }
            }
        });
        mainArView.findViewById(R.id.ar_mixview_naverview_reduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(naver_map_level > 0) {
                    naver_map_level--;
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mainArView.findViewById(R.id.ar_mixview_naverview_wrapper).getLayoutParams();
                    int tenMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, Resources.getSystem().getDisplayMetrics());
                    params.setMargins(0, tenMargin, tenMargin, 0);
                    params.width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, naver_map_size[naver_map_level][0], Resources.getSystem().getDisplayMetrics());
                    params.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, naver_map_size[naver_map_level][1], Resources.getSystem().getDisplayMetrics());
                    mainArView.findViewById(R.id.ar_mixview_naverview_wrapper).setLayoutParams(params);
                }
            }
        });

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

        naverFragment = new FragmentMapview();
        naverFragment.setArguments(new Bundle());

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(R.id.ar_mixview_naverview, naverFragment);
        fragmentTransaction.commit();

        final EditText searchText = (EditText) mainArView.findViewById(R.id.ar_mixview_search_text);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, android.view.KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        String queryString = searchText.getText().toString();
                            Intent intent = new Intent(activity, SearchListActivity.class);
                            intent.putExtra("searchName", queryString);
                            mainArView.setVisibility(View.GONE);
                            activity.startActivityForResult(intent, SEARCH_LIST);
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

                    final ArrayList<String> list = new ArrayList<>();
                    for (int index = 0; index < locationData.length(); index++)
                        list.add(locationData.getString(index).substring(2, locationData.getString(index).length() - 2));

                    SearchViewAdapter adapter = new SearchViewAdapter(inflater);
                    adapter.setDataList(list);
                    adapter.setCurrentText(charSequence.toString());
                    searchListView.setAdapter(adapter);
                    searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String queryString = list.get(i);
                            searchText.setText(queryString);
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

                mainArView.setVisibility(View.GONE);
                activity.startActivityForResult(intent, WRITE_REVIEW);
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
