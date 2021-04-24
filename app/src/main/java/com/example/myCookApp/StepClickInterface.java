package com.example.myCookApp;

import android.widget.EditText;

import com.example.myCookApp.models.Image;
import com.example.myCookApp.models.Step;
import com.example.myCookApp.models.StepWithImages;

public interface StepClickInterface {
    void onImageItemClick(String uri);
    void onTogglerSwitch(Step step);
    void getTime(int hours, int minutes, Step step);
    void dialogSelection(int id, int requestCode);
    void addNewImageClick(long stepId);
    void onDeleteImage(Image image);
    void onTextChange(Step step, String s);
}
