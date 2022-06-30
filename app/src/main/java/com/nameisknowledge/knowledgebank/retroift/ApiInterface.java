package com.nameisknowledge.knowledgebank.retroift;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ApiInterface {
  //  @Headers({"authorization: key=AAAAHvRq5t0:APA91bH8MLB1oMQ-34Dnh6uEJB-Q5IeZOdf57WpzpAlLnmgrY45tSeI-XtNeaYP4_p8NxAob2g5Vo-jk87983FG4zBJQ4k778fTFJthi7TqGLuLix_4vpkBFNFmCFAdsA_k_qCU4aqsy","content-type:application/json"})
    @POST("fcm/send")
    Call<PushNotification> pushNot(@Body PushNotification pushNotification);
}
