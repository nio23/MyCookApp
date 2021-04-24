package com.example.myCookApp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCookApp.R;
import com.example.myCookApp.models.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngredientsAdapter extends ListAdapter<Ingredient, IngredientsAdapter.ViewHolder>  {

    //private HashMap<Long, Ingredient> mapCheckBoxes = new HashMap<>();
    private ArrayList<Long> selectedIngredients = new ArrayList<>();


    public IngredientsAdapter(ArrayList<Long> recipeIngredients) {
        super(diffCallback);
        this.selectedIngredients = recipeIngredients;
    }

    private static final DiffUtil.ItemCallback<Ingredient> diffCallback = new DiffUtil.ItemCallback<Ingredient>() {
        @Override
        public boolean areItemsTheSame(@NonNull Ingredient oldItem, @NonNull Ingredient newItem) {
            return oldItem.getIngredientId() == newItem.getIngredientId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Ingredient oldItem, @NonNull Ingredient newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredientview_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient currentIngredient = getItem(position);
        holder.ingrTextView.setText(currentIngredient.getName());
        holder.checkBox.setChecked(selectedIngredients.contains(currentIngredient.getIngredientId()));
        /*if (mapCheckBoxes.containsKey(currentIngredient.getIngredientId()))
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);*/
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ingrTextView;
        private CheckBox checkBox;
        private LinearLayout ingrItemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ingrTextView = itemView.findViewById(R.id.ingrTextView);
            checkBox = itemView.findViewById(R.id.checkbox);
            ingrItemView = itemView.findViewById(R.id.ingrItemView);

            ingrItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (!checkBox.isChecked()) {
                            checkBox.setChecked(true);
                            selectedIngredients.add(getItem(position).getIngredientId());
                            //mapCheckBoxes.put(getItem(position).getIngredientId(), getItem(position));
                        } else {
                            checkBox.setChecked(false);
                            selectedIngredients.remove(getItem(position).getIngredientId());
                            //mapCheckBoxes.remove(getItem(position).getIngredientId());
                        }
                    }
                }
            });
        }
    }

    public ArrayList<Long> getSelectedIngredients() {
        return selectedIngredients;
    }

    /*public ArrayList<Ingredient> getCheckedIngredients() {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for (Map.Entry<Long, Ingredient> item: mapCheckBoxes.entrySet()) {
            ingredients.add(item.getValue());
        }
        return ingredients;
    }*/
}
