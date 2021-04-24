package com.example.myCookApp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.myCookApp.activities.ExecuteRecipeActivity;
import com.example.myCookApp.activities.MainActivity;
import com.example.myCookApp.activities.RecipeInfoActivity;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "channelName";
    private NotificationManager notificationManager;
    private int position;
    private long recipeId;

    public NotificationHelper(Context base, int position, long recipeId) {
        super(base);
        this.position = position;
        this.recipeId = recipeId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannel(){
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.GREEN);
        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if (notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public NotificationCompat.Builder getChannelNotification(){
        Intent notificationIntent = new Intent(getApplicationContext(), ExecuteRecipeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
        //stackBuilder.addNextIntentWithParentStack(intent);

        Intent recipeInfoIntent = new Intent(this, RecipeInfoActivity.class);
        //recipeInfoIntent.putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipeId);

        Intent mainActivityIntent = new Intent(this, MainActivity.class);

        stackBuilder.addNextIntent(mainActivityIntent);
        stackBuilder.addNextIntent(recipeInfoIntent);
        stackBuilder.addNextIntent(notificationIntent);
        stackBuilder.editIntentAt(1).putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipeId);
        stackBuilder.editIntentAt(2).putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipeId);
        stackBuilder.editIntentAt(2).putExtra(ExecuteRecipeActivity.VIEWPAGER_CURRENT_ITEM, position);
        PendingIntent resultPendingIntent  = stackBuilder.getPendingIntent(position, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.ic_baseline_local_dining_24)
                .setContentTitle("MyCookApp")
                .setContentText("Τέλος χρόνου. Η παρασκευή σας είναι έτοιμη!")
                .setContentIntent(resultPendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

    }
}
