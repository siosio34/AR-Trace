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

        clientSelector.getTraceLikeInformation(trace, this, traceHolder); // 처음 좋아요 정보 가져오기
        traceHolder.likeWrapper.setOnClickListener(new View.OnClickListener() { // 좋아요 버튼 클릭시 처리되는 함수
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