package com.example.myCookApp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCookApp.R;
import com.example.myCookApp.adapters.IngredientsAdapter;
import com.example.myCookApp.dialogs.IngredientsQuantityDialog;
import com.example.myCookApp.dialogs.NewIngredientDialog;
import com.example.myCookApp.models.Ingredient;
import com.example.myCookApp.models.IngredientWithQuantity;
import com.example.myCookApp.viewmodels.SelectIngredientsViewModel;
import com.example.myCookApp.viewmodels.ViewModelFactory;


import java.util.ArrayList;
import java.util.List;

public class SelectIngredientsActivity extends AppCompatActivity implements IngredientsQuantityDialog.IngredientsQuantityDialogListener, NewIngredientDialog.NewIngredientDialogListener {
    public static final String EXTRA_RECIPE_ID = "com.example.myCookApp.activities.SelectIngredientsActivity.EXTRA_RECIPE_ID";
    public static final String SELECTED_INGREDIENTS_WITH_QUANTITIES = "com.example.myCookApp.activities.SelectIngredientsActivity.SELECTED_INGREDIENTS_WITH_QUANTITIES";
    private RecyclerView recyclerView;
    private Button quantityBtn;
    private long recipeId;
    private SelectIngredientsViewModel viewModel;
    private IngredientsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);


        Intent intent = getIntent();
        if (intent.getExtras()!= null) {
            recipeId = intent.getExtras().getLong(EXTRA_RECIPE_ID);
        }


        quantityBtn = findViewById(R.id.quantityBtn);
        quantityBtn.setOnClickListener(quantityBtnListener);

        recyclerView = findViewById(R.id.ingrRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication(), recipeId)).get(SelectIngredientsViewModel.class);
        adapter = new IngredientsAdapter(viewModel.getRecipeIngredients());
        recyclerView.setAdapter(adapter);


        viewModel.getIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(List<Ingredient> ingredients) {
                adapter.submitList(ingredients);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ingredient_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_item:
                searchView(item);
                return true;
            case R.id.add_item:
                newIngredientDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void searchView(MenuItem item) {
        final SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setSearchInput(newText+"%");
                return false;
            }
        });
    }

    public View.OnClickListener quantityBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<Long> selectedIngredients = adapter.getSelectedIngredients();
            if (selectedIngredients.isEmpty())
                Toast.makeText(getApplicationContext(), "Επέλεξε τα υλικά της συνταγής", Toast.LENGTH_LONG).show();
            else {
                List<IngredientWithQuantity> selectedIngredientsWithQuantities = viewModel.getSelectedIngredientsWithQuantities(selectedIngredients);
                ingredientsQuantityDialog((ArrayList) selectedIngredientsWithQuantities);
            }

        }
    };


    public void ingredientsQuantityDialog(ArrayList<IngredientWithQuantity> ingredientsSelected) {
        IngredientsQuantityDialog dialog = IngredientsQuantityDialog.getInstance(ingredientsSelected);
        dialog.show(getSupportFragmentManager(), IngredientsQuantityDialog.TAG);
    }


    public void newIngredientDialog() {
        NewIngredientDialog dialog = NewIngredientDialog.getInstance();
        dialog.show(getSupportFragmentManager(), NewIngredientDialog.TAG);

    }

    public void toastIngredientsSelected(ArrayList<IngredientWithQuantity> ingredients) {
        Log.d("SelectedIng",""+ingredients.toString());
    }



    @Override
    public void getIngredients(ArrayList<IngredientWithQuantity> ingredients) {
        //toastIngredientsSelected(ingredients);
        Intent resultIntent = new Intent();
        resultIntent.putExtra(SELECTED_INGREDIENTS_WITH_QUANTITIES, ingredients);
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    @Override
    public void getNewIngredient(String ingredient) {
        viewModel.addNewIngredient(ingredient);
    }

}
