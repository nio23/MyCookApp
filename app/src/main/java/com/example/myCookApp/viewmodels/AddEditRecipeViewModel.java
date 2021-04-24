package com.example.myCookApp.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.myCookApp.models.Image;
import com.example.myCookApp.Repository;
import com.example.myCookApp.models.IngredientWithQuantity;
import com.example.myCookApp.models.Recipe;
import com.example.myCookApp.models.RecipeIngredient;
import com.example.myCookApp.models.RecipeWithStepsAndImages;
import com.example.myCookApp.models.Step;
import com.example.myCookApp.models.StepWithImages;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;


public class AddEditRecipeViewModel extends ViewModel {
    private final Repository repository;
    private final long recipeId;
    private MutableLiveData<List<IngredientWithQuantity>> ingredientsWithQuantity;
    private final Flowable<RecipeWithStepsAndImages> recipeWithStepsAndImagesFlowable;
    private RecipeWithStepsAndImages backUpRecipeWithStepsAndImages;
    private final List<IngredientWithQuantity> backupIngredientsWithQuantity;


    public AddEditRecipeViewModel(@NonNull Application application, long recipeId) {
        repository = new Repository(application);
        if (recipeId == -1){
            this.recipeId = repository.createNewRecipe();
        }else{
            this.recipeId = recipeId;
        }


        if (ingredientsWithQuantity == null){
            ingredientsWithQuantity = new MutableLiveData<>();
        }
        ingredientsWithQuantity.setValue(repository.getIngredientsWithQuantities(this.recipeId));
        recipeWithStepsAndImagesFlowable = repository.getRecipeWithStepsAndImagesFlowable(this.recipeId);
        backupIngredientsWithQuantity = ingredientsWithQuantity.getValue();
    }

    public void setBackUpRecipeWithStepsAndImages(RecipeWithStepsAndImages backUpRecipeWithStepsAndImages) {
        this.backUpRecipeWithStepsAndImages = backUpRecipeWithStepsAndImages;
    }

    public LiveData<List<IngredientWithQuantity>> getIngredientsWithQuantity() {
        return ingredientsWithQuantity;
    }

    public void setIngredientsWithQuantity(List<IngredientWithQuantity> ingredientsWithQuantity) {
        this.ingredientsWithQuantity.setValue(ingredientsWithQuantity);
    }

    public long getRecipeId() {
        return recipeId;
    }



    public Flowable<RecipeWithStepsAndImages> getRecipeWithStepsAndImagesFlowable() {
        return recipeWithStepsAndImagesFlowable;
    }

    public Flowable<List<StepWithImages>> getStepsWithImages(){
        return recipeWithStepsAndImagesFlowable.map(recipeWithStepsAndImages -> recipeWithStepsAndImages.getStepsWithImages());
    }

    public Flowable<String> getCreator(){
        return recipeWithStepsAndImagesFlowable.map( recipeWithStepsAndImages -> recipeWithStepsAndImages.getRecipe().getCreator());
    }

    public Flowable<String> getTitle(){
        return recipeWithStepsAndImagesFlowable.map(recipeWithStepsAndImages -> recipeWithStepsAndImages.getRecipe().getTitle());
    }

    public Flowable<String> getMainPhoto(){
        return recipeWithStepsAndImagesFlowable.map(recipeWithStepsAndImages -> recipeWithStepsAndImages.getRecipe().getMainPhoto());
    }


    public void restoreRecipe(){
        Recipe recipe = backUpRecipeWithStepsAndImages.getRecipe();
        List<Step> steps = new ArrayList<>();
        List<Image> images = new ArrayList<>();
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();


        for (StepWithImages item: backUpRecipeWithStepsAndImages.getStepsWithImages()) {
            if (item.getStep().getInstructions() != null && !item.getStep().getInstructions().isEmpty() )
                steps.add(item.getStep());
            if (item.getImages() != null && !item.getImages().isEmpty())
                images.addAll(item.getImages());
        }

        if (backupIngredientsWithQuantity != null && !backupIngredientsWithQuantity.isEmpty()) {
            for (IngredientWithQuantity item : backupIngredientsWithQuantity) {
                recipeIngredients.add(new RecipeIngredient(recipeId, item.getIngredient().getIngredientId(), item.getQuantity()));
            }
        }

        repository.updateRecipe(recipe, recipeIngredients, steps, images);

    }

    public void addNewImage(long fk_stepId, String uri){
       Image image = new Image();
       image.setStepParentId(fk_stepId);
       image.setUri(uri);
       repository.insert(image);
    }

    public void addNewStep(){
        Step step = new Step();
        step.setFk_recipeId(recipeId);
        step.setInstructions("");
        repository.insert(step);
    }

    public void deleteImage(Image image) {
        repository.deleteImage(image);
    }


    public void updateStep(Step step) {
        repository.updateStep(step);
    }


    public void setMainPhoto(String mainPhoto) {
        repository.updateMainPhoto(mainPhoto, recipeId);
    }



    public void deleteStepWithImages(StepWithImages stepWithImages) {
        if (stepWithImages.getImages()!=null && !stepWithImages.getImages().isEmpty())
            repository.deleteImage(stepWithImages.getImages());
        repository.deleteStep(stepWithImages.getStep());
    }

    public void restoreStepWithImages(StepWithImages deletedStepWithImages) {
        repository.insert(deletedStepWithImages.getStep());
        if (!deletedStepWithImages.getImages().isEmpty())
        repository.insert(deletedStepWithImages.getImages());
    }


    public void updateRecipe(String title, String creator, String mainPhoto, List<StepWithImages> currentList) {

                Recipe recipe = new Recipe(recipeId, title, creator, mainPhoto);
                List<Step> steps = new ArrayList<>();
                List<Image> images = new ArrayList<>();
                List<RecipeIngredient> ingredients = new ArrayList<>();

                if (ingredientsWithQuantity.getValue()!=null && !ingredientsWithQuantity.getValue().isEmpty())
                for (IngredientWithQuantity item : ingredientsWithQuantity.getValue()){
                    ingredients.add(new RecipeIngredient(recipeId, item.getIngredient().getIngredientId(), item.getQuantity()));
                }

                for (StepWithImages item: currentList) {
                    if (item.getStep().getInstructions().trim().isEmpty() && item.getImages().isEmpty()) {
                        repository.deleteStep(item.getStep());
                    }else{
                        steps.add(item.getStep());
                        if (!item.getImages().isEmpty()) {
                            images.addAll(item.getImages());
                        }
                    }
                }
                repository.updateRecipe(recipe, ingredients, steps, images);

    }


    public void deleteRecipe() {
        repository.deleteRecipe(recipeId);
    }
}
