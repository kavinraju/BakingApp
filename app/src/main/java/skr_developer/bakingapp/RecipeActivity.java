package skr_developer.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import skr_developer.bakingapp.Database.AppExecutors;
import skr_developer.bakingapp.Database.IngredientsEntry;
import skr_developer.bakingapp.Database.RecipeDatabase;
import skr_developer.bakingapp.Database.RecipeEntry;
import skr_developer.bakingapp.Database.StepsEntry;
import skr_developer.bakingapp.Fragments.RecipeFragment;
import skr_developer.bakingapp.Fragments.RecipeStepFragment;
import skr_developer.bakingapp.Model.Ingredient;
import skr_developer.bakingapp.Model.Recipe;
import skr_developer.bakingapp.Model.Step;
import skr_developer.bakingapp.ViewModel.RecipeActivityViewModel;
import skr_developer.bakingapp.ViewModel.RecipeActivityViewModelFactory;
import skr_developer.bakingapp.WidgetUtils.RecipeIngredientsService;

public class RecipeActivity extends AppCompatActivity implements RecipeFragment.OnRecipeSetpClickListener{

    private static String TAG = RecipeActivity.class.getSimpleName();

    //Key used for SharedElementTransition
    public static String SHARED_ELEMENT_TRANSITION_EXTRA = "sharedElementTransition";

    //Key used for savedInstanceState
    private static String BUNDLE_KEY_RECIPE= "recipe";
    private static String BUNDLE_KEY_RECIPE_POSITION = "recipe_position";
    private static String BUNDLE_KEY_TWO_PANE = "two_pane";
    private static String BUNDLE_KEY_RECIPE_FRAG= "recipe_frag_bundle";
    private static String BUNDLE_KEY_RECIPE_STEP_FRAG = "recipe_step_frag_bundle";
    private static String BUNDLE_KEY = "";

    //Keys for Fragments
    private static String RECIPE_FRAG = "recipe_frag";
    private static String RECIPE_STEP_FRAG = "recipe_step_frag";

    //Key used for Intent
    public static String EXTRA_RECIPE_POSITION = "position";
    public static String EXTRA_IS_FROM_WIDGET = "isFromWidget";
    public static final String EXTRA_RECIPE_ID = "recipeID";
    public static final String EXTRA_RECIPE = "recipe";

    private int[] recipeDrawables = {R.drawable.nutella_pie, R.drawable.brownies, R.drawable.yellow_cake, R.drawable.cheesecake};
    private static boolean twoPane;
    private boolean isFav = false;
    private boolean isFromWidget = false;

    private Recipe recipe;
    private int recipePosition;
    private int recipeID;

    //Fragments
    private FragmentManager fragmentManager;
    private RecipeFragment recipeFragment;
    private RecipeStepFragment recipeStepFragment;

    // Database
    private RecipeDatabase recipeDatabase;
    private RecipeEntry recipeEntry;


