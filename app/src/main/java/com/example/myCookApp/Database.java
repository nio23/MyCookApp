package com.example.myCookApp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myCookApp.models.Image;
import com.example.myCookApp.models.Ingredient;
import com.example.myCookApp.models.Recipe;
import com.example.myCookApp.models.RecipeIngredient;
import com.example.myCookApp.models.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@androidx.room.Database(entities = {Recipe.class, Step.class, Image.class, Ingredient.class, RecipeIngredient.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract StepDao stepDao();
    public abstract ImageDao imageDao();
    public abstract RecipeDao recipeDao();
    public abstract IngredientDao ingredientDao();
    public abstract RecipeIngredientDao recipeIngredientDao();

    private static volatile Database INSTANCE;
    private static final int NUMBER_OF_THREADS = 6;
    static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext()
                            , Database.class, "test_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(createDatabase)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback createDatabase = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriterExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    RecipeDao recipeDao = INSTANCE.recipeDao();
                    StepDao stepDao = INSTANCE.stepDao();
                    ImageDao imageDao = INSTANCE.imageDao();
                    IngredientDao ingredientDao = INSTANCE.ingredientDao();
                    RecipeIngredientDao recipeIngredientDao = INSTANCE.recipeIngredientDao();

                    ArrayList<Ingredient> ingredients = new ArrayList<>();
                    ingredients.add(new Ingredient("Ελαιόλαδο"));
                    ingredients.add(new Ingredient("Κιμάς"));
                    ingredients.add(new Ingredient("Κρεμμύδια"));
                    ingredients.add(new Ingredient("Σκόρδο"));
                    ingredients.add(new Ingredient("Ζάχαρη κρυσταλλική"));
                    ingredients.add(new Ingredient("Θυμάρι"));
                    ingredients.add(new Ingredient("Πελτέ ντομάτας"));
                    ingredients.add(new Ingredient("Κρασί κόκκινο"));
                    ingredients.add(new Ingredient("Κύβο κότας"));
                    ingredients.add(new Ingredient("Στικ κανέλα"));
                    ingredients.add(new Ingredient("Φύλλα δάφνης"));
                    ingredients.add(new Ingredient("Κόκκους μπαχάρι"));
                    ingredients.add(new Ingredient("Ντομάτα κονκασέ"));
                    ingredients.add(new Ingredient("Σπαγγέτι"));
                    ingredients.add(new Ingredient("Κρέμα γάλακτος 35%"));
                    ingredients.add(new Ingredient("Παρμεζάνα"));
                    ingredients.add(new Ingredient("Πατάτες"));
                    ingredients.add(new Ingredient("Καρότα"));
                    ingredients.add(new Ingredient("Φασόλια"));
                    ingredients.add(new Ingredient("Ντομάτα"));
                    ingredients.add(new Ingredient("Μαρούλι"));
                    ingredients.add(new Ingredient("Αγγούρι"));
                    ingredients.add(new Ingredient("Κολοκύθια"));
                    ingredients.add(new Ingredient("Κρασί Λευκό"));
                    ingredients.add(new Ingredient("Λεμόνι"));
                    ingredients.add(new Ingredient("Νερό"));
                    ingredients.add(new Ingredient("Κύβο Λαχανικών"));
                    ingredients.add(new Ingredient("Φασολάκια"));
                    ingredients.add(new Ingredient("Δυόσμο"));
                    ingredients.add(new Ingredient("Μαϊντανό"));
                    ingredients.add(new Ingredient("Κρεμμύδια Φρέσκα"));

                    long[] ingredientsId = ingredientDao.insert(ingredients);



                    Recipe recipe1 = new Recipe("Μακαρόνια με κιμά","Άκης Πετρετζίκης");
                    recipe1.setMainPhoto("https://qul.imgix.net/d7a5d4ba-6688-4c08-a22f-03f7832adb68/528386_sld.jpg");
                    long recipe1Id = recipeDao.insertAndGetId(recipe1);

                    List<Step> recipe1steps = new ArrayList<>();
                    recipe1steps.add(new Step(recipe1Id,"Βάζουμε ένα βαθύ αντικολλητικό τηγάνι σε δυνατή φωτιά να κάψει πολύ καλά και ρίχνουμε 1 κ.σ. ελαιόλαδο."));
                    recipe1steps.add(new Step(recipe1Id,"Προσθέτουμε το ¼ από τον κιμά, κόβοντάς τον σε κομματάκια με το χέρι μας. Σοτάρουμε μέχρι να καραμελώσει και να πάρει χρώμα εξωτερικά (δεν θέλουμε να ψηθεί τελείως)."));
                    recipe1steps.add(new Step(recipe1Id,"Αφαιρούμε τον κιμά από το τηγάνι με μια τρυπητή κουτάλα, τον βάζουμε σε ένα μπολ και επαναλαμβάνουμε άλλες τρεις φορές την ίδια διαδικασία με τον υπόλοιπο κιμά (αν προσθέσουμε όλο τον κιμά κατευθείαν δεν θα καραμελώσει όμορφα)."));
                    recipe1steps.add(new Step(recipe1Id,"Στο ίδιο τηγάνι και χωρίς να το καθαρίσουμε προσθέτουμε το υπόλοιπο ελαιόλαδο, τα κρεμμύδια, το σκόρδο, τη ζάχαρη, το ξερό θυμάρι και αφήνουμε να καραμελώσει το κρεμμύδι για τουλάχιστον 4-5 λεπτά.",5*60));
                    recipe1steps.add(new Step(recipe1Id,"Στη συνέχεια, ρίχνουμε τον κιμά, τον πελτέ και ανακατεύουμε."));
                    recipe1steps.add(new Step(recipe1Id,"Σβήνουμε με το κρασί και αφήνουμε να εξατμιστεί τελείως το αλκοόλ."));
                    recipe1steps.add(new Step(recipe1Id,"Με το που εξατμιστεί το κρασί, ρίχνουμε μέσα το νερό, τον κύβο, το στικ κανέλας, τα φύλλα δάφνης, τα μπαχάρια και τη ντομάτα κονκασέ."));
                    recipe1steps.add(new Step(recipe1Id,"Χαμηλώνουμε τη φωτιά, ανακατεύουμε και αφήνουμε να σιγοβράσει για τουλάχιστον 10 λεπτά.",10*60));
                    recipe1steps.add(new Step(recipe1Id,"Όση ώρα βράζει ο κιμάς, ετοιμάζουμε τα ζυμαρικά μας."));
                    recipe1steps.add(new Step(recipe1Id,"Σε μία κατσαρόλα με μπόλικο αλατισμένο νερό που βράζει, ρίχνουμε τα μακαρόνια."));
                    recipe1steps.add(new Step(recipe1Id,"Τα βράζουμε σύμφωνα με τις οδηγίες της συσκευασίας, μείον ένα λεπτό. Θέλουμε τα μακαρόνια μας να είναι al dente."));
                    recipe1steps.add(new Step(recipe1Id,"Μόλις τα ζυμαρικά μας είναι έτοιμα, τα βγάζουμε από τη φωτιά, τα σουρώνουμε και αφήνουμε στην άκρη."));
                    recipe1steps.add(new Step(recipe1Id,"Αφού έχει δέσει η σάλτσα μας, προσθέτουμε την κρέμα γάλακτος, ανακατεύουμε και σβήνουμε τη φωτιά."));
                    recipe1steps.add(new Step(recipe1Id,"Σερβίρουμε τα μακαρόνια με τη σάλτσα κιμά από πάνω, πασπαλίζουμε με φρέσκια παρμεζάνα, βασιλικό και μπόλικο φρέσκο πιπέρι."));
                    List<Long> stepsId= stepDao.insert(recipe1steps);

                    List<Image> images = new ArrayList<>();
                    images.add(new Image(stepsId.get(2),"https://img-global.cpcdn.com/recipes/4fb3e0dcc31c074d/751x532cq70/%CE%BA%CF%8D%CF%81%CE%B9%CE%B1-%CF%86%CF%89%CF%84%CE%BF%CE%B3%CF%81%CE%B1%CF%86%CE%AF%CE%B1-%CF%83%CF%85%CE%BD%CF%84%CE%B1%CE%B3%CE%AE%CF%82-mosxaraki-kokkinisto-ala-tzortzina.jpg"));
                    images.add(new Image(stepsId.get(2),"https://d1uz88p17r663j.cloudfront.net/resized/fbf9b801b39c4aab54d7351f44fbcce9_%CF%80%CE%B9%CE%BA%CE%AC%CE%BD%CF%84%CE%B9%CE%BA%CE%BF_%CE%BC%CE%BF%CF%83%CF%87%CE%AC%CF%81%CE%B9_944_531.jpg"));
                    images.add(new Image(stepsId.get(3),"https://www.dimitrisskarmoutsos.gr/photos/Garides-saganaki-me-feta.jpg"));
                    images.add(new Image(stepsId.get(3),"https://d3fch0cwivr6nf.cloudfront.net/system/uploads/medium/data/9320/garides-tempura-site.jpg"));
                    images.add(new Image(stepsId.get(3),"https://www.giorgostsoulis.com/media/k2/items/cache/dbe2064f7b08a4acf308a99bd5078f1b_L.jpg"));
                    imageDao.insert(images);



                    ArrayList<RecipeIngredient> recipeIngredients = new ArrayList<>();
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[0],"3 κ.σ."));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[1],"750 γρ."));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[2],"2"));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[3],"1 σκ."));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[4],"1 κ.γ."));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[5],"1 κ.σ."));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[6],"2 κ.σ."));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[7],"100 ml"));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[8],"1"));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[9],"1"));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[10],"2"));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[11],"3"));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[12],"400 γρ."));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[13],"500 γρ."));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[14],"100 ml"));
                    recipeIngredients.add(new RecipeIngredient(1,ingredientsId[15],"τριμμένη"));

                    recipeIngredientDao.insert(recipeIngredients);

                    Recipe recipe2 = new Recipe("Φασολάκια λαδερά", "Άκης Πετρετζίκης");
                    recipe2.setMainPhoto("https://d3fch0cwivr6nf.cloudfront.net/system/uploads/recipe/data/3366/recipe_thumb_fasolakia-coca-cola.jpg");
                    long recipe2Id = recipeDao.insertAndGetId(recipe2);

                    ArrayList<Step> recipe2Steps = new ArrayList<>();
                    recipe2Steps.add(new Step(recipe2Id,"Τοποθετούμε μία κατσαρόλα σε δυνατή φωτιά και προσθέτουμε 2 κ.σ. ελαιόλαδο."));
                    recipe2Steps.add(new Step(recipe2Id,"Κόβουμε σε χοντρά κομμάτια το κρεμμύδι και το βάζουμε στην κατσαρόλα."));
                    recipe2Steps.add(new Step(recipe2Id,"Ψιλοκόβουμε το σκόρδο και το βάζουμε στην κατσαρόλα μαζί με τη ζάχαρη. Σοτάρουμε μέχρι να μαλακώσει το κρεμμύδι."));
                    recipe2Steps.add(new Step(recipe2Id,"Ξεφλουδίζουμε τις πατάτες, τις κόβουμε σε κομμάτια και τις βάζουμε στην κατσαρόλα."));
                    recipe2Steps.add(new Step(recipe2Id,"Προσθέτουμε τον πελτέ, σβήνουμε με το κρασί, ανακατεύουμε και αφήνουμε μέχρι να εξατμιστεί."));
                    recipe2Steps.add(new Step(recipe2Id,"Βάζουμε το ξύσμα από το λεμόνι, τη ντομάτα κονκασέ, το νερό και τον κύβο."));
                    recipe2Steps.add(new Step(recipe2Id,"Κόβουμε τις άκρες από τα φασολάκια, τα βάζουμε στην κατσαρόλα και ανακατεύουμε."));
                    recipe2Steps.add(new Step(recipe2Id,"Κλείνουμε το καπάκι, χαμηλώνουμε τη φωτιά σε μέτρια προς χαμηλή και σιγοβράζουμε για 40-50 λεπτά."));
                    recipe2Steps.add(new Step(recipe2Id,"Αφαιρούμε από τη φωτιά και προσθέτουμε τον δυόσμο, τον μαϊντανό και το πράσινο μέρος από τα φρέσκα κρεμμύδια που έχουμε ψιλοκόψει, αλάτι, πιπέρι, 80 γρ. ελαιόλαδο, ανακατεύουμε και σερβίρουμε."));
                    recipe2Steps.add(new Step(recipe2Id,"Σερβίρουμε προαιρετικά με φέτα που έχουμε πασπαλίσει με τη ρίγανη και το ελαιόλαδο και φέτες από ψωμί."));
                    stepDao.insert(recipe2Steps);

                    ArrayList<RecipeIngredient> recipeIngredients2 = new ArrayList<>();
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[2],"1"));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[0],"100 γρ."));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[3],"1 σκ."));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[4],"1 κ.γ."));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[16],"500 γρ."));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[6],"1 κ.σ. γεμάτη"));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[23],"50 γρ."));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[24],"ξύσμα λεμονιού, από 1"));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[12],"400 γρ."));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[25],"300 γρ."));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[26],"1"));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[27],"1 κιλό"));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[28],"1/3 ματσάκι"));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[30],"2"));
                    recipeIngredients2.add(new RecipeIngredient(recipe2Id, ingredientsId[29],"1/3 ματσάκι"));

                    recipeIngredientDao.insert(recipeIngredients2);


                }
            });
        }
    };
}
