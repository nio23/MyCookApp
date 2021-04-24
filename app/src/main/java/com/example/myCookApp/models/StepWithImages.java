package com.example.myCookApp.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class StepWithImages {
    @Embedded private Step step;
    @Relation(parentColumn = "stepId", entityColumn = "stepParentId", entity = Image.class)
    private List<Image> images;


    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public StepWithImages(Step step, List<Image> images) {
        this.step = step;
        this.images = images;
    }

    @Override
    public String toString() {
        return "StepWithImages{" +
                "step=" + step +
                ", images=" + images +
                '}';
    }
}
