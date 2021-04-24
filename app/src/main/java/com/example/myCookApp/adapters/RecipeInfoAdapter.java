package com.example.myCookApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCookApp.R;
import com.example.myCookApp.models.StepWithImages;

public class RecipeInfoAdapter extends ListAdapter<StepWithImages, RecipeInfoAdapter.ViewHolder> {
    private Context context;



    public RecipeInfoAdapter() {
        super(diffCallback);
    }

    private static final DiffUtil.ItemCallback<StepWithImages> diffCallback = new DiffUtil.ItemCallback<StepWithImages>() {
        @Override
        public boolean areItemsTheSame(@NonNull StepWithImages oldItem, @NonNull StepWithImages newItem) {
            return oldItem.getStep().getStepId() ==  newItem.getStep().getStepId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull StepWithImages oldItem, @NonNull StepWithImages newItem) {
            return  oldItem.getStep().getInstructions().equals(newItem.getStep().getInstructions());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_info_item, parent,false);
        return new ViewHolder(itemView);
    }



    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StepWithImages currentStep = getItem(position);

        holder.instructions.setText(currentStep.getStep().getInstructions());



            ImagesAdapter adapter = new ImagesAdapter(false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            holder.imagesRV.setLayoutManager(linearLayoutManager);
            holder.imagesRV.setHasFixedSize(true);
            holder.imagesRV.setAdapter(adapter);
            adapter.submitList(currentStep.getImages());

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView instructions;
        RecyclerView imagesRV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            instructions = itemView.findViewById(R.id.instructionsTV);
            imagesRV = itemView.findViewById(R.id.imagesInfoRV);
        }
    }




}
