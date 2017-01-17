package com.dragon4.owo.ar_trace.ARCore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dragon4.owo.ar_trace.Model.Trace;
import com.dragon4.owo.ar_trace.R;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * Created by mansu on 2017-01-16.
 */

public class WriteReviewActivity extends Activity implements View.OnClickListener {
    //middle image between picture chooser and video chooser.
    private ImageView middleImg;

    //take picture request number
    private final int REQUEST_IMAGE = 1;

    //save file destination after taking picture.
    private File destination = null;

    //count for upload image
    private int uploadedCount;
    private int uploadFailCount;
    private int uploadedThumbnailCount;
    private int uploadFailThumbnailCount;

    private Context context;
    // TODO: 2017. 1. 16. 인텐트로 넘기기전에 경도 위도를 받아오도록하자

    //choosed bitmap
    private Bitmap currentBitmap = null;

    private Double currentLat;
    private Double currentLon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_write_review);

        context = getApplicationContext();
        middleImg = (ImageView)findViewById(R.id.ar_mixview_write_review_middle_img);
        //register onclick listener
        findViewById(R.id.ar_mixview_write_review_back).setOnClickListener(this);
        findViewById(R.id.ar_mixview_write_review_axis).setOnClickListener(this);
        findViewById(R.id.ar_mixview_write_review_location).setOnClickListener(this);
        findViewById(R.id.ar_mixview_write_review_picture).setOnClickListener(this);
        findViewById(R.id.ar_mixview_write_review_register).setOnClickListener(this);

        currentLat = getIntent().getDoubleExtra("lat",0.0);
        currentLon = getIntent().getDoubleExtra("lon",0.0);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ar_mixview_write_review_back:
                finish();
                break;

            case R.id.ar_mixview_write_review_axis:
                middleImg.setImageResource(R.drawable.icon_rhombus_left_chosen);
                break;

            case R.id.ar_mixview_write_review_location:
                middleImg.setImageResource(R.drawable.icon_rhombus_right_chosen);
                break;

            case R.id.ar_mixview_write_review_picture:
                choosePictureCase();
                break;

            case R.id.ar_mixview_write_review_register:
                uploadTraceToServer();
                break;
        }
    }

    public void choosePictureCase() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ARTrace/");

        if (!file.exists())
            file.mkdir();

        destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ARTrace/" + System.currentTimeMillis() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        // uploadImageToPythonServer(Uri.fromFile(destination));
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                try {

                    uploadImageToPythonServer(Uri.fromFile(destination));

                    FileInputStream in = null;
                    in = new FileInputStream(destination);

                    currentBitmap = BitmapFactory.decodeStream(in, null, null);

                    //bitmap is too large to be uploaded into a texture problem.
                    //check max texture size
                    /*
                    int[] maxTextureSize = new int[1];
                    GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
                    */

                    ImageView currentImageView = new ImageView(this);
                    View picture = findViewById(R.id.ar_mixview_write_review_picture);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(currentBitmap, picture.getMeasuredWidth(), picture.getMeasuredHeight(), true);
                    currentImageView.setImageBitmap(scaledBitmap);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(picture.getMeasuredWidth(), picture.getMeasuredHeight());
                    currentImageView.setLayoutParams(params);

                    //delete current pictureview and add new pictureview
                    ViewGroup parent = (ViewGroup) picture.getParent();
                    int index = parent.indexOfChild(picture);
                    parent.removeView(picture);
                    parent.addView(currentImageView, index);

                    //set id and register onclicklistener
                    currentImageView.setId(R.id.ar_mixview_write_review_picture);
                    currentImageView.setOnClickListener(this);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                destination.delete();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                destination.delete();
            }
        }

    }

    private void uploadTraceToServer() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("잠시만 기다려주세요.");
        dialog.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("building").child(/*building id*/"1");
        myRef.push().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Location curLoc = mixContext.getCurrentLocation();
                // Double lat = curLoc.getLatitude();
                // Double lon = curLoc.getLongitude();

                Trace trace = new Trace();
                String requestLocationIdURL;
                
                //set location id of trace
                // TODO: 2017. 1. 17. 해쉿값뽑아내기
                trace.setLocationID("1");

                //use key as trace id
                trace.setTraceID(dataSnapshot.getKey());

                //add content to trace
                EditText content = (EditText)findViewById(R.id.ar_mixview_write_review_content);
                if(content != null)
                    trace.setContent(content.getText().toString());
                else
                    trace.setContent("");

                trace.setLat(currentLat);
                trace.setLon(currentLon);
                // image url, tumbnailurl 은 아래 함수서 뽑아냄

                // like num은 어차피 0
                // lat, lon 뽑아내야함.
                // placeName 뽑아내야함
                // userID가 없네... !

                //add current date to trace
                trace.setWriteDate(new Date());

                //register trace
                dataSnapshot.getRef().setValue(trace);

                if(currentBitmap != null)
                    uploadImageToServer(trace, dialog);
                else {
                    Toast.makeText(getApplicationContext(), "업로드에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }


    private void uploadImageToPythonServer(final Uri fileURI) throws IOException {
        // TODO: 2017. 1. 16. 파일의 uri 가져오는거 ㄲ

        final String charset = "UTF-8";
        final String requestURL = "http://192.168.1.41:8009/upload";
        new Thread(new Runnable() {

            @Override
            public void run() {
                MultipartUtility multipart = null;
                try {
                    multipart = new MultipartUtility(requestURL, charset);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                multipart.addFormField("param_name_1", "param_value");
                multipart.addFormField("param_name_2", "param_value");
                multipart.addFormField("param_name_3", "param_value");
                try {
                    multipart.addFilePart("file", new File(fileURI.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String response = null; // response from server.
                try {
                    response = multipart.finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("response code",response);

            }
        }).start();

    }

    private void uploadImageToServer(final Trace trace, final ProgressDialog dialog) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadedCount = 0;
                uploadFailCount = 0;
                uploadedThumbnailCount = 0;
                uploadFailThumbnailCount = 0;
                final Object lock = new Object();

                if(currentBitmap != null) {
                    double scale = 0;
                    if(currentBitmap.getWidth() > currentBitmap.getHeight())
                        scale = currentBitmap.getWidth() / 256;
                    else
                        scale = currentBitmap.getHeight() / 256;
                    if(scale == 0)
                        scale = 1;

                    ByteArrayOutputStream jpegOut = new ByteArrayOutputStream();
                    currentBitmap.compress(Bitmap.CompressFormat.JPEG, 70, jpegOut);

                    Bitmap thumbnail = Bitmap.createScaledBitmap(currentBitmap, (int)(currentBitmap.getWidth() / scale), (int)(currentBitmap.getHeight() / scale), true);
                    ByteArrayOutputStream jpegThumbnailOut = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, jpegThumbnailOut);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference myRef = storage.getReference().child(trace.getLocationID()).child(trace.getTraceID()+".jpg");
                    StorageReference thumbnailRef = storage.getReference().child(trace.getLocationID()).child("sn-"+trace.getTraceID()+".jpg");

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
                                if((uploadedCount + uploadFailCount + uploadFailThumbnailCount + uploadedThumbnailCount) == 2) {
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

                                uploadedCount++;
                                if((uploadedCount + uploadFailCount + uploadFailThumbnailCount + uploadedThumbnailCount) == 2) {
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
                                if((uploadedCount + uploadFailCount + uploadFailThumbnailCount + uploadedThumbnailCount) == 2) {
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

                                //trace.setThumbnailURL(downloadUri.toString());

                                //Only the original thread that created a view hierarchy can touch its views.
                                uploadedThumbnailCount++;
                                if((uploadedCount + uploadFailCount + uploadFailThumbnailCount + uploadedThumbnailCount) == 2) {
                                    synchronized (lock) {
                                        lock.notify();
                                    }
                                }
                            }
                        });
                    } else
                        Toast.makeText(getApplicationContext(), "이미 존재하는 파일 이름입니다. 파일 이름을 변경해 주세요", Toast.LENGTH_SHORT).show();
                }

                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                dialog.dismiss();

            }
        }).start();
        Toast.makeText(getApplicationContext(), "업로드에 성공하였습니다.", Toast.LENGTH_SHORT).show();
    }


}