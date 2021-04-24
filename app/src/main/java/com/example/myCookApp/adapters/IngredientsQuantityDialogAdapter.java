package com.example.myCookApp.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCookApp.R;
import com.example.myCookApp.models.IngredientWithQuantity;

import java.util.ArrayList;

public class IngredientsQuantityDialogAdapter extends RecyclerView.Adapter<IngredientsQuantityDialogAdapter.IngredientViewHolder> {
    private ArrayList<IngredientWithQuantity> ingredientsSelectedList;

    public IngredientsQuantityDialogAdapter(ArrayList<IngredientWithQuantity> ingredientsSelectedList) {
        this.ingredientsSelectedList = ingredientsSelectedList;
    }


    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_ingredient_quantity_view_item, parent,false);
        return new IngredientViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, final int position) {
        holder.ingredientTextView.setText(ingredientsSelectedList.get(position).getIngredient().getName());
        holder.quantityEditText.setText(ingredientsSelectedList.get(position).getQuantity());
    }

    @Override
    public int getItemCount() {
        return ingredientsSelectedList.size();
    }



    class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView ingredientTextView;
        private EditText quantityEditText;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientTextView = itemView.findViewById(R.id.chosenIngrTV);
            quantityEditText = itemView.findViewById(R.id.quantityET);
            quantityEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ingredientsSelectedList.get(getAdapterPosition()).setQuantity(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    public ArrayList<IngredientWithQuantity> getIngredientsSelectedList() {
        return ingredientsSelectedList;
    }
}
