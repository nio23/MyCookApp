package com.example.myCookApp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Data;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myCookApp.activities.ExecuteRecipeActivity;
import com.example.myCookApp.activities.MainActivity;
import com.example.myCookApp.activities.RecipeInfoActivity;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CountDownWorker extends Worker {

    private final NotificationManager notificationManager;
    public static final String TAG = "MyWorker";

    public CountDownWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        int position = getInputData().getInt("position", 0);
        int time = getInputData().getInt("time",0);
        long recipeId = getInputData().getLong("recipeId", 0);
        long stepId = getInputData().getLong("stepId", 0);

        setForegroundAsync(createForegroundInfo(recipeId, position, time));
        if (time > 0){
            for (int i=time; i>=0 ; i--) {
                if (isStopped()){
                    break;
                }
                try {
                    Thread.sleep(1000);
                    //Log.d("Counter",""+i);
                } catch (InterruptedException e) {
                    return Result.failure();
                }
                if (i%10==0)
                setForegroundAsync(createForegroundInfo(recipeId, position, i));

            }

        }

        /*if (time > 0){
            for (int i=0; i<=10 ; i++) {
                if (isStopped()){
                    break;
                }
                try {
                    Thread.sleep(1000);
                    Log.d("Counter",""+i);
                } catch (InterruptedException e) {
                    return Result.failure();
                }
                if (i%10==0)
                setForegroundAsync(createForegroundInfo(recipeId, position, i));

            }

        }*/

        Data outputData = new Data.Builder()
                .putLong("stepId", stepId)
                .putInt("position", position)
                .build();
        return Result.success(outputData);
    }

    private ForegroundInfo createForegroundInfo(long recipeId, int position, int progress){
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, ExecuteRecipeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent recipeInfoIntent = new Intent(context, RecipeInfoActivity.class);
        recipeInfoIntent.putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipeId);

        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);
        stackBuilder.addNextIntent(recipeInfoIntent);
        stackBuilder.addNextIntent(notificationIntent);
        stackBuilder.editIntentAt(1).putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipeId);
        stackBuilder.editIntentAt(2).putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipeId);
        stackBuilder.editIntentAt(2).putExtra(ExecuteRecipeActivity.VIEWPAGER_CURRENT_ITEM, position);

        PendingIntent resultPendingIntent  = stackBuilder.getPendingIntent(position, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(position);
        }
        /*PendingIntent intent = WorkManager.getInstance(context)
                .createCancelPendingIntent(getId());*/
        Notification notification = new NotificationCompat.Builder(context, String.valueOf(position))
                .setContentTitle("Χρονόμετρο")
                .setContentText("Χρόνος που απομένει: "+timeLeft(progress))
                .setSmallIcon(R.drawable.ic_baseline_local_dining_24)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
                .build();
        return new ForegroundInfo(position, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(int position) {
        NotificationChannel channel = new NotificationChannel(String.valueOf(position)
                , "Service Channel",
                NotificationManager.IMPORTANCE_LOW);
        channel.enableVibration(false);
        notificationManager.createNotificationChannel(channel);

    }

    private String timeLeft(int timeLeft){
        int hours = timeLeft / 3600;
        int minutes = (timeLeft % 3600) / 60;
        int seconds = timeLeft % 60;
        if (hours != 0)
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

    }

}