    @BindView(R.id.iv_recipe_bk)
    ImageView iv_recipeBk;
    @BindView(R.id.container_recipe)
    FrameLayout container_recipe;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab_fav)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        //Check if the intent is from Wiget fo that Database query can be done.
        if (intent.hasExtra(EXTRA_IS_FROM_WIDGET)){
            isFromWidget = intent.getBooleanExtra(EXTRA_IS_FROM_WIDGET,false);
        }
        //Get teh Recipe Position
        recipePosition = getIntent().getIntExtra(EXTRA_RECIPE_POSITION, 0);

        fragmentManager = getSupportFragmentManager();

        recipeDatabase = RecipeDatabase.getInstance(getApplicationContext());

        if (isFromWidget){
            recipeID = intent.getIntExtra(EXTRA_RECIPE_ID,0);
            Log.d(TAG, String.valueOf(recipeID));
            checkIfRecipeIsFavorite(true,recipeID,savedInstanceState);

        }else {
            //It works version greater that LOLLIPOP
            Bundle bundle = getIntent().getExtras();
            assert bundle != null;

            String imageTransitionName = bundle.getString(SHARED_ELEMENT_TRANSITION_EXTRA);
            iv_recipeBk.setTransitionName(imageTransitionName);

            //fab.setTransitionName(bundle.getString("FABTransition"));
            recipe = getIntent().getParcelableExtra(EXTRA_RECIPE);
            recipeID = recipe.getId();
            checkIfRecipeIsFavorite(false,recipeID,savedInstanceState);
        }

        if (recipe != null){
            setUpUIComponents(savedInstanceState);
        }
    }

    private void setUpUIComponents(Bundle savedInstanceState){

        //Set up toolbar
        toolbar.setTitle(recipe.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setToolbarImage();

        if (findViewById(R.id.view) != null) {

            /*CODE FOR TWO PANE LAYOUT*/
            twoPane = true;

            if (savedInstanceState == null) {

                // Recipe Frag
                recipeFragment = new RecipeFragment();
                recipeFragment.setRecipe(recipe);
                recipeFragment.setRecipePosition(recipePosition);
                recipeFragment.setTwoPane(twoPane);
                fragmentManager.beginTransaction()
                        .add(R.id.container_recipe, recipeFragment, RECIPE_FRAG)
                        .commit();

                //Recipe Step Frag
                recipeStepFragment = new RecipeStepFragment();
                recipeStepFragment.setStep(recipe.getSteps().get(0));
                fragmentManager.beginTransaction()
                        .add(R.id.container_recipe_step_twopane, recipeStepFragment, RECIPE_STEP_FRAG)
                        .commit();

            } else {

                recipe = savedInstanceState.getParcelable(BUNDLE_KEY_RECIPE);
                recipePosition = savedInstanceState.getInt(BUNDLE_KEY_RECIPE_POSITION);
                twoPane = savedInstanceState.getBoolean(BUNDLE_KEY_TWO_PANE);
                recipeFragment = (RecipeFragment) getSupportFragmentManager().getFragment(savedInstanceState, RECIPE_FRAG);
                recipeStepFragment = (RecipeStepFragment) getSupportFragmentManager().getFragment(savedInstanceState, RECIPE_STEP_FRAG);

                if (recipeFragment == null) {
                    recipeFragment = new RecipeFragment();
                    recipeFragment.setRecipe(recipe);
                    recipeFragment.setRecipePosition(recipePosition);
                    recipeFragment.setTwoPane(twoPane);
                }
                if (recipeStepFragment == null) {
                    recipeStepFragment = new RecipeStepFragment();
                    recipeStepFragment.setStep(recipe.getSteps().get(recipePosition));
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.container_recipe, recipeFragment, RECIPE_FRAG)
                        .commit();
                fragmentManager.beginTransaction()
                        .replace(R.id.container_recipe_step_twopane, recipeStepFragment, RECIPE_STEP_FRAG)
                        .commit();

            }

        } else {
            /*CODE FOR NORMAL MOBILE LAYOUT*/
            twoPane = false;

            if (savedInstanceState == null) {

                // Recipe Frag
                recipeFragment = new RecipeFragment();
                recipeFragment.setRecipe(recipe);
                recipeFragment.setRecipePosition(recipePosition);
                recipeFragment.setTwoPane(twoPane);
                fragmentManager.beginTransaction()
                        .add(R.id.container_recipe, recipeFragment, RECIPE_FRAG)
                        .commit();

            } else {
                recipe = savedInstanceState.getParcelable(BUNDLE_KEY_RECIPE);
                recipePosition = savedInstanceState.getInt(BUNDLE_KEY_RECIPE_POSITION);
                twoPane = savedInstanceState.getBoolean(BUNDLE_KEY_TWO_PANE);

                recipeFragment = (RecipeFragment) getSupportFragmentManager().getFragment(savedInstanceState, RECIPE_FRAG);
                if (recipeFragment == null) {
                    recipeFragment = new RecipeFragment();
                    recipeFragment.setRecipe(recipe);
                    recipeFragment.setRecipePosition(recipePosition);
                    recipeFragment.setTwoPane(twoPane);
                    ;
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.container_recipe, recipeFragment, RECIPE_FRAG)
                        .commit();
            }

        }
    }

    private void setToolbarImage() {
        switch (recipe.getName()){
            case "Nutella Pie":
                iv_recipeBk.setBackgroundResource(recipeDrawables[0]);
                break;
            case "Brownies":
                iv_recipeBk.setBackgroundResource(recipeDrawables[1]);
                break;
            case "Yellow Cake":
                iv_recipeBk.setBackgroundResource(recipeDrawables[2]);
                break;
            case "Cheesecake":
                iv_recipeBk.setBackgroundResource(recipeDrawables[3]);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (twoPane){

            if ( getSupportFragmentManager().findFragmentByTag(RECIPE_FRAG).isAdded() ){
                getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_RECIPE_FRAG, recipeFragment);
            }
            if ( getSupportFragmentManager().findFragmentByTag(RECIPE_STEP_FRAG).isAdded() ){
                getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_RECIPE_STEP_FRAG, recipeStepFragment);
            }
            outState.putParcelable(BUNDLE_KEY_RECIPE, recipe);
            outState.putInt(BUNDLE_KEY_RECIPE_POSITION, recipePosition);
            outState.putBoolean(BUNDLE_KEY_TWO_PANE, twoPane);

        }else {

            if ( getSupportFragmentManager().findFragmentByTag(RECIPE_FRAG).isAdded() ){
                getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_RECIPE_FRAG, recipeFragment);
            }

            outState.putParcelable(BUNDLE_KEY_RECIPE, recipe);
            outState.putInt(BUNDLE_KEY_RECIPE_POSITION, recipePosition);
            outState.putBoolean(BUNDLE_KEY_TWO_PANE, twoPane);
        }
    }

    @Override
    public void onRecipeStepSelected(int position) {
        if (twoPane){
            RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
            recipeStepFragment.setStep(recipe.getSteps().get(position));


            fragmentManager.beginTransaction()
                    .replace(R.id.container_recipe_step_twopane, recipeStepFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @OnClick(R.id.fab_fav)
    public void onClickFAB(View view) {

        //Write into Database

        if (isFav){
            //Delete the contents from Database
            if (recipe != null) {
                AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        recipeDatabase.recipesDao().deleteRecipe(recipeEntry);
                        recipeDatabase.recipesDao().deleteIngredients(recipe.getId());
                        recipeDatabase.recipesDao().deleteSteps(recipe.getId());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fab.setImageResource(R.drawable.favorite_border_primary_24dp);
                            }
                        });
                    }
                });

                isFav = false;

                //Update the Widgets
                RecipeIngredientsService.startQueryingNumberOfRecipes(getApplicationContext());
            }
        }else {

            //Convert Recipe POJO into RecipeEntry POJO
            final RecipeEntry recipeEntry = new RecipeEntry(recipe.getId(),recipe.getName(),recipe.getServings(),recipe.getImage());
            this.recipeEntry = recipeEntry;

            //Convert List of Ingredient POJO into List of IngredientsEntry POJO
            final List<IngredientsEntry> ingredientsEntries = new ArrayList<>();
            Log.d(TAG,"Size - Ingredients - " + recipe.getIngredients().size());
            for (Ingredient ingredient: recipe.getIngredients()){
                ingredientsEntries.add(new IngredientsEntry(recipe.getId()
                        ,ingredient.getQuantity()
                        ,recipe.getName()
                        ,ingredient.getMeasure()
                        ,ingredient.getIngredient()));
            }

            //Convert List of Step POJO into List of StepsEntry POJO
            final List<StepsEntry> stepsEntries = new ArrayList<>();
            for (int i =0; i< recipe.getSteps().size(); i++){
                Step step = recipe.getSteps().get(i);
                stepsEntries.add(new StepsEntry(recipe.getId(),
                        step.getShortDescription(),
                        step.getDescription(),
                        step.getVideoURL(),
                        step.getThumbnailURL()));
                Log.d(TAG,"Step - RecipeId" + recipe.getId());
            }

            // After converting all the POJOs write into Database
            AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    recipeDatabase.recipesDao().insertRecipe(recipeEntry);
                    recipeDatabase.recipesDao().insertIngredients(ingredientsEntries);
                    recipeDatabase.recipesDao().insertSteps(stepsEntries);
                    Log.d(TAG,"Size - stepsEntries" + stepsEntries.size());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fab.setImageResource(R.drawable.favorite_primary_24dp);
                            Toast.makeText(RecipeActivity.this, "Done", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            isFav = true;

            //Update the Widgets
            RecipeIngredientsService.startQueryingNumberOfRecipes(getApplicationContext());
        }
    }

    // Note: This is Lifecycle aware component.
    private void checkIfRecipeIsFavorite(boolean isFromWidget, final int recipeID, final Bundle savedInstancestate){

        RecipeActivityViewModelFactory recipeActivityViewModelFactory = new RecipeActivityViewModelFactory(recipeDatabase,recipeID,isFromWidget);
        RecipeActivityViewModel recipeActivityViewModel = ViewModelProviders
                .of(this,recipeActivityViewModelFactory)
                .get(RecipeActivityViewModel.class);
        recipeActivityViewModel.getRecipeEntryLiveData().observe(this, new Observer<RecipeEntry>() {
            @Override
            public void onChanged(@Nullable RecipeEntry recipeEntry1) {

                recipeEntry = recipeEntry1;
                if (recipeEntry != null){
                    Log.d(TAG," recipeEntry not null");
                }else {
                    Log.d(TAG," recipeEntry null");
                }

                /* Check if this recipe is in Database by passing this Recipe's ID.
                 If if it's there then it returns RecipeEntry else it returns null.*/

                isFav = recipeEntry1 !=null;
                if (isFav){
                    fab.setImageResource(R.drawable.favorite_primary_24dp);
                }else {
                    fab.setImageResource(R.drawable.favorite_border_primary_24dp);
                }
            }
        });

        if (isFromWidget){

            final List<Ingredient> ingredients = new ArrayList<>();
            final List<Step> steps = new ArrayList<>();
            recipeActivityViewModel.getIngredientsListLiveData().observe(this, new Observer<List<IngredientsEntry>>() {
                @Override
                public void onChanged(@Nullable List<IngredientsEntry> ingredientsEntries) {
                    assert ingredientsEntries != null;
                    for (IngredientsEntry entry : ingredientsEntries) {
                        ingredients.add(new Ingredient(entry.getQuantity()
                                , entry.getMeasure()
                                , entry.getIngredient()));
                    }

                    if (ingredientsEntries.size() != 0) {
                        Log.d(TAG, "ingredientsEntries not null");
                    } else {
                        Log.d(TAG, "ingredientsEntries null");
                    }
                    if (recipeEntry != null) {
                        recipe = new Recipe(recipeEntry.getRecipeId()
                                , recipeEntry.getName()
                                , ingredients, steps
                                , recipeEntry.getServings()
                                , recipeEntry.getImage());
                        setUpUIComponents(savedInstancestate);
                        Log.d(TAG, "recipe not null");
                    }
                }

            });
            recipeActivityViewModel.getStepsListLiveData().observe(this, new Observer<List<StepsEntry>>() {
                @Override
                public void onChanged(@Nullable List<StepsEntry> stepsEntries) {
                    assert stepsEntries != null;
                    for (StepsEntry entry: stepsEntries){
                        steps.add(new Step(entry.getId()
                                ,entry.getShortDescription()
                                ,entry.getDescription()
                                ,entry.getVideoURL()
                                ,entry.getThumbnailURL()));
                    }
                    if (stepsEntries.size() != 0){
                        Log.d(TAG,"stepsEntries not null");
                    }else {
                        Log.d(TAG,"stepsEntries null");
                    }
                }
            });

        }
        }
     /*
     // Note: This is not a Lifecycle aware component.
     AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {

                Log.d("Run","Query");
                //recipeEntry = recipeDatabase.recipesDao().loadRecipe(recipe.getId());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isFav = recipeEntry !=null;
                        if (isFav){
                            Toast.makeText(RecipeActivity.this, "Fav", Toast.LENGTH_SHORT).show();
                            fab.setImageResource(R.drawable.favorite_primary_24dp);
                        }else {
                            Toast.makeText(RecipeActivity.this, "notfav", Toast.LENGTH_SHORT).show();
                            fab.setImageResource(R.drawable.favorite_border_primary_24dp);
                        }
                    }
                });
            }
        });*/

}
