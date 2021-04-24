package com.example.myCookApp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myCookApp.models.Recipe;
import com.example.myCookApp.models.RecipeWithStepsAndImages;
import com.example.myCookApp.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivityViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Recipe>> recipes;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        recipes = repository.getAllRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes(){
        return recipes;
    }

    public void update(Recipe recipe){
        repository.update(recipe);
    }

    public long createNewRecipe(){
        return repository.createNewRecipe();
    }

    public void deleteRecipe(long recipeId) {
        repository.deleteRecipe(recipeId);
    }
}
