package com.example.myCookApp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.myCookApp.activities.ExecuteRecipeActivity;
import com.example.myCookApp.activities.RecipeInfoActivity;

import static com.example.myCookApp.App.CHANNEL_ID;

public class ExecuteRecipeService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private final class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            try{
                Thread.sleep(10000);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }

            stopSelf(msg.arg1);
        }
    }


    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long recipeId = intent.getExtras().getLong(RecipeInfoActivity.EXTRA_RECIPE_ID);
        int position = intent.getExtras().getInt(ExecuteRecipeActivity.VIEWPAGER_CURRENT_ITEM);
        long requestCode = intent.getExtras().getLong(ExecuteRecipeActivity.SERVICE_REQUEST_CODE);

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);
        Toast.makeText(this,""+requestCode,Toast.LENGTH_SHORT).show();

        Intent notificationIntent = new Intent(this, ExecuteRecipeActivity.class);
        //notificationIntent.putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipeId);

        /*Intent recipeInfoIntent = new Intent(this, RecipeInfoActivity.class);
        recipeInfoIntent.putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipeId);

        Intent mainActivityIntent = new Intent(this, MainActivity.class);*/

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        stackBuilder.editIntentAt(1).putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipeId);
        stackBuilder.editIntentAt(2).putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipeId);
        stackBuilder.editIntentAt(2).putExtra(ExecuteRecipeActivity.VIEWPAGER_CURRENT_ITEM, position);
        /*stackBuilder.addNextIntent(mainActivityIntent);
        stackBuilder.addNextIntent(recipeInfoIntent);
        stackBuilder.addNextIntent(notificationIntent);*/

        PendingIntent resultPendingIntent  = stackBuilder.getPendingIntent((int) requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
        /*PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MyCookApp")
                .setContentText("Χρόνος που απομένει:")
                .setSmallIcon(R.drawable.ic_favorite_24dp)
                .setContentIntent(resultPendingIntent)
                .build();

        /*NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify((int) requestCode, notification);*/
        startForeground(1, notification);


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
