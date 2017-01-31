package com.dragon4.owo.ar_trace.ARCore;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mansu on 2017-01-25.
 */

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter {

    public ArrayList<Trace> traceList;

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public ImageView userProfileView;
        public TextView userNameView;
        public ImageView imgView;
        public TextView contentView;
        public TextView dateView;
        public TextView likeNumberView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            userProfileView = (ImageView)itemView.findViewById(R.id.ar_mixview_review_user_profile);
            userNameView = (TextView)itemView.findViewById(R.id.ar_mixview_review_user_name);
            imgView = (ImageView)itemView.findViewById(R.id.ar_mixview_review_img);
            contentView = (TextView)itemView.findViewById(R.id.ar_mixview_review_content);
            dateView = (TextView)itemView.findViewById(R.id.ar_mixview_review_date);
            likeNumberView = (TextView)itemView.findViewById(R.id.ar_mixview_review_like_number);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ar_mixview_review_item, parent, false);
        ReviewViewHolder vh = new ReviewViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Trace trace = traceList.get(position);
        ReviewViewHolder reviewHolder = (ReviewViewHolder)holder;
        //TODO: 2017.01.25 user profile and user name
        //Picasso.with(reviewHolder.userProfileView()).load(trace.getUserProfileURL()).into(reviewHolder.userProfileView);
        //reviewHolder.userNameView.setText(trace.getUserName());
        Picasso.with(reviewHolder.imgView.getContext()).load(trace.getImageURL()).into(reviewHolder.imgView);
        reviewHolder.contentView.setText(trace.getContent());
        reviewHolder.dateView.setText(trace.getWriteDate().toString());
        reviewHolder.likeNumberView.setText(""+trace.getLikeNum());
    }

    @Override
    public int getItemCount() {
        return traceList.size();
    }

    public void setList(ArrayList<Trace> traceList) { this.traceList = traceList; }
}
