package com.dragon4.owo.ar_trace.Network.Firebase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.Model.User;
import com.dragon4.owo.ar_trace.Network.ClientSelector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by joyeongje on 2017. 1. 20..
 */

public class FirebaseClient implements ClientSelector{

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("building");
    String makeKey;

    private int uploadedCount = 0;
    private int uploadFailCount = 0;
    private int uploadedThumbnailCount = 0;
    private int uploadFailThumbnailCount = 0;

    private String makeTraceKey() {
        makeKey = myRef.push().getKey();
        return makeKey;
    }

    @Override
    public void uploadImageToServer(final Trace trace, final Bitmap currentBitmap) {

        final String traceKey = makeTraceKey();
        trace.setTraceID(traceKey);

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
                                DatabaseReference databaseRef = database.getReference("building").child(trace.getLocationID()).child(trace.getTraceID()).child("imageURL");
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
                                DatabaseReference databaseRef = database.getReference("building").child(trace.getLocationID()).child(trace.getTraceID()).child("thumbnailURL");
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
        DatabaseReference locationRef = myRef.child(trace.getLocationID()).child(trace.getTraceID());
        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(trace);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public List<Trace> getTraceDataFromServer() {
        return null;
    }

    @Override
    public void registerUser(User user) {

    }

    @Override
    public void checkUser(String user_id) {

    }

    @Override
    public User login() {
        return null;
    }
}
