package com.dragon4.owo.ar_trace.ARCore.Activity;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dragon4.owo.ar_trace.ARCore.MixUtils;
import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.Model.User;
import com.dragon4.owo.ar_trace.Network.ClientSelector;
import com.dragon4.owo.ar_trace.Network.Firebase.FirebaseClient;
import com.dragon4.owo.ar_trace.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by Mansu on 2017-01-25.
 */

public class TraceRecyclerViewAdapter extends RecyclerView.Adapter {
    public ArrayList<Trace> traceList;
    public ClientSelector clientSelector;

    public TraceRecyclerViewAdapter(ClientSelector clientSelector) {
        this.clientSelector = clientSelector;
    }

    public class TraceViewHolder extends RecyclerView.ViewHolder {
        public ImageView userProfileView;
        public TextView userNameView;
        public ImageView imgView;
        public TextView contentView;
        public TextView dateView;
        public TextView likeNumberView;
        public TextView likeTextView;
        public ImageView likeIconView;
        public LinearLayout likeWrapper;
        public boolean isLikeClicked = false;

        public TraceViewHolder(View itemView) {
            super(itemView);
            userProfileView = (ImageView)itemView.findViewById(R.id.ar_mixview_review_user_profile);
            userNameView = (TextView)itemView.findViewById(R.id.ar_mixview_review_user_name);
            imgView = (ImageView)itemView.findViewById(R.id.ar_mixview_review_img);
            contentView = (TextView)itemView.findViewById(R.id.ar_mixview_review_content);
            dateView = (TextView)itemView.findViewById(R.id.ar_mixview_review_date);
            likeNumberView = (TextView)itemView.findViewById(R.id.ar_mixview_review_like_number);
            likeTextView = (TextView)itemView.findViewById(R.id.ar_mixview_review_like_text);
            likeIconView = (ImageView) itemView.findViewById(R.id.ar_mixview_review_like_icon);
            likeWrapper = (LinearLayout)itemView.findViewById(R.id.ar_mixview_review_like);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ar_mixview_review_item, parent, false);
        TraceViewHolder vh = new TraceViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Trace trace = traceList.get(position);
        final TraceViewHolder traceHolder = (TraceViewHolder)holder;

        // 리뷰정보 표시
        Picasso.with(traceHolder.userProfileView.getContext()).load(trace.getUserImageUrl()).transform(new CropCircleTransformation()).into(traceHolder.userProfileView);
        traceHolder.userNameView.setText(trace.getUserName());
        Picasso.with(traceHolder.imgView.getContext()).load(trace.getImageURL()).into(traceHolder.imgView);
        traceHolder.contentView.setText(trace.getContent());
        traceHolder.dateView.setText(MixUtils.getDateString(trace.getWriteDate()));
        traceHolder.likeNumberView.setText(String.valueOf(trace.getLikeNum()));

        // 1. 내가 좋아햇는지 여부에 따라 좋아요에 따라 색깔을 칠한다. clientSelctor.checkLikeTraceUser(String userID,TraceHolder traceholder);
        // 2. 클릭할때마다 색깔만 바꾸는 이벤트 처리
        // 3. 마지막에 리뷰 홀더 사라질때 체크가 되어있어 있던거
        // 클릭하면 색깔만 바꾸게
        // 리사이클러뷰에서 사라질때 전송을 한다..?

        // TODO: 2017. 2. 20. 좋아요 버튼 누를때마다 처리는 여기서 !

        // 좋아요했는지 여부 가져와서 좋아요 색깔 체크, 좋아요버튼
        //setLike(); // 내가 좋아요 했는지 이력 가져온다음에 좋아요 색깔체크
        // 좋아요 클릭 이벤트.
        // TODO: 2017. 2. 20. 내가 좋아요 한거 인거 아니냐에 따라 표시 해야됨

        // 리뷰정보 좋아요 표시 및 이벤트 ...
        // 네이버 에서 데이터 정보를 가져오고 그에따른 후기 데이터가 자체로 구성되어있기 때문에 이과정이필요
        // 네이버에서 데이터가져오는경우가 아니고 자체데이터 활용시는 필요없는 부분

        //void getTraceLikeInformation(String traceID,TraceRecyclerViewAdapter.TraceViewHolder traceAdapter);
        // 좋아요 데이터는 비동기로 가져오자!
        clientSelector.getTraceLikeInformation(trace, this, traceHolder);
        traceHolder.likeWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                traceHolder.isLikeClicked = !traceHolder.isLikeClicked;
                setLike(traceHolder);
                traceHolder.likeNumberView.setText(String.valueOf(Integer.parseInt(traceHolder.likeNumberView.getText().toString()) + (traceHolder.isLikeClicked ? 1 : -1)));
                clientSelector.sendTraceLikeToServer(traceHolder.isLikeClicked, trace);
            }
        });
    }

    public void setLike(TraceViewHolder traceHolder) {
        if(traceHolder.isLikeClicked) {
            traceHolder.likeNumberView.setTextColor(Color.parseColor("#A5009017"));
            traceHolder.likeTextView.setTextColor(Color.parseColor("#A5009017"));
            traceHolder.likeIconView.setImageResource(R.drawable.ic_like);
        }
        else {
            traceHolder.likeNumberView.setTextColor(Color.parseColor("#5A000000"));
            traceHolder.likeTextView.setTextColor(Color.parseColor("#5A000000"));
            traceHolder.likeIconView.setImageResource(R.drawable.ic_like_cancel);
        }
    }

    @Override
    public int getItemCount() {
        return traceList.size();
    }

    public void setList(ArrayList<Trace> traceList) { this.traceList = traceList; }
}