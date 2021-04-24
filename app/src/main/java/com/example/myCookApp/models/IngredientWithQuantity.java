package com.example.myCookApp.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class IngredientWithQuantity implements Parcelable {

    @Embedded
    private Ingredient ingredient;
    private String quantity;

    public IngredientWithQuantity(Ingredient ingredient, String quantity) {
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    @Ignore
    public IngredientWithQuantity(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Ignore
    protected IngredientWithQuantity(Parcel in) {
        ingredient = in.readParcelable(Ingredient.class.getClassLoader());
        quantity = in.readString();
    }

    public static final Creator<IngredientWithQuantity> CREATOR = new Creator<IngredientWithQuantity>() {
        @Override
        public IngredientWithQuantity createFromParcel(Parcel in) {
            return new IngredientWithQuantity(in);
        }

        @Override
        public IngredientWithQuantity[] newArray(int size) {
            return new IngredientWithQuantity[size];
        }
    };

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "IngredientWithQuantity{" +
                "ingredient=" + ingredient +
                ", quantity='" + quantity + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(ingredient, flags);
        dest.writeString(quantity);
    }
}
