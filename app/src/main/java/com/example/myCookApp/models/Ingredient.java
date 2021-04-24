package com.example.myCookApp.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Ingredient implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long ingredientId;

    private String name;

    public Ingredient(String name) {
        this.name = name;
    }

    protected Ingredient(Parcel in) {
        ingredientId = in.readLong();
        name = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public long getIngredientId() {
        return ingredientId;
    }

    public String getName() {
        return name;
    }

    public void setIngredientId(long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public void setName(String name) {
        this.name = name;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ingredientId);
        dest.writeString(name);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "ingredientId=" + ingredientId +
                ", name='" + name + '\'' +
                '}';
    }
}
