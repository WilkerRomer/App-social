package com.example.communitygaming.Utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.UsuariosProviders;

import java.util.List;

public class ViewedMessageHelper {

    public static void updateOnline(boolean status, final Context context ){
       UsuariosProviders mUsuariosProviders = new UsuariosProviders();
        AuthProviders mAuthProviders = new AuthProviders();
        if (mAuthProviders.getUid() != null){
            if (isApplicationSentToBackground(context)){
                mUsuariosProviders.updateOnline(mAuthProviders.getUid(), status);
            }
            else if(status){
                mUsuariosProviders.updateOnline(mAuthProviders.getUid(), status);
            }
        }
    }

    public static boolean isApplicationSentToBackground(final Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
