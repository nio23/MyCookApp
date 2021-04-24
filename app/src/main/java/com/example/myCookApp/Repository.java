package com.example.myCookApp;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.myCookApp.models.Image;
import com.example.myCookApp.models.Ingredient;
import com.example.myCookApp.models.IngredientWithQuantity;
import com.example.myCookApp.models.Recipe;
import com.example.myCookApp.models.RecipeIngredient;
import com.example.myCookApp.models.RecipeWithStepsAndImages;
import com.example.myCookApp.models.Step;
import com.example.myCookApp.models.StepWithImages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;


public class Repository {
    private final RecipeDao recipeDao;
    private final StepDao stepDao;
    private final ImageDao imageDao;
    private final IngredientDao ingredientDao;
    private final RecipeIngredientDao recipeIngredientDao;

    public Repository(Application application){
        Database database = Database.getDatabase(application);
        stepDao = database.stepDao();
        imageDao = database.imageDao();
        recipeDao = database.recipeDao();
        ingredientDao = database.ingredientDao();
        recipeIngredientDao = database.recipeIngredientDao();
    }

    public LiveData<List<Recipe>> getAllRecipes(){
        return recipeDao.getAllRecipes();
    }

    public LiveData<List<Ingredient>> getIngredientsLike(String ingredient) {
        return ingredientDao.getIngredientsLike(ingredient);
    }


    public LiveData<List<StepWithImages>> getAllStepsByRecipeId(long recipeId){
        return stepDao.getLiveDataStepsWithImagesByRecipeId(recipeId);
    }




    public void updateRecipe(Recipe recipe, List<RecipeIngredient> recipeIngredients, List<Step> steps, List<Image> images){
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                recipeDao.delete(recipe.getRecipeId());
                recipeDao.insert(recipe);
                recipeIngredientDao.insert(recipeIngredients);
                stepDao.insert(steps);
                if (!images.isEmpty())
                imageDao.insert(images);
            }
        });
    }

    public void updateStep(Step step) {
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                stepDao.update(step);
            }
        });
    }


    public void insert(Step step){
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                stepDao.insert(step);
            }
        });
    }


    public void insert(Image image){
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                imageDao.insert(image);
            }
        });
    }

    public void insert(List<Image> image){
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                imageDao.insert(image);
            }
        });
    }

    public void update(Recipe recipe){
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                recipeDao.update(recipe);
            }
        });
    }

    public void deleteImage(Image image){
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                imageDao.deleteImageById(image);
            }
        });
    }

    public void deleteImage(List<Image> image){
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                imageDao.delete(image);
            }
        });
    }





    public Long createNewRecipe(){
        Recipe recipe = new Recipe();
        long id=-1;
        Future<Long> recipeId = Database.databaseWriterExecutor.submit(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return recipeDao.insertAndGetId(recipe);
            }
        });

        try {
                id = recipeId.get();
        } catch (ExecutionException e) {
            e.getCause().printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        List<Step> steps = new ArrayList<>();
        Step step1 = new Step();
        Step step2 = new Step();
        step1.setFk_recipeId(id);
        step2.setFk_recipeId(id);
        steps.add(step1);
        steps.add(step2);

        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                stepDao.insert(steps);
            }
        });

        return id;
    }



    public List<IngredientWithQuantity> getIngredientsWithQuantities(long recipeId){
        List<IngredientWithQuantity> ingredientsList = new ArrayList<>();
        Callable<List<IngredientWithQuantity>> callable = new Callable<List<IngredientWithQuantity>>() {
            @Override
            public List<IngredientWithQuantity> call() throws Exception {
                return recipeIngredientDao.getIngredientsWithQuantityList(recipeId);
            }
        };
        Future<List<IngredientWithQuantity>> future = Database.databaseWriterExecutor.submit(callable);

        try {
            ingredientsList = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ingredientsList;
    }


    public List<IngredientWithQuantity> getSelectedIngredientsWithQuantities(ArrayList<Long> ingredients, long recipeId){
        List<IngredientWithQuantity> ingredientsListWithQuantites = new ArrayList<>();
        Callable<List<IngredientWithQuantity>> callable = new Callable<List<IngredientWithQuantity>>() {
            @Override
            public List<IngredientWithQuantity> call() throws Exception {
                return recipeIngredientDao.getSelectedIngredientsWithQuantities(ingredients, recipeId);
            }
        };
        Future<List<IngredientWithQuantity>> future = Database.databaseWriterExecutor.submit(callable);

        try {
            ingredientsListWithQuantites = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ingredientsListWithQuantites;
    }

    public ArrayList<Long> getRecipeIngredients(long recipeId) {
        List<Long> ingredients = new ArrayList<>();
        Callable<List<Long>> callable = new Callable<List<Long>>() {
            @Override
            public List<Long> call() throws Exception {
                return recipeIngredientDao.getRecipeIngredients(recipeId);
            }
        };

        Future<List<Long>> future = Database.databaseWriterExecutor.submit(callable);

        try {
            ingredients = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (ArrayList) ingredients;
    }

    public List<RecipeWithStepsAndImages> getRecipeWithStepsAndImages(long recipeId) {
        List<RecipeWithStepsAndImages> recipeWithStepsAndImages = new ArrayList<>();
        Callable<List<RecipeWithStepsAndImages>> callable = new Callable<List<RecipeWithStepsAndImages>>() {
            @Override
            public List<RecipeWithStepsAndImages> call() throws Exception {
                return recipeDao.getRecipeWithStepsAndImages(recipeId);
            }
        };

        Future<List<RecipeWithStepsAndImages>> future = Database.databaseWriterExecutor.submit(callable);

        try {
            recipeWithStepsAndImages = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return recipeWithStepsAndImages;
    }




    public void updateMainPhoto(String mainPhoto, long recipeId) {
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                recipeDao.updateMainPhoto(mainPhoto, recipeId);
            }
        });
    }




    public void addNewIngredient(String ingredient) {
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ingredientDao.insert(new Ingredient(ingredient));
            }
        });
    }


    public void deleteStep(Step step) {
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                stepDao.delete(step);
            }
        });
    }


    public void deleteRecipe(long recipeId) {
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                recipeDao.delete(recipeId);
            }
        });
    }

    public Flowable<RecipeWithStepsAndImages> getRecipeWithStepsAndImagesFlowable(long recipeId) {
        return recipeDao.getRecipeWithStepsAndImagesFlowable(recipeId);
    }


    public Flowable<List<IngredientWithQuantity>> getIngredientsWithQuantitiesFlowable(long recipeId){
        return recipeIngredientDao.getIngredientsWithQuantitiesFlowable(recipeId);
    }


    public void updateStepState(long stepId, boolean status) {
        Database.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                stepDao.updateStepState(stepId, status);
            }
        });
    }
}
