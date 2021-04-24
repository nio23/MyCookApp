package com.example.myCookApp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.myCookApp.models.Recipe;

@Entity(foreignKeys = @ForeignKey(entity = Recipe.class, parentColumns = "recipeId", childColumns = "fk_recipeId", onDelete = 5))
public class Step {
    @PrimaryKey(autoGenerate = true)
    private long stepId ;

    private long fk_recipeId;

    private String instructions;
    private int stepTime;
    private int timeLeft;
    private boolean isRunning;



    public Step(long stepId, long fk_recipeId, String instructions, int stepTime) {
        this.stepId = stepId;
        this.fk_recipeId = fk_recipeId;
        this.instructions = instructions;
        this.stepTime = stepTime;
    }

    @Ignore
    public Step() {
        instructions ="";
    }

    @Ignore
    public Step(long fk_recipeId, String instructions) {
        this.fk_recipeId = fk_recipeId;
        this.instructions = instructions;
    }

    @Ignore
    public Step(long fk_recipeId, String instructions, int stepTime) {
        this.fk_recipeId = fk_recipeId;
        this.instructions = instructions;
        this.stepTime = stepTime;
    }

    public long getStepId() {
        return stepId;
    }

    public void setStepId(long stepId) {
        this.stepId = stepId;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getStepTime() {
        return stepTime;
    }

    public void setStepTime(int stepTime) {
        this.stepTime = stepTime;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public long getFk_recipeId() {
        return fk_recipeId;
    }

    public void setFk_recipeId(long fk_recipeId) {
        this.fk_recipeId = fk_recipeId;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public String toString() {
        return "Step{" +
                "stepId=" + stepId +
                ", fk_recipedId=" + fk_recipeId +
                ", instructions='" + instructions + '\'' +
                ", stepTime=" + stepTime +
                ", timeLeft=" + timeLeft +
                '}';
    }
}
