package com.example.myCookApp.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Step.class, parentColumns = "stepId", childColumns ="stepParentId", onDelete = 5))
public class Image {
    @PrimaryKey(autoGenerate = true)
    private long imageId;

    private long stepParentId;

    private String uri;

    @Ignore
    public Image(long imageId, long stepParentId, String uri) {
        this.imageId = imageId;
        this.stepParentId = stepParentId;
        this.uri = uri;
    }

    public Image(long stepParentId, String uri) {
        this.stepParentId = stepParentId;
        this.uri = uri;
    }

    public Image() {
        uri="";
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public long getStepParentId() {
        return stepParentId;
    }

    public void setStepParentId(long stepParentId) {
        this.stepParentId = stepParentId;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageId=" + imageId +
                ", stepParentId=" + stepParentId +
                ", uri='" + uri + '\'' +
                '}';
    }
}
