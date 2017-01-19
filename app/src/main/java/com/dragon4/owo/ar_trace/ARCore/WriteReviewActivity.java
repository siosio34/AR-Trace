package com.dragon4.owo.ar_trace.ARCore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

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
    private String placeName;

    TextView axisTitle;
    TextView axisNum;
    TextView locationTitle;
    TextView locationName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ar_mixview_write_review);
        loadActivity();
    }

    public void loadActivity() {

        context = getApplicationContext();
        middleImg = (ImageView)findViewById(R.id.ar_mixview_write_review_middle_img);

        //register onclick listener
        findViewById(R.id.ar_mixview_write_review_back).setOnClickListener(this);
        findViewById(R.id.ar_mixview_write_review_axis).setOnClickListener(this);
        findViewById(R.id.ar_mixview_write_review_location).setOnClickListener(this);
        findViewById(R.id.ar_mixview_write_review_add).setOnClickListener(this);
        findViewById(R.id.ar_mixview_write_review_register).setOnClickListener(this);
        axisTitle = (TextView)findViewById(R.id.ar_mixview_write_review_axis_title);
        axisNum = (TextView)findViewById(R.id.ar_mixview_write_review_axis_num);
        locationTitle = (TextView)findViewById(R.id.ar_mixview_write_review_location_title);
        locationName = (TextView)findViewById(R.id.ar_mixview_write_review_location_name);

        currentLat = getIntent().getDoubleExtra("lat",0.0);
        currentLon = getIntent().getDoubleExtra("lon",0.0);

        // 경도 위도
        TextView axisView = (TextView)findViewById(R.id.ar_mixview_write_review_axis_num);
        axisView.setText(String.valueOf(String.valueOf(currentLat) + " N " + String.valueOf(currentLon) + " E "));

        // 경도 위도를 좌표 변환후 주소로 표시.
        TextView locationNameView = (TextView)findViewById(R.id.ar_mixview_write_review_location_name);
        String requestReverseGeoAPI = DataSource.createNaverGeoAPIRequcetURL(currentLat,currentLon);
        try {
            String reverseGeoString = new HttpHandler().execute(requestReverseGeoAPI).get();
            placeName = parsingReverseGeoJson(reverseGeoString);
            locationNameView.setText(placeName);
            Log.i("parsing Address",locationNameView.getText().toString());

        } catch (InterruptedException | JSONException | ExecutionException e) {

        }
    }

    public String parsingReverseGeoJson(String reverseGeoString) throws JSONException {
        String locationName = "";
        JSONObject reverseObject = new JSONObject(reverseGeoString);
        JSONArray dataList = reverseObject.getJSONObject("result").getJSONArray("items");
        for(int i = 0 ; i < dataList.length(); i++) {
            if(dataList.getJSONObject(i).getBoolean("isRoadAddress")) {
                locationName = dataList.getJSONObject(i).getString("address");
            }
        }
        return locationName;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ar_mixview_write_review_back:
                finish();
                break;

            case R.id.ar_mixview_write_review_axis:
                chooseAxis();
                break;

            case R.id.ar_mixview_write_review_location:
                chooseLocation();
                break;

            case R.id.ar_mixview_write_review_add:
                choosePictureCase();
                break;

            case R.id.ar_mixview_write_review_register:
                uploadTraceToServer();
                break;
        }
    }

    private void chooseAxis() {
        middleImg.setImageResource(R.drawable.icon_rhombus_left_chosen);
        axisTitle.setTextColor(Color.parseColor("#A6000000"));
        axisNum.setTextColor(Color.parseColor("#A6000000"));
        locationTitle.setTextColor(Color.parseColor("#5B000000"));
        locationName.setTextColor(Color.parseColor("#42000000"));
    }

    private void chooseLocation() {
        middleImg.setImageResource(R.drawable.icon_rhombus_right_chosen);
        axisTitle.setTextColor(Color.parseColor("#5B000000"));
        axisNum.setTextColor(Color.parseColor("#42000000"));
        locationTitle.setTextColor(Color.parseColor("#A6000000"));
        locationName.setTextColor(Color.parseColor("#A6000000"));
    }

    private void choosePictureCase() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ARTrace/");

        if (!file.exists())
            file.mkdir();

        destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ARTrace/" + System.currentTimeMillis() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                try {

                    // if python server on
                    //uploadImageToPythonServer(Uri.fromFile(destination));

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
                    View picture = findViewById(R.id.ar_mixview_write_review_add);
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
                    currentImageView.setId(R.id.ar_mixview_write_review_add);
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

                Trace trace = new Trace();
                
                //set location id of trace
                String hashKey = MixUtils.makeHashStringMD5(currentLon,currentLon);
                trace.setLocationID(hashKey);

                //use key as trace id
                trace.setTraceID(dataSnapshot.getKey());

                //add content to trace
                EditText content = (EditText)findViewById(R.id.ar_mixview_write_review_content);
                if(content != null)
                    trace.setContent(content.getText().toString());
                else
                    trace.setContent("");

                // 이미지 url , 썸네일 url 추가
                if(currentBitmap != null)
                    uploadImageToServer(trace, dialog);
                else {
                    Toast.makeText(getApplicationContext(), "업로드에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                // 경도 위도
                trace.setLat(currentLat);
                trace.setLon(currentLon);

                // 장소이름
                trace.setPlaceName(placeName);

                // 업로드 날짜
                trace.setWriteDate(new Date());

                //register trace
                dataSnapshot.getRef().setValue(trace);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        setResult(RESULT_OK);
    }
}