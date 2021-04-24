package com.example.myCookApp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import com.example.myCookApp.Util;
import com.example.myCookApp.dialogs.ImagePreviewDialog;
import com.example.myCookApp.dialogs.ImageSelectionDialog;
import com.example.myCookApp.dialogs.TimePickerDialog;
import com.example.myCookApp.models.Image;
import com.example.myCookApp.R;
import com.example.myCookApp.StepClickInterface;
import com.example.myCookApp.models.IngredientWithQuantity;
import com.example.myCookApp.models.Step;
import com.example.myCookApp.models.StepWithImages;
import com.example.myCookApp.adapters.StepsListAdapter;
import com.example.myCookApp.viewmodels.AddEditRecipeViewModel;
import com.example.myCookApp.viewmodels.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.RxWorker;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class AddEditRecipeActivity extends AppCompatActivity implements StepClickInterface {
    public static final String EXTRA_RECIPE_ID = "com.example.test.AddEditRecipeActivity.EXTRA_RECIPE";
    public static final String REQUEST_CODE = "com.example.test.AddEditRecipeActivity.REQUEST_CODE";
    private static final int SELECT_INGREDIENTS_REQUEST = 1;
    private final int STEP_SELECTION_DIALOG_REQUEST = 2;
    private final int STEP_SELECT_IMAGE_REQUEST = 4;
    private final int MAINPHOTO_SELECTION_DIALOG_REQUEST = 5;
    private final int MAINPHOTO_SELECT_IMAGE_REQUEST = 7;
    private final int MAINPHOTO_IMAGE_CAPTURE = 8;
    private final int STEP_IMAGE_CAPTURE = 9;
    private TextView ingredientsTV, mainPhotoTV;
    private TextInputEditText titleET, creatorTI;
    private ImageView mainPhotoImage;
    private Button selectIngredientsBtn, addNewStepBtn, publishBtn;
    private RecyclerView stepsRecyclerView;
    private AddEditRecipeViewModel viewModel;
    private long recipeId;
    private Uri imageCapturedUri;
    private long currentStepId;
    private long backPressedTime;
    private StepsListAdapter adapter;
    private Intent intent;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private String currentMainPhotoUri="";
    private Util util = new Util();
    private long timeSinceLastRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_recipe);
        initViews();
        intent = getIntent();
        recipeId = intent.getExtras().getLong(EXTRA_RECIPE_ID);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication(), recipeId)).get(AddEditRecipeViewModel.class);
        adapter = new StepsListAdapter(this);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        stepsRecyclerView.setAdapter(adapter);

        timeSinceLastRequest = System.currentTimeMillis();

        disposables.add(viewModel.getRecipeWithStepsAndImagesFlowable()
                .subscribeOn(Schedulers.io())
                .firstElement()
                .subscribe( recipe -> {
                    viewModel.setBackUpRecipeWithStepsAndImages(recipe);
                } ));




        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                StepWithImages deletedStepWithImages = adapter.getStepWithImagesAt(position);
                viewModel.deleteStepWithImages(deletedStepWithImages);
                Snackbar.make(stepsRecyclerView,"Καταλάθος διαγραφή; ", Snackbar.LENGTH_LONG)
                        .setAction("Επαναφορά", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewModel.restoreStepWithImages(deletedStepWithImages);
                            }
                        }).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(AddEditRecipeActivity.this, R.color.colorAccent))
                        .addActionIcon(R.drawable.ic_delete_sweep_40dp)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        }).attachToRecyclerView(stepsRecyclerView);




        viewModel.getIngredientsWithQuantity().observe(this, new Observer<List<IngredientWithQuantity>>() {
            @Override
            public void onChanged(List<IngredientWithQuantity> ingredientsWithQuantities) {
                if (!ingredientsWithQuantities.isEmpty()) {
                    ingredientsTV.setText(util.transformIngredients(ingredientsWithQuantities));
                }
            }
        });


        selectIngredientsBtn.setOnClickListener(selectIngredientsBtnListener);
        addNewStepBtn.setOnClickListener(createNewStepBtnListener);
        publishBtn.setOnClickListener(publishBtnListener);
        mainPhotoImage.setOnClickListener(mainPhotoListener);
        mainPhotoTV.setOnClickListener(mainPhotoListener);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()){
            if (intent.getExtras().getInt(REQUEST_CODE) == MainActivity.CREATE_RECIPE_REQUEST){
                /*Intent data = new Intent();
                data.putExtra(EXTRA_RECIPE_ID, viewModel.getRecipeId());
                setResult(RESULT_CANCELED, data);*/
                viewModel.deleteRecipe();
            }else if (intent.getExtras().getInt(REQUEST_CODE) == RecipeInfoActivity.EDIT_REQUEST){
                viewModel.restoreRecipe();
            }
            super.onBackPressed();
        }else{
            Toast.makeText(getBaseContext(),"Όλες οι αλλαγές θα χαθούν, είστε σίγουρος;", Toast.LENGTH_LONG).show();
        }
        backPressedTime = System.currentTimeMillis();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case SELECT_INGREDIENTS_REQUEST:
                    if (data != null) {
                        viewModel.setIngredientsWithQuantity(data.getParcelableArrayListExtra(SelectIngredientsActivity.SELECTED_INGREDIENTS_WITH_QUANTITIES));
                        break;
                    }
                case STEP_SELECT_IMAGE_REQUEST:
                    if (data != null) {
                        viewModel.addNewImage(currentStepId, getUri(data.getData()));
                        break;
                    }
                case MAINPHOTO_SELECT_IMAGE_REQUEST:
                    if (data != null) {
                        Uri imageUri = data.getData();
                        viewModel.setMainPhoto(getUri(imageUri));
                        mainPhotoTV.setVisibility(View.GONE);
                        break;
                    }
                case MAINPHOTO_IMAGE_CAPTURE:
                    viewModel.setMainPhoto(imageCapturedUri.toString());
                    mainPhotoTV.setVisibility(View.GONE);
                    break;
                case STEP_IMAGE_CAPTURE:
                    viewModel.addNewImage(currentStepId, imageCapturedUri.toString());
                    break;
            }
    }

    private View.OnClickListener createNewStepBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewModel.addNewStep();
        }
    };

    private View.OnClickListener selectIngredientsBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SelectIngredientsActivity.class);
            intent.putExtra(SelectIngredientsActivity.EXTRA_RECIPE_ID, recipeId);
            startActivityForResult(intent, SELECT_INGREDIENTS_REQUEST);
        }
    };

    private View.OnClickListener publishBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //viewModel.updateRecipe(titleET.getText().toString(), creatorTI.getText().toString());
            String title = titleET.getText().toString().trim();
            String creator = creatorTI.getText().toString().trim();
            String mainPhoto = currentMainPhotoUri;
            if (!title.isEmpty()) {
                Log.d("CurrentListAdapter", ""+adapter.getCurrentList().toString());
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        viewModel.updateRecipe(title, creator, mainPhoto, adapter.getCurrentList());
                    }
                }).start();
                setResult(RESULT_OK);
                finish();*/
            }else {
                Toast.makeText(getBaseContext(),"Συμπληρώστε τα απαραίτητα στοιχεία",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener mainPhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectionDialog(MAINPHOTO_SELECTION_DIALOG_REQUEST);
        }
    };

    private void dispatchTakePictureIntent(int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            imageCapturedUri = null;
            try {
                imageCapturedUri = createImageUri();
            } catch (IOException e) {
                Toast.makeText(this, "Error creating the file!", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (imageCapturedUri != null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageCapturedUri);
                startActivityForResult(takePictureIntent, requestCode);

            }
        }
    }

    private Uri createImageUri() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg");
        imageCapturedUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return imageCapturedUri;
    }

    private void initViews() {
        ingredientsTV = findViewById(R.id.ingredientsTV);
        titleET = findViewById(R.id.titleET);
        mainPhotoImage = findViewById(R.id.mainPhotoImage);
        mainPhotoTV = findViewById(R.id.mainPhotoText);
        selectIngredientsBtn = findViewById(R.id.selectIngredientsBtn);
        addNewStepBtn = findViewById(R.id.addNewStepBtn);
        publishBtn = findViewById(R.id.publishBtn);
        stepsRecyclerView = findViewById(R.id.stepsRecyclerView);
        creatorTI = findViewById(R.id.creatorTextInput);
    }

    private String getUri(Uri imageUri){
        if (isMediaDocument(imageUri)) {
            final String docId = DocumentsContract.getDocumentId(imageUri);
            final String[] split = docId.split(":");
            String name = split[1];
            return  MediaStore.Images.Media.EXTERNAL_CONTENT_URI + File.separator + name;
        }
        return null;
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void selectImage(int requestCode) {
        Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickPhoto.setType("image/*");
        if (requestCode == STEP_SELECTION_DIALOG_REQUEST){
            startActivityForResult(pickPhoto, STEP_SELECT_IMAGE_REQUEST);
        }
        else if (requestCode == MAINPHOTO_SELECTION_DIALOG_REQUEST) {
            startActivityForResult(pickPhoto, MAINPHOTO_SELECT_IMAGE_REQUEST);
        }
    }

    public void selectionDialog(int requestCode){
        ImageSelectionDialog dialog = new ImageSelectionDialog(this, requestCode);
        dialog.show(getSupportFragmentManager(),"image selection dialog");
    }



    @Override
    public void onTextChange(Step step, String s) {
        step.setInstructions(s);
        viewModel.updateStep(step);
    }



    @Override
    public void onImageItemClick(String imageUri) {
        ImagePreviewDialog dialog = ImagePreviewDialog.newInstance(imageUri);
        dialog.show(getSupportFragmentManager(),"imagePreviewDialog");
    }

    @Override
    public void onTogglerSwitch(Step step) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, step);
        timePickerDialog.show(getSupportFragmentManager(),"time picker dialog");
    }

    @Override
    public void getTime(int hours, int minutes, Step step) {
        int timeInSeconds = hours * 3600 + minutes * 60;
            Step updateStep = new Step(step.getFk_recipeId(), step.getInstructions());
            updateStep.setStepId(step.getStepId());
            updateStep.setStepTime(timeInSeconds);
            viewModel.updateStep(updateStep);
    }

    @Override
    public void addNewImageClick(long stepId) {
        currentStepId = stepId;
        selectionDialog(STEP_SELECTION_DIALOG_REQUEST);
    }

    @Override
    public void onDeleteImage(Image image) {
        viewModel.deleteImage(image);
    }

    @Override
    public void dialogSelection(int id, int requestCode) {
        switch (id){
            case R.id.camera:
                if (requestCode == MAINPHOTO_SELECTION_DIALOG_REQUEST)
                    checkForWriteStoragePermission(MAINPHOTO_IMAGE_CAPTURE);
                else if (requestCode == STEP_SELECTION_DIALOG_REQUEST)
                    checkForWriteStoragePermission(STEP_IMAGE_CAPTURE);
                break;
            case R.id.image:
                checkForReadExternalStoragePermission(requestCode);
                break;
        }
    }



    public void checkForReadExternalStoragePermission(int requestCode){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(AddEditRecipeActivity.this, "You already have granted this permission", Toast.LENGTH_SHORT).show();
            selectImage(requestCode);
        } else {
            requestReadExternalStoragePermission(requestCode);
        }
    }

    public void checkForWriteStoragePermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(AddEditRecipeActivity.this, "You already have granted this permission", Toast.LENGTH_SHORT).show();
            dispatchTakePictureIntent(requestCode);
        } else {
            requestWriteStoragePermission(requestCode);
        }
    }

    private void requestReadExternalStoragePermission(int requestCode){
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddEditRecipeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Άδεια πρόσβασης")
                    .setMessage("Η εφαρμογή χρειάζεται πρόσβαση στα αρχεία σας, ώστε να μπορείτε να ανεβάζετε εικόνες.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AddEditRecipeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);

                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        }
    }


    private void requestWriteStoragePermission(int requestCode){
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddEditRecipeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Άδεια πρόσβασης")
                    .setMessage("Η εφαρμογή χρειάζεται πρόσβαση, ώστε να αποθηκεύονται οι φωτογραφίες.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AddEditRecipeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);

                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STEP_IMAGE_CAPTURE || requestCode == MAINPHOTO_IMAGE_CAPTURE)
            dispatchTakePictureIntent(requestCode);
        if (requestCode == STEP_SELECTION_DIALOG_REQUEST || requestCode == MAINPHOTO_SELECTION_DIALOG_REQUEST){
            selectImage(requestCode);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if (v instanceof EditText){
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(),(int) ev.getRawY())){
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        disposables.add(viewModel.getTitle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(title -> {
                    if (title!= null && !title.isEmpty())
                            titleET.setText(title);
                        }
                        ));

        disposables.add(viewModel.getCreator()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(creator -> {
                    if (creator !=null && !creator.isEmpty())
                    creatorTI.setText(creator);
                }));

        disposables.add(viewModel.getMainPhoto()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mainPhoto -> {
                    if (mainPhoto!= null && !mainPhoto.trim().isEmpty()) {
                        Picasso.get().load(mainPhoto).into(mainPhotoImage);
                        mainPhotoTV.setText(null);
                        currentMainPhotoUri = mainPhoto;
                    }
                }));

        disposables.add(viewModel.getStepsWithImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stepsWithImages -> {
                    if (stepsWithImages != null )
                    adapter.submitList(stepsWithImages);
                }));


    }

}
