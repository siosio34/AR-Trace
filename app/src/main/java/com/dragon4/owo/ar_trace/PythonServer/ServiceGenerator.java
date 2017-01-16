package com.dragon4.owo.ar_trace.PythonServer;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by joyeongje on 2017. 1. 11..
 */

public class ServiceGenerator {

    // TODO: 2017. 1. 11. 파이썬 유알엘을 채워넣어야된다.
    public static final String API_BASE_URL = "http://192.168.1.41:8009/upload";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());


    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
