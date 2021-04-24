package com.example.myCookApp;

import com.example.myCookApp.models.IngredientWithQuantity;

import java.util.List;

public class Util {

    public StringBuilder transformIngredients(List<IngredientWithQuantity> ingredientsWithQuantities){
            StringBuilder stringBuilder = new StringBuilder();
            for (IngredientWithQuantity item : ingredientsWithQuantities) {
                stringBuilder.append(item.getQuantity()).append(" ").append(item.getIngredient().getName()).append(System.lineSeparator());
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder;
    }
}
