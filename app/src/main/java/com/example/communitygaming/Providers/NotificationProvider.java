package com.example.communitygaming.Providers;


import com.example.communitygaming.Models.FCMBody;
import com.example.communitygaming.Models.FCMResponse;
import com.example.communitygaming.retrofit.IFCMapi;
import com.example.communitygaming.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider(){

    }

    public Call<FCMResponse> setNotification(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMapi.class).send(body);
    }
}
