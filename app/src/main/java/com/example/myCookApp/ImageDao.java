package com.example.myCookApp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.example.myCookApp.models.Image;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ImageDao {

    @Insert
    void insert(Image image);

    @Insert(onConflict = REPLACE)
    void insert(List<Image> images);

    @Delete
    void deleteImageById(Image image);

    @Delete
    void delete(List<Image> images);
}
