package com.example.myCookApp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myCookApp.models.IngredientWithQuantity;
import com.example.myCookApp.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipeIngredientDao {

    @Insert
    void insert(RecipeIngredient recipeIngredient);

    @Insert(onConflict = REPLACE)
    void insert(List<RecipeIngredient> recipeIngredients);

    @Query("SELECT * FROM Ingredient INNER JOIN recipeingredient ON recipeingredient.fk_ingredientId = Ingredient.ingredientId WHERE recipeingredient.fk_recipeId = :recipeId")
    List<IngredientWithQuantity> getIngredientsWithQuantityList(long recipeId);

    @Query("SELECT r.fk_recipeId as recipeId, ingredientId, r.quantity , name " +
            "FROM Ingredient i LEFT JOIN RecipeIngredient r ON r.fk_ingredientId = i.ingredientId AND r.fk_recipeId = :recipeId " +
            "WHERE i.ingredientId IN (:selectedIngredients)")
    List<IngredientWithQuantity> getSelectedIngredientsWithQuantities(ArrayList<Long> selectedIngredients, long recipeId);

    @Query("SELECT fk_ingredientId as ingredientId FROM Recipeingredient WHERE fk_recipeId = :recipeId")
    List<Long> getRecipeIngredients(long recipeId);

    @Query("SELECT * FROM Ingredient INNER JOIN recipeingredient ON recipeingredient.fk_ingredientId = Ingredient.ingredientId WHERE recipeingredient.fk_recipeId = :recipeId")
    Flowable<List<IngredientWithQuantity>> getIngredientsWithQuantitiesFlowable(long recipeId);

}
