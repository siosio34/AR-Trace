package com.dragon4.owo.ar_trace.Network.Firebase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.dragon4.owo.ar_trace.ARCore.Activity.TraceRecyclerViewAdapter;
import com.dragon4.owo.ar_trace.FCM.FCMWebServerConnector;
import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.Model.TracePointer;
import com.dragon4.owo.ar_trace.Model.User;
import com.dragon4.owo.ar_trace.Network.ClientSelector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joyeongje on 2017. 1. 20..
 */

public class FirebaseClient implements ClientSelector {

    private static final String TAG = "FirebaseClient";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    // TODO: 2017. 2. 6. 유저것도 할수 잇게...!


    private int uploadedCount = 0;
    private int uploadFailCount = 0;
    private int uploadedThumbnailCount = 0;
    private int uploadFailThumbnailCount = 0;

    public FirebaseClient() {

    }

    private Bitmap currentBitmap;

    private String makeTraceKey(String id) {
        return myRef.child("building").child(id).child("trace").push().getKey();
    }

    /*
    @Override
    public boolean getUserInformation(String userId) {

        myRef.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {



                        } else {

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }
    */

    @Override
    public void uploadUserDataToServer(final User currentUser, final Context googleSignInContext) {

        myRef.child("users").child(currentUser.getUserId()).addListenerForSingleValueEvent(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User getUserFromDB = dataSnapshot.getValue(User.class);
                        if (getUserFromDB == null) {
                            if(User.getMyInstance().getUserToken() != null)
                                currentUser.setUserToken(User.getMyInstance().getUserToken());
                            else
                                currentUser.setUserToken(FirebaseInstanceId.getInstance().getToken());

                            myRef.child("users").child(currentUser.getUserId()).setValue(currentUser);
                            User.setMyInstance(currentUser);
                            Log.i("신규 유저 정보", User.getMyInstance().toString());

                        } else { // 존재할경우 -> 불러와야함
                            String token = FirebaseInstanceId.getInstance().getToken();
                            if(getUserFromDB.getUserToken().compareTo(token) != 0)
                                updateTokenToServer(getUserFromDB.getUserTraceList(), token);
                            getUserFromDB.setUserToken(token);
                            myRef.child("users").child(getUserFromDB.getUserId()).setValue(getUserFromDB);
                            User.setMyInstance(getUserFromDB);
                            Log.i("기존 유저정보", User.getMyInstance().toString());
                        }

                        final Intent loginReceiver = new Intent();
                        loginReceiver.setAction("LOGIN_SUCCESS");
                        googleSignInContext.sendBroadcast(loginReceiver);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                    }

                }
        );

    }

    private void updateTokenToServer(final List<TracePointer> infoList, final String token) {
        myRef.child("building").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> childUpdates = new HashMap<>();
                for(int i=0; i<infoList.size(); i++)
                    childUpdates.put("/building/" + infoList.get(0).getBuildingID() + "/trace/" + infoList.get(0).getTraceID() + "/userToken", token);
                myRef.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void uploadImageToServer(final Trace trace, final File file) {

        try {
            FileInputStream in = null;
            in = new FileInputStream(file);
            currentBitmap = BitmapFactory.decodeStream(in, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Object lock = new Object();

                if (currentBitmap != null) {
                    double scale = 0;
                    if (currentBitmap.getWidth() > currentBitmap.getHeight())
                        scale = currentBitmap.getWidth() / 256;
                    else
                        scale = currentBitmap.getHeight() / 256;
                    if (scale == 0)
                        scale = 1;

                    ByteArrayOutputStream jpegOut = new ByteArrayOutputStream();
                    currentBitmap.compress(Bitmap.CompressFormat.JPEG, 70, jpegOut);

                    Bitmap thumbnail = Bitmap.createScaledBitmap(currentBitmap, (int) (currentBitmap.getWidth() / scale), (int) (currentBitmap.getHeight() / scale), true);
                    ByteArrayOutputStream jpegThumbnailOut = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, jpegThumbnailOut);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference myRef = storage.getReference().child(trace.getLocationID()).child(trace.getTraceID() + ".jpg");
                    StorageReference thumbnailRef = storage.getReference().child(trace.getLocationID()).child("sn-" + trace.getTraceID() + ".jpg");

                    if (myRef.getName() == null || myRef.getName() != "") {
                        byte[] imageData = jpegOut.toByteArray();
                        final UploadTask uploadTask = myRef.putBytes(imageData);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(getContext(), "사진 업로드에 실패하였습니다. 네트워크를 확인해주세요,", Toast.LENGTH_SHORT).show();
                                //dialog.setMessage("파일 업로드에 실패하였습니다. : " + fileName);
                                uploadFailCount++;
                                e.printStackTrace();
                                if ((uploadedCount + uploadFailCount + uploadFailThumbnailCount + uploadedThumbnailCount) == 2) {
                                    synchronized (lock) {
                                        lock.notify();
                                    }
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //   dialog.dismiss();
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference databaseRef = database.getReference("building").child(trace.getLocationID()).child("trace").child(trace.getTraceID()).child("imageURL");
                                databaseRef.setValue(downloadUri.toString());
                                //trace.setImageURL(downloadUri.toString());
                                Log.i("FirebaseClient", "이미지 업로드 완료");

                                uploadedCount++;
                                if ((uploadedCount + uploadFailCount + uploadFailThumbnailCount + uploadedThumbnailCount) == 2) {
                                    synchronized (lock) {
                                        lock.notify();
                                    }
                                }
                            }
                        });

                        byte[] thumbnailData = jpegThumbnailOut.toByteArray();
                        final UploadTask thumbNailUploadTask = thumbnailRef.putBytes(thumbnailData);
                        thumbNailUploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(getContext(), "사진 업로드에 실패하였습니다. 네트워크를 확인해주세요,", Toast.LENGTH_SHORT).show();
                                //dialog.setMessage("파일 업로드에 실패하였습니다. : " + fileName);
                                uploadFailThumbnailCount++;
                                e.printStackTrace();
                                if ((uploadedCount + uploadFailCount + uploadFailThumbnailCount + uploadedThumbnailCount) == 2) {
                                    synchronized (lock) {
                                        lock.notify();
                                    }
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference databaseRef = database.getReference("building").child(trace.getLocationID()).child("trace").child(trace.getTraceID()).child("thumbnailURL");
                                databaseRef.setValue(downloadUri.toString());
                                Log.i("FirebaseClient", "썸네일 이미지 업로드 완료");

                                //trace.setThumbnailURL(downloadUri.toString());

                                //Only the original thread that created a view hierarchy can touch its views.
                                uploadedThumbnailCount++;
                                if ((uploadedCount + uploadFailCount + uploadFailThumbnailCount + uploadedThumbnailCount) == 2) {
                                    synchronized (lock) {
                                        lock.notify();
                                    }
                                }
                            }
                        });
                    } else {
                        Log.i("FirebaseClient", " 이미지 업로드 실패");
                        // 파일이름 존재하지않을때
                    }

                }

                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    // Toast.makeText(getApplicationContext(), "업로드에 성공하였습니다.", Toast.LENGTH_SHORT).show();

    @Override
    public void uploadTraceToServer(final Trace trace) {
        final String traceKey = makeTraceKey(trace.getLocationID());
        trace.setTraceID(traceKey);

        myRef.child("users").child(User.getMyInstance().getUserId()).child("userTraceList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<TracePointer> pointers = User.getMyInstance().getUserTraceList();
                if(pointers == null)
                    pointers = new ArrayList<>();
                TracePointer pointer = new TracePointer(trace.getLocationID(), trace.getTraceID());
                pointers.add(pointer);

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/building/" + trace.getLocationID() + "/lat", trace.getLat());
                childUpdates.put("/building/" + trace.getLocationID() + "/lon", trace.getLon());
                childUpdates.put("/building/" + trace.getLocationID() + "/trace/" + trace.getTraceID(), trace);
                childUpdates.put("/users/" + User.getMyInstance().getUserId() + "/userTraceList", pointers);
                myRef.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public ArrayList<Trace> getTraceDataFromServer(String traceKey, final TraceRecyclerViewAdapter mAdapter) {
        // 하나의 장소에 대해서 리뷰들을 가져오는것.
        final ArrayList<Trace> traceList = new ArrayList<>();
        mAdapter.setList(traceList);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("building").child(traceKey).child("trace");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Trace trace = child.getValue(Trace.class);
                    traceList.add(trace);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return traceList;
    }

    private boolean isWorking = false;
    public boolean isWorking() {
        return isWorking;
    }

    @Override
    public void sendTraceLikeToServer(boolean isLikeClicked, Trace trace) {
        if(!isWorking) {
            isWorking = true;
            sendTraceLikeToFirebase(isLikeClicked, trace);
            if (isLikeClicked && trace.getUserId().compareTo(User.getMyInstance().getUserId()) != 0) {
                FCMWebServerConnector connector = new FCMWebServerConnector();
                connector.sendLikePush(trace);
            }
        }
    }

    private void sendTraceLikeToFirebase(final boolean isLikeClicked, final Trace trace) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("building").child(trace.getLocationID()).child("trace").child(trace.getTraceID());

        myRef.child("likeNum").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(isLikeClicked)
                    mutableData.setValue((long)mutableData.getValue() + 1);
                else
                    mutableData.setValue((long)mutableData.getValue() - 1);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(databaseError == null && b == true)
                    sendTraceLikeClickedUserToFirebase(myRef, isLikeClicked);
                else
                    sendTraceLikeToFirebase(isLikeClicked, trace);
            }
        });
    }

    private void sendTraceLikeClickedUserToFirebase(final DatabaseReference myRef, final boolean isLikeClicked) {
        myRef.child("likeUserList").child(User.getMyInstance().getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isLikeClicked)
                    dataSnapshot.getRef().setValue("");
                else
                    dataSnapshot.getRef().removeValue();
                isWorking = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                sendTraceLikeClickedUserToFirebase(myRef, isLikeClicked);
            }
        });
    }

    @Override
    public void getReviewNumberFromServer(String buildingID, final TextView reviewNumber) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("building").child(buildingID).child("trace");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    reviewNumber.setText(dataSnapshot.getChildrenCount() + "");
                }
                else
                    reviewNumber.setText("0");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}