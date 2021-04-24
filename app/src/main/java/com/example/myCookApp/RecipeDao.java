package com.example.myCookApp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myCookApp.models.Recipe;
import com.example.myCookApp.models.RecipeWithStepsAndImages;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipeDao {

    @Insert(onConflict = REPLACE)
    void insert(Recipe recipe);

    @Insert
    long insertAndGetId(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Delete
    void deleteRecipe(Recipe recipe);

    @Query("SELECT * FROM Recipe")
    LiveData<List<Recipe>> getAllRecipes();

    @Transaction
    @Query("SELECT * FROM recipe")
    LiveData<List<RecipeWithStepsAndImages>> getRecipesWithStepsAndImages();


    @Transaction
    @Query("SELECT * FROM recipe WHERE recipeId = :id")
    LiveData<RecipeWithStepsAndImages> getRecipeWithStepAndImagesLiveData(long id);

    @Query("UPDATE recipe SET mainPhoto = :mainPhoto WHERE recipeId =:recipeId")
    void updateMainPhoto(String mainPhoto, long recipeId);


    @Query("DELETE FROM Recipe WHERE recipeId=:recipeId")
    void delete(long recipeId);


    @Query("SELECT * FROM Recipe WHERE recipeId =:recipeId")
    Flowable<RecipeWithStepsAndImages> getRecipeWithStepsAndImagesFlowable(long recipeId);

    @Query("SELECT * FROM Recipe WHERE recipeId =:recipeId")
    List<RecipeWithStepsAndImages> getRecipeWithStepsAndImages(long recipeId);

}
