package com.example.myCookApp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;


public class NewIngredientDialog extends AppCompatDialogFragment {
    public static String TAG ="NewIngredientDialog";
    private NewIngredientDialogListener listener;

    public interface NewIngredientDialogListener{
        void getNewIngredient(String ingredient);
    }

    public static NewIngredientDialog getInstance(){
        return new NewIngredientDialog();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        FrameLayout layout = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(100,0,100,0);
        layout.addView(input,params);
        builder.setTitle("Νέο υλικό")
                .setView(layout)
                .setNegativeButton("Ακύρωση", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Προσθήκη", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String inputText = input.getText().toString().trim();
                        if (inputText.isEmpty()){
                            Toast.makeText(getContext(),"Πληκτρολόγησε υλικό", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            listener.getNewIngredient(capitalize(inputText));
                            dismiss();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);
        return dialog;

    }

    private String capitalize(String inputText){
        return Character.toUpperCase(inputText.charAt(0)) + inputText.substring(1);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (NewIngredientDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() +" must implement NewIngredientDialogListener");
        }
    }
}
