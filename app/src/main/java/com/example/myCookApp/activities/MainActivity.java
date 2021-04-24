package com.example.myCookApp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myCookApp.adapters.MainActivityAdapter;
import com.example.myCookApp.R;
import com.example.myCookApp.models.Recipe;
import com.example.myCookApp.models.RecipeWithStepsAndImages;
import com.example.myCookApp.viewmodels.MainActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int CREATE_RECIPE_REQUEST = 1;
    private RecyclerView recyclerView;
    private FloatingActionButton createNewRecipe;
    private MainActivityViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNewRecipe = findViewById(R.id.addRecipeBtn);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setHasFixedSize(true);

        final MainActivityAdapter adapter = new MainActivityAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MainActivityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                Intent intent = new Intent(getApplicationContext(), RecipeInfoActivity.class);
                intent.putExtra(RecipeInfoActivity.EXTRA_RECIPE_ID, recipe.getRecipeId());
                startActivity(intent);
            }
        });


        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                adapter.setRecipes(recipes);
            }
        });




        createNewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AddEditRecipeActivity.class);
                intent.putExtra(AddEditRecipeActivity.EXTRA_RECIPE_ID, -1L);
                intent.putExtra(AddEditRecipeActivity.REQUEST_CODE, CREATE_RECIPE_REQUEST);
                //startActivityForResult(intent, CREATE_RECIPE_REQUEST);
                startActivity(intent);
            }
        });



    }


}