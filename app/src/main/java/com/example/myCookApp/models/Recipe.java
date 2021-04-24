package com.example.myCookApp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    private long recipeId;

    private String title;
    private String creator;
    private String mainPhoto;

    public Recipe(String title, String creator, String mainPhoto) {
        this.title = title;
        this.creator = creator;
        this.mainPhoto = mainPhoto;
    }

    @Ignore
    public Recipe(String title, String creator) {
        this.title = title;
        this.creator = creator;
    }

    @Ignore
    public Recipe() {
        title ="";
        creator = "";
        mainPhoto ="";
    }

    @Ignore
    public Recipe(long recipeId, String title, String creator, String mainPhoto) {
        this.recipeId = recipeId;
        this.title = title;
        this.creator = creator;
        this.mainPhoto = mainPhoto;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(String mainPhoto) {
        this.mainPhoto = mainPhoto;
    }



    @Override
    public String toString() {
        return "Recipe{" +
                "recipeId=" + recipeId +
                ", title='" + title + '\'' +
                ", creator='" + creator + '\'' +
                ", mainPhoto='" + mainPhoto + '\'' +
                '}';
    }
}
