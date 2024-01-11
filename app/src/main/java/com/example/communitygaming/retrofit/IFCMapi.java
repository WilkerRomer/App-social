package com.example.communitygaming.retrofit;

import com.example.communitygaming.Models.FCMBody;
import com.example.communitygaming.Models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMapi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAKgu-4WE:APA91bHfzS-wN7mArA9eMcr-F8bAlOwUJr6TWXYksFwv6-9zY0Pk8MRaGU7TzcrCdUcAmbR-o2bnQw-KcDfT0csEfvrvAJxoQN35FvHHxeEd031YrXyw6_oPXFmbBoQD1AQus3XnRu8s"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
