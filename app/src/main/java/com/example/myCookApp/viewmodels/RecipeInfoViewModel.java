package com.example.myCookApp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myCookApp.Repository;
import com.example.myCookApp.models.IngredientWithQuantity;
import com.example.myCookApp.models.RecipeIngredient;
import com.example.myCookApp.models.RecipeWithStepsAndImages;
import com.example.myCookApp.models.StepWithImages;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

public class RecipeInfoViewModel extends ViewModel  {
    private Repository repository;
    private final Flowable<RecipeWithStepsAndImages> recipeWithStepsAndImages;
    private final Flowable<List<IngredientWithQuantity>> ingredientsWithQuantities;


    public RecipeInfoViewModel(Application application, long recipeId) {
        repository = new Repository(application);
        recipeWithStepsAndImages = repository.getRecipeWithStepsAndImagesFlowable(recipeId);
        ingredientsWithQuantities = repository.getIngredientsWithQuantitiesFlowable(recipeId);
    }

    /*public Flowable<RecipeWithStepsAndImages> getRecipeWithStepsAndImages() {
        return recipeWithStepsAndImages;
    }*/

    public Flowable<List<IngredientWithQuantity>> getIngredientsWithQuantities() {
        return ingredientsWithQuantities;
    }

    public Flowable<String> getTitle(){
        return recipeWithStepsAndImages.map(item -> item.getRecipe().getTitle());
    }

    public Flowable<List<StepWithImages>> getStepsWithImages(){
        return recipeWithStepsAndImages.map(item -> item.getStepsWithImages());
    }

}
