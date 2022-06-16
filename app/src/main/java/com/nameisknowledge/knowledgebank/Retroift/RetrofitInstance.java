package com.nameisknowledge.knowledgebank.Retroift;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class RetrofitInstance  {
    private final String fcmServerKey ="AAAAMwrqCqc:APA91bHeXudZgXI8kAEfZZlM4K08vECU2kqfrBe_AyQyW7t22n6Xw0niqHt6fkyXI6p6HflCBT_ejjDDE2HZHc_F7Iiw_exJj_12EBM8KzJVwNvz-XAi29Jyf7XlG9CW8zr9Lqgn_2mF";
    private final ApiInterface apiInterface;
    private static RetrofitInstance retrofitInstance;

    private RetrofitInstance() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder();
                requestBuilder.header("content-type", "application/json");
                requestBuilder.header("authorization", "key=" + fcmServerKey);
                return chain.proceed(requestBuilder.build());
            }
        });
        String baseUrl = "https://fcm.googleapis.com";
        this.apiInterface = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).client(httpClient).build().create(ApiInterface.class);
    }

    public static RetrofitInstance getInstance(){
        if (retrofitInstance == null){
            retrofitInstance = new RetrofitInstance();
        }
        return retrofitInstance;
    }

    public void sentNot(PushNotification pushNotification){
        apiInterface.pushNot(pushNotification).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Response<PushNotification> response, Retrofit retrofit) {
                // success code
            }

            @Override
            public void onFailure(Throwable t) {
                // error code
            }
        });
    }
}
