package com.example.myCookApp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myCookApp.models.Step;
import com.example.myCookApp.models.StepWithImages;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface StepDao {

    @Insert
    void insert(Step step);

    @Insert(onConflict = REPLACE)
    List<Long> insert(List<Step> steps);

    @Update
    void update(Step step);

    @Delete
    void delete(Step step);


    @Transaction
    @Query("SELECT * FROM Step WHERE fk_recipeId = :id")
    LiveData<List<StepWithImages>> getLiveDataStepsWithImagesByRecipeId(long id);

    @Query("UPDATE Step SET isRunning =:status WHERE stepId =:stepId")
    void updateStepState(long stepId, boolean status);
}
