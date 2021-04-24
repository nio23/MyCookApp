package com.example.myCookApp.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myCookApp.R;
import com.example.myCookApp.Repository;
import com.example.myCookApp.Util;
import com.example.myCookApp.adapters.RecipeInfoAdapter;
import com.example.myCookApp.models.Recipe;
import com.example.myCookApp.models.RecipeWithStepsAndImages;
import com.example.myCookApp.models.StepWithImages;
import com.example.myCookApp.viewmodels.RecipeInfoViewModel;
import com.example.myCookApp.viewmodels.ViewModelFactory;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;

public class RecipeInfoActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "com.example.myCookApp.activities.RecipeInfoActivity.EXTRA_RECIPE_ID" ;
    public static final int EDIT_REQUEST = 2;
    private RecipeInfoAdapter adapter;
    private long recipeId;
    private RecipeInfoViewModel viewModel;
    private TextView titleTV, ingredientsTV;
    private RecyclerView stepsRV;
    private Button execute;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final Util util = new Util();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);
        initViews();
        Intent intent = getIntent();
        if (intent.getExtras() != null){
            recipeId = getIntent().getExtras().getLong(EXTRA_RECIPE_ID);
        }
        Log.d("RecipeInfo",""+recipeId);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication(), recipeId)).get(RecipeInfoViewModel.class);

        adapter = new RecipeInfoAdapter();
        stepsRV.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        stepsRV.setAdapter(adapter);

        execute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecipeInfoActivity.this, ExecuteRecipeActivity.class);
                    intent.putExtra(EXTRA_RECIPE_ID, recipeId);
                    startActivity(intent);
                }
            });

    }

    private void initViews() {
        titleTV = findViewById(R.id.titleTV);
        stepsRV = findViewById(R.id.stepsRV);
        ingredientsTV = findViewById(R.id.ingredientsTV);
        execute = findViewById(R.id.executeBtn);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipeinfo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit:
                Intent intent = new Intent(this, AddEditRecipeActivity.class);
                intent.putExtra(AddEditRecipeActivity.EXTRA_RECIPE_ID, recipeId);
                intent.putExtra(AddEditRecipeActivity.REQUEST_CODE, EDIT_REQUEST);
                startActivityForResult(intent, EDIT_REQUEST);
                //Log.d("RecipeInfo",""+recipe.getId());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        disposables.add(viewModel.getTitle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(title-> titleTV.setText(title) ));

        disposables.add(viewModel.getIngredientsWithQuantities()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ingredientWithQuantities ->{
                            if (ingredientWithQuantities!=null && !ingredientWithQuantities.isEmpty())
                                ingredientsTV.setText(util.transformIngredients(ingredientWithQuantities));
                        }
                ));

        disposables.add(viewModel.getStepsWithImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stepsWithImages ->{
                    if(stepsWithImages !=null)
                        adapter.submitList(stepsWithImages);
                }
                ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
