package com.example.myCookApp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.myCookApp.CountDownWorker;
import com.example.myCookApp.Repository;
import com.example.myCookApp.models.RecipeWithStepsAndImages;
import com.example.myCookApp.models.Step;
import com.example.myCookApp.models.StepWithImages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

public class ExecuteRecipeViewModel extends ViewModel {
    private final Repository repository;
    private final long recipeId;
    private final Flowable<RecipeWithStepsAndImages> recipeWithStepsAndImagesFlowable;
    private WorkManager workManager;
    private LiveData<List<WorkInfo>> status;
    //private MutableLiveData<List<Boolean>> notifyMeCheckBoxes;
    //private ArrayList<Boolean> notifyMeCheckBoxes ;
    private HashMap<Integer, Boolean> notifyMe;


    public ExecuteRecipeViewModel(Application application, long recipeId) {
        repository = new Repository(application);
        this.recipeId = recipeId;
        recipeWithStepsAndImagesFlowable = repository.getRecipeWithStepsAndImagesFlowable(recipeId);
        workManager = WorkManager.getInstance(application);
        status = workManager.getWorkInfosByTagLiveData("ddd");
       /* if (notifyMeCheckBoxes == null) {
            notifyMeCheckBoxes = new ArrayList<>();
        }*/
        if (notifyMe == null){
            notifyMe = new HashMap<>();
        }
        /*if (notifyMeCheckBoxes == null){
            notifyMeCheckBoxes = new MutableLiveData<>();
        }*/
    }

    public boolean getNotifyMe(int position) {
        if (notifyMe.containsKey(position))
            return notifyMe.get(position);
        else
            return false;
    }

    public void setNotifyMe(int position, Boolean state) {
        if (notifyMe.containsKey(position)){
            notifyMe.remove(position);
        }
        notifyMe.put(position, state);
    }

    /* public boolean getNotifyMeState(int position){
        return notifyMeCheckBoxes.get(position);
    }

    public void setNotifyMeCheckBoxes(Boolean state, int position) {
        this.notifyMeCheckBoxes.set(position, state);
    }*/

    /* public void setNotifyMeCheckBox(Boolean notifyMeCheckBox, int position) {
        ArrayList<Boolean> checkBoxes = new ArrayList<>(this.notifyMeCheckBoxes.getValue());
        checkBoxes.set(position,notifyMeCheckBox);
        this.notifyMeCheckBoxes.setValue(checkBoxes);
    }*/

    /*public MutableLiveData<List<Boolean>> getNotifyMeCheckBoxes() {
        return notifyMeCheckBoxes;
    }*/

    public LiveData<List<WorkInfo>> getStatus() {
        return status;
    }

    public Flowable<List<StepWithImages>> getStepsWithImages(){
        return recipeWithStepsAndImagesFlowable.map(item -> item.getStepsWithImages());
    }

    public void startOrStopTimer(Step step) {
        Step updatedStep = new Step();
        updatedStep.setStepId(step.getStepId());
        updatedStep.setFk_recipeId(step.getFk_recipeId());
        updatedStep.setInstructions(step.getInstructions());
        updatedStep.setRunning(!step.isRunning());
        updatedStep.setTimeLeft(step.getTimeLeft());
        updatedStep.setStepTime(step.getStepTime());
        repository.updateStep(updatedStep);
    }

    public void startCountDown(long recipeId, int position, int time, long stepId){
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(CountDownWorker.class)
                .setInputData(createDataForCounter(recipeId, position, time, stepId))
                .addTag("ddd")
                .build();
        workManager.enqueueUniqueWork(String.valueOf(stepId), ExistingWorkPolicy.REPLACE, request);
        updateStepState(stepId, true);
    }

    public void cancelCountDown(long stepId){
        workManager.cancelUniqueWork(String.valueOf(stepId));
        updateStepState(stepId, false);
    }

    private Data createDataForCounter(long recipeId, int position, int time, long stepId){
        Data.Builder builder = new Data.Builder();
        builder.putLong("recipeId", recipeId);
        builder.putInt("position", position);
        builder.putInt("time", time);
        builder.putLong("stepId", stepId);
        return builder.build();
    }

    public void updateStepState(long stepId, boolean status) {
        repository.updateStepState(stepId, status);
    }

    public void pruneWork(){
        workManager.pruneWork();
    }
}
