package com.example.myCookApp.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myCookApp.Repository;
import com.example.myCookApp.models.Ingredient;
import com.example.myCookApp.models.IngredientWithQuantity;

import java.util.ArrayList;
import java.util.List;

public class SelectIngredientsViewModel extends ViewModel {
    private Repository repository;
    private long recipeId;
    private ArrayList<Long> recipeIngredients;
    private MutableLiveData<String> searchInput = new MutableLiveData<>();
    private LiveData<List<Ingredient>> ingredients = Transformations.switchMap(searchInput, ingredient ->{
        return repository.getIngredientsLike(ingredient);
    });


    public SelectIngredientsViewModel(Application application, long recipeId) {
        repository = new Repository(application);
        this.recipeId = recipeId;
        searchInput.setValue("%");
        recipeIngredients = repository.getRecipeIngredients(recipeId);
    }


    public List<IngredientWithQuantity> getSelectedIngredientsWithQuantities(ArrayList<Long> selectedIngredients){
        return repository.getSelectedIngredientsWithQuantities(selectedIngredients, recipeId);
    }


    public void setSearchInput(String ingredient) {
        searchInput.setValue(ingredient);
    }

    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }

    public void addNewIngredient(String ingredient) {
        repository.addNewIngredient(ingredient);
    }

    public ArrayList<Long> getRecipeIngredients() {
        return recipeIngredients;
    }

}
