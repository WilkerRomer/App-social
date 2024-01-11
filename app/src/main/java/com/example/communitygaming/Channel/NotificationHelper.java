package com.example.communitygaming.Channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.example.communitygaming.Models.Message;
import com.example.communitygaming.R;

import java.util.Date;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "com.example.communitygaming";
    private static final String CHANNEL_NAME = "CommunityGaming";

    private NotificationManager manager;

    public NotificationHelper(Context context){
        super(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels(){
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getNotification(String title, String body){
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setColor(Color.GRAY)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }

    public NotificationCompat.Builder getNotificationMessage(
            Message[] messages,
            String usuarioSender,
            String usuarioReceiver,
            String lastMessage,
            Bitmap bitmapSender,
            Bitmap bitmapReceiver,
            NotificationCompat.Action action) {

        Person person1 = null;
        if (bitmapReceiver == null){
            person1 = new Person.Builder()
                    .setName(usuarioReceiver)
                    .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_baseline_person_24))
                    .build();
        }
        else{
            person1 = new Person.Builder()
                    .setName(usuarioReceiver)
                    .setIcon(IconCompat.createWithBitmap(bitmapReceiver))
                    .build();
        }

        Person person2 = null;
        if (bitmapSender == null){
            person2 = new Person.Builder()
                    .setName(usuarioSender)
                    .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_baseline_person_24))
                    .build();
        }
        else{
            person2 = new Person.Builder()
                    .setName(usuarioSender)
                    .setIcon(IconCompat.createWithBitmap(bitmapSender))
                    .build();
        }

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(person1);
        NotificationCompat.MessagingStyle.Message messasge1 = new
                                    NotificationCompat.MessagingStyle.Message(
                lastMessage,
                                            new Date().getTime(),
                                            person1);
        messagingStyle.addMessage(messasge1);

        for (Message m: messages){
            NotificationCompat.MessagingStyle.Message messasge2 = new
                    NotificationCompat.MessagingStyle.Message(
                    m.getMessage(),
                    m.getFecha(),
                    person2);
            messagingStyle.addMessage(messasge2);
        }
        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(messagingStyle)
                .addAction(action);
    }
}
