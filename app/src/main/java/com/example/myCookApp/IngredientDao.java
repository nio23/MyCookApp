package com.example.myCookApp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myCookApp.models.Ingredient;
import com.example.myCookApp.models.IngredientWithQuantity;

import java.util.ArrayList;
import java.util.List;


@Dao
public interface IngredientDao {

    @Insert
    void insert(Ingredient ingredient);

    @Insert
    long[] insert(List<Ingredient> ingredients);

    @Query("SELECT * FROM Ingredient WHERE name LIKE :ingredient")
    LiveData<List<Ingredient>> getIngredientsLike(String ingredient);

}
