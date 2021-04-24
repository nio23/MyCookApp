package com.example.myCookApp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCookApp.R;
import com.example.myCookApp.models.Image;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ExecuteRecipeImagesViewPagerAdapter extends RecyclerView.Adapter<ExecuteRecipeImagesViewPagerAdapter.ViewHolder> {
    private ArrayList<Image> images = new ArrayList<>();




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image currentImage = images.get(position);
        Picasso.get().load(currentImage.getUri()).fit().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);

            imageView.getLayoutParams().width = MATCH_PARENT;
            imageView.getLayoutParams().height = MATCH_PARENT;
        }
    }

    public void setImages(ArrayList<Image> images){
        this.images = images;
        notifyDataSetChanged();
    }
}
