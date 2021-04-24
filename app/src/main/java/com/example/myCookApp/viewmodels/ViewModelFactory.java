package com.example.myCookApp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myCookApp.activities.ExecuteRecipeActivity;


public class ViewModelFactory implements ViewModelProvider.Factory {

    private long recipeId;
    private Application application;

    public ViewModelFactory(Application application, long recipeId) {
        this.recipeId = recipeId;
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecipeInfoViewModel.class))
            return (T) new RecipeInfoViewModel(application, recipeId);
        else if (modelClass.isAssignableFrom(AddEditRecipeViewModel.class))
            return (T) new AddEditRecipeViewModel(application, recipeId);
        else if (modelClass.isAssignableFrom(SelectIngredientsViewModel.class))
            return (T) new SelectIngredientsViewModel(application, recipeId);
        else if (modelClass.isAssignableFrom(ExecuteRecipeViewModel.class))
            return (T) new ExecuteRecipeViewModel(application, recipeId);
        else
            return null;
    }

}
