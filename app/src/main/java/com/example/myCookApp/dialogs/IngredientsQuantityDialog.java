package com.example.myCookApp.dialogs;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCookApp.R;
import com.example.myCookApp.adapters.IngredientsQuantityDialogAdapter;
import com.example.myCookApp.models.IngredientWithQuantity;

import java.util.ArrayList;

public class IngredientsQuantityDialog extends DialogFragment {
    public static String TAG = "IngredientsQuantityDialog";
    private static final String ARG_SELECTED_INGREDIENTS = "com.example.myCookApp.dialogs.SELECTED_INGREDIENTS";
    private IngredientsQuantityDialogListener listener;
    private RecyclerView recyclerView;
    private Button okBtn, cancelBtn;
    private IngredientsQuantityDialogAdapter adapter;

    public interface IngredientsQuantityDialogListener {
        void getIngredients(ArrayList<IngredientWithQuantity> ingredients);
    }

    public static IngredientsQuantityDialog getInstance(ArrayList<IngredientWithQuantity> ingredientsSelected) {
        IngredientsQuantityDialog fragment = new IngredientsQuantityDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_SELECTED_INGREDIENTS, ingredientsSelected);
        fragment.setArguments(args);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_ingredient_quantity, container, false);
        recyclerView = v.findViewById(R.id.quantityRecyclerView);
        okBtn = v.findViewById(R.id.quantityOkBtn);
        okBtn.setOnClickListener(okBtnListener);
        cancelBtn = v.findViewById(R.id.quantityCancelBtn);
        cancelBtn.setOnClickListener(cancelBtnListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            adapter = new IngredientsQuantityDialogAdapter(getArguments().getParcelableArrayList(ARG_SELECTED_INGREDIENTS));
        }

        recyclerView.setAdapter(adapter);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return v;
    }



    private View.OnClickListener okBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean emptyQuantity=false ;
            ArrayList<IngredientWithQuantity> ingredientsWithQuantity = adapter.getIngredientsSelectedList();

            for (IngredientWithQuantity ingredient : ingredientsWithQuantity) {
                if (ingredient.getQuantity() == null || ingredient.getQuantity().trim().isEmpty() ) {
                    emptyQuantity = true;
                    break;
                }
            }

            if (emptyQuantity) {
                Toast.makeText(getActivity(), "Συμπληρώστε τις ποσότητες", Toast.LENGTH_SHORT).show();
            } else {
                listener.getIngredients(ingredientsWithQuantity);
                dismiss();
            }

        }
    };

    private View.OnClickListener cancelBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (IngredientsQuantityDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IngredientsQuantityDialogListener");
        }
    }


}
