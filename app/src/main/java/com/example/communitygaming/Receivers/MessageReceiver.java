package com.example.communitygaming.Receivers;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;



import androidx.annotation.NonNull;



import com.example.communitygaming.Models.FCMBody;
import com.example.communitygaming.Models.FCMResponse;
import com.example.communitygaming.Models.Message;
import com.example.communitygaming.Providers.MessagesProviders;
import com.example.communitygaming.Providers.NotificationProvider;
import com.example.communitygaming.Providers.TokenProviders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.communitygaming.Service.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

public class MessageReceiver extends BroadcastReceiver {

    String mExtraIdSender;
    String mExtraIdReceiver;
    String mExtraIdChat;
    String mExtraUsuarioSender;
    String mExtraUsuarioReceiver;
    String mExtraImageSender;
    String mExtraImageReceiver;
    int mEtraIdNotification;

    TokenProviders mTokenProvider;
    NotificationProvider mNotificatioProvider;

    @Override
    public void onReceive(Context context, Intent intent) {
        mExtraIdSender = intent.getExtras().getString("idSender");
        mExtraIdReceiver = intent.getExtras().getString("idReceiver");
        mExtraIdChat = intent.getExtras().getString("idChat");
        mExtraUsuarioSender = intent.getExtras().getString("usuarioSender");
        mExtraUsuarioReceiver = intent.getExtras().getString("usuarioReceiver");
        mExtraImageSender = intent.getExtras().getString("imageSender");
        mExtraImageReceiver = intent.getExtras().getString("imageReceiver");

        mEtraIdNotification = intent.getExtras().getInt("idNotifiaction");

        mTokenProvider = new TokenProviders();
        mNotificatioProvider = new NotificationProvider();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mEtraIdNotification);

        String message = getMessageText(intent).toString();

        sendMessage(message);
    }

    private void sendMessage(String messageText) {
        final Message message = new Message();
        message.setIdChat(mExtraIdChat);
        message.setIdSender(mExtraIdReceiver);
        message.setIdReceiver(mExtraIdSender);
        message.setFecha(new Date().getTime());
        message.setViewed(false);
        message.setIdChat(mExtraIdChat);
        message.setMessage(messageText);

        MessagesProviders messagesProviders = new MessagesProviders();

        messagesProviders.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    getToken(message);
                }
            }
        });
    }

    private void getToken(Message message){
        mTokenProvider.getToken(mExtraIdSender).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Gson gson = new Gson();
                        ArrayList<Message> messageArray = new ArrayList<>();
                        messageArray.add(message);
                        String messages = gson.toJson(messageArray);
                        sendNotification(token, messages, message);
                    }
                }
            }
        });
    }

    private void sendNotification(String token, String messages, Message message){
        Map<String, String> data = new HashMap<>();
        data.put("title", "NUEVO MENSAJE");
        data.put("body", message.getMessage());
        data.put("idNotification", String.valueOf(mEtraIdNotification));
        data.put("messages", messages);
        data.put("usuarioSender", mExtraUsuarioReceiver);
        data.put("usuarioReceiver", mExtraUsuarioSender);
        data.put("idSender", message.getIdSender());
        data.put("idReceiber", message.getIdReceiver());
        data.put("idChat", message.getIdChat());
        data.put("imageSender", mExtraImageReceiver);
        data.put("imageReceiver", mExtraIdSender);

        FCMBody body = new FCMBody(token, "high", "4500s", data);
        mNotificatioProvider.setNotification(body).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.d("ERROR", "El error fue: " + t.getMessage());
            }
        });
    }

    private CharSequence getMessageText(Intent intent){
        Bundle remoteImput = RemoteInput.getResultsFromIntent(intent);
        if (remoteImput != null){
            return remoteImput.getCharSequence(NOTIFICATION_REPLY);
        }
        return null;
    }
}
