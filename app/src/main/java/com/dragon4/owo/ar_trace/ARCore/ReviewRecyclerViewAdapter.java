package com.dragon4.owo.ar_trace.ARCore;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.Model.User;
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
        public TextView likeTextView;
        public ImageView likeIconView;
        public LinearLayout likeWrapper;
        public boolean isLikeClicked = false;

        public ReviewViewHolder(View itemView) {
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
        ReviewViewHolder vh = new ReviewViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Trace trace = traceList.get(position);
        final ReviewViewHolder reviewHolder = (ReviewViewHolder)holder;
        //TODO: 2017.01.25 user profile and user name
        //Picasso.with(reviewHolder.userProfileView()).load(trace.getUserProfileURL()).into(reviewHolder.userProfileView);
        //reviewHolder.userNameView.setText(trace.getUserName());
        Picasso.with(reviewHolder.imgView.getContext()).load(trace.getImageURL()).into(reviewHolder.imgView);
        reviewHolder.contentView.setText(trace.getContent());
        reviewHolder.dateView.setText(MixUtils.getDateString(trace.getWriteDate()));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("building").child(trace.getLocationID()).child("trace").child(trace.getTraceID());
        myRef.child("likeNum").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewHolder.likeNumberView.setText(""+dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.child("likeUserList").child(User.getMyInstance().getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    reviewHolder.isLikeClicked = true;
                else
                    reviewHolder.isLikeClicked = false;
                setLike(reviewHolder, Integer.parseInt(reviewHolder.likeNumberView.getText().toString()));

                reviewHolder.likeWrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reviewHolder.isLikeClicked = !reviewHolder.isLikeClicked;
                        setLike(reviewHolder, Integer.parseInt(reviewHolder.likeNumberView.getText().toString()) + (reviewHolder.isLikeClicked ? 1 : -1));
                        sendLikeToServer(reviewHolder.isLikeClicked, trace);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setLike(ReviewViewHolder reviewHolder, int number) {
        reviewHolder.likeNumberView.setText("" + number);
        if(reviewHolder.isLikeClicked) {
            reviewHolder.likeNumberView.setTextColor(Color.parseColor("#A5009017"));
            reviewHolder.likeTextView.setTextColor(Color.parseColor("#A5009017"));
            reviewHolder.likeIconView.setImageResource(R.drawable.ic_like);
        }
        else {
            reviewHolder.likeNumberView.setTextColor(Color.parseColor("#5A000000"));
            reviewHolder.likeTextView.setTextColor(Color.parseColor("#5A000000"));
            reviewHolder.likeIconView.setImageResource(R.drawable.ic_like_cancel);
        }
    }

    public void sendLikeToServer(final boolean isLikeClicked, Trace trace) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("building").child(trace.getLocationID()).child("trace").child(trace.getTraceID());
        myRef.child("likeUserList").child(User.getMyInstance().getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(isLikeClicked)
                    dataSnapshot.getRef().setValue("");
                else
                    dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        myRef.child("likeNum").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(isLikeClicked)
                    dataSnapshot.getRef().setValue((long)dataSnapshot.getValue() + 1);
                else
                    dataSnapshot.getRef().setValue((long)dataSnapshot.getValue() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return traceList.size();
    }

    public void setList(ArrayList<Trace> traceList) { this.traceList = traceList; }
}