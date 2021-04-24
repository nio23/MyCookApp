package com.example.myCookApp.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.example.myCookApp.AlertReceiver;
import com.example.myCookApp.ExecuteRecipeService;
import com.example.myCookApp.R;
import com.example.myCookApp.adapters.ExecuteRecipeViewPagerAdapter;
import com.example.myCookApp.models.Step;
import com.example.myCookApp.viewmodels.ExecuteRecipeViewModel;
import com.example.myCookApp.viewmodels.ViewModelFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ExecuteRecipeActivity extends AppCompatActivity {

    public static final String SERVICE_REQUEST_CODE = "com.example.myCookApp.activities.ExecuteRecipeActivity.Service_Request_Code";
    public static final String VIEWPAGER_CURRENT_ITEM = "com.example.myCookApp.activities.ExecuteRecipeActivity.ViewPager2_Current_Item";
    private long recipeId ;
    private ExecuteRecipeViewModel viewModel;
    private ExecuteRecipeViewPagerAdapter adapter;
    private ViewPager2 viewPager2;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private int viewpager2CurrentItem = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execute_recipe);
        viewPager2 = findViewById(R.id.executeRecipeVP);

        Intent intent = getIntent();
        if (intent.getExtras() != null){
            recipeId = intent.getExtras().getLong(RecipeInfoActivity.EXTRA_RECIPE_ID);
            if (intent.hasExtra(VIEWPAGER_CURRENT_ITEM))
                viewpager2CurrentItem = intent.getExtras().getInt(VIEWPAGER_CURRENT_ITEM);
        }
        //recipeId = getIntent().getExtras().getLong(RecipeInfoActivity.EXTRA_RECIPE_ID);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication(), recipeId)).get(ExecuteRecipeViewModel.class);
        adapter = new ExecuteRecipeViewPagerAdapter();


        viewPager2.setAdapter(adapter);


        adapter.setOnItemClickListener(new ExecuteRecipeViewPagerAdapter.OnItemClickListener() {
            @Override
            public void startOrStopTimer(Step step, int position) {
                if (step.isRunning()){
                    viewModel.cancelCountDown(step.getStepId());
                }else{
                    viewModel.startCountDown(recipeId, position, step.getStepTime(), step.getStepId());
                }
            }

            @Override
            public void checkBoxState(Boolean state, int position) {
                viewModel.setNotifyMe(position, state);
            }
        });



        viewModel.getStatus().observe(this, listOfWorkInfos -> {
            if (listOfWorkInfos == null || listOfWorkInfos.isEmpty()){
                return;
            }

            Log.d("ExecuteRecipeActivity", listOfWorkInfos.get(0).getState().toString());


            for (WorkInfo workInfo: listOfWorkInfos){
                if (workInfo.getState().isFinished()){
                    if (workInfo.getState() == WorkInfo.State.SUCCEEDED){
                        Data outputData = workInfo.getOutputData();
                        long stepId = outputData.getLong("stepId",0);
                        int position = outputData.getInt("position",0);
                        viewModel.updateStepState(stepId, false);
                        if (viewModel.getNotifyMe(position))
                        createAlarm(position);
                    }
                    viewModel.pruneWork();
                }
                /*if (workInfo.getState().isFinished() && workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    Data outputData = workInfo.getOutputData();
                    long stepId = outputData.getLong("stepId",0);
                    viewModel.updateStepState(stepId, false);
                    Log.d("ExecuteRecipeActivity", workInfo.getId().toString()+"  State.Succeeded");
                }

                if (workInfo.getState().isFinished() && workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    Log.d("ExecuteRecipeActivity", "WorkInfo State.Succeeded");
                    createAlarm(1,1);
                }*/

            }

        });


    }


    private void createAlarm(int position){
        Log.d("ALARM", "Alarm activated, requestCode: "+position);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
        intent.putExtra("position", position);
        intent.putExtra("recipeId", recipeId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), position, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
    }

    private void cancelAlarm(long requestCode){
        Log.d("ALARM", "Alarm canceled, requestCode: "+requestCode);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), (int)requestCode, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void startService(long requestCode, int position){
        Intent serviceIntent = new Intent(this, ExecuteRecipeService.class);
        serviceIntent.putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipeId);
        serviceIntent.putExtra(VIEWPAGER_CURRENT_ITEM, position);
        serviceIntent.putExtra(SERVICE_REQUEST_CODE, requestCode);
        startService(serviceIntent);
    }

    public void stopService(){
        Intent serviceIntent = new Intent(this, ExecuteRecipeService.class);
        stopService(serviceIntent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        disposables.add(viewModel.getStepsWithImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( stepWithImages ->{
                    if (stepWithImages != null) {
                        adapter.submitList(stepWithImages);

                    }
                }));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (viewpager2CurrentItem != -1){
            viewPager2.post(new Runnable() {
                @Override
                public void run() {
                    viewPager2.setCurrentItem(viewpager2CurrentItem, false);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }


}
