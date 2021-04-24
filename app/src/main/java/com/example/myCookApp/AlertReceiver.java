package com.example.myCookApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int position;
        long recipeId;

        position = intent.getExtras().getInt("position");
        recipeId = intent.getExtras().getLong("recipeId");

        NotificationHelper notificationHelper = new NotificationHelper(context, position, recipeId);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(position, nb.build());
    }


}
