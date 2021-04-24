package com.example.myCookApp.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

@Entity(primaryKeys = {"fk_recipeId", "fk_ingredientId"}, foreignKeys = { @ForeignKey(entity = Recipe.class, parentColumns = "recipeId", childColumns = "fk_recipeId", onDelete = 5),
        @ForeignKey(entity = Ingredient.class, parentColumns = "ingredientId", childColumns = "fk_ingredientId", onDelete = 5)})
public class RecipeIngredient {
    private long fk_recipeId;
    private long fk_ingredientId;
    private String quantity;

    public RecipeIngredient(long fk_recipeId, long fk_ingredientId, String quantity) {
        this.fk_recipeId = fk_recipeId;
        this.fk_ingredientId = fk_ingredientId;
        this.quantity = quantity;
    }

    @Ignore
    public RecipeIngredient() {
    }

    public long getFk_recipeId() {
        return fk_recipeId;
    }

    public void setFk_recipeId(long fk_recipeId) {
        this.fk_recipeId = fk_recipeId;
    }

    public long getFk_ingredientId() {
        return fk_ingredientId;
    }

    public void setFk_ingredientId(long fk_ingredientId) {
        this.fk_ingredientId = fk_ingredientId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "RecipeIngredient{" +
                "fk_recipeId=" + fk_recipeId +
                ", fk_ingredientId=" + fk_ingredientId +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
