package com.example.myCookApp.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.myCookApp.models.Recipe;
import com.example.myCookApp.models.Step;
import com.example.myCookApp.models.StepWithImages;

import java.util.List;

public class RecipeWithStepsAndImages {
    @Embedded private Recipe recipe ;
    @Relation(parentColumn = "recipeId", entityColumn = "fk_recipeId", entity = Step.class)
    private List<StepWithImages> stepsWithImages;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public List<StepWithImages> getStepsWithImages() {
        return stepsWithImages;
    }

    public void setStepsWithImages(List<StepWithImages> stepsWithImages) {
        this.stepsWithImages = stepsWithImages;
    }

    public RecipeWithStepsAndImages(Recipe recipe, List<StepWithImages> stepsWithImages) {
        this.recipe = recipe;
        this.stepsWithImages = stepsWithImages;
    }

    @Override
    public String toString() {
        return "RecipeWithStepsAndImages{" +
                "recipe=" + recipe +
                ", stepsWithImages=" + stepsWithImages +
                '}';
    }



}
