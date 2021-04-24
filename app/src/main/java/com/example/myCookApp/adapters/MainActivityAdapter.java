package com.example.myCookApp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCookApp.R;
import com.example.myCookApp.models.Recipe;
import com.example.myCookApp.models.RecipeWithStepsAndImages;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {
    private List<Recipe> recipes = new ArrayList<>();
    private OnItemClickListener listener;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipeview_item, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe currentRecipe = recipes.get(position);

        holder.mTextViewCreator.setText(currentRecipe.getCreator());
        holder.mTextViewTitle.setText(currentRecipe.getTitle());
        if (currentRecipe.getMainPhoto() != null && !currentRecipe.getMainPhoto().trim().isEmpty()) {
            Picasso.get().load(currentRecipe.getMainPhoto()).fit().into(holder.mImageView);

        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextViewCreator;
        public TextView mTextViewTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextViewTitle = itemView.findViewById(R.id.titleTextView);
            mTextViewCreator = itemView.findViewById(R.id.creatorNameTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(recipes.get(position));
                    }
                }
            });
        }

    }

    public void setRecipes (List<Recipe> recipes){
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(Recipe recipe);
    }

    public void  setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
