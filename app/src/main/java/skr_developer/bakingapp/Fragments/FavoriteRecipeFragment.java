package skr_developer.bakingapp.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.bakingapp.Adapter.RecipeCardAdapter;
import skr_developer.bakingapp.Database.IngredientsEntry;
import skr_developer.bakingapp.Database.RecipeDatabase;
import skr_developer.bakingapp.Database.RecipeEntry;
import skr_developer.bakingapp.Database.StepsEntry;
import skr_developer.bakingapp.Model.Ingredient;
import skr_developer.bakingapp.Model.Recipe;
import skr_developer.bakingapp.Model.Step;
import skr_developer.bakingapp.R;
import skr_developer.bakingapp.RecipeActivity;
import skr_developer.bakingapp.ViewModel.FavoriteRecipeFragmentViewModel;

public class FavoriteRecipeFragment extends Fragment
        implements RecipeCardAdapter.OnClickRecipeCardListener {

    //Keys used for savedInstanceState
    public static String SHARED_ELEMENT_TRANSITION_EXTRA = "sharedElementTransition";
    public static String RECIPE_LIST_BUNDLE_KEY = "recipeList";

    private List<Recipe> recipeList = new ArrayList<>();
    private RecipeCardAdapter recipeCardAdapter;
    private List<RecipeEntry> recipeEntries;
    private List<IngredientsEntry> ingredientsEntries;
    private List<StepsEntry> stepsEntries;

    private Context context;
    private RecipeDatabase recipeDatabase;

    @BindView(R.id.recyclerview_home)
    RecyclerView recyclerView;
    @BindView(R.id.linear_layout_no_fav_recipies)
    LinearLayout linearLayoutNoRecipies;

    public FavoriteRecipeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list_home,container,false);
        ButterKnife.bind(this,view);
        context = view.getContext();
        linearLayoutNoRecipies.setVisibility(View.GONE);
        recipeDatabase = RecipeDatabase.getInstance(context);
        queryDataFromDatabase();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            recipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST_BUNDLE_KEY);
            setRecipeAdapter(recipeList);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE_LIST_BUNDLE_KEY, (ArrayList<? extends Parcelable>) recipeList);
    }


    private void queryDataFromDatabase() {

        FavoriteRecipeFragmentViewModel favoriteRecipeFragmentViewModel = ViewModelProviders.of(this).get(FavoriteRecipeFragmentViewModel.class);
        favoriteRecipeFragmentViewModel.getIngridientsLiveData().observe(this, new Observer<List<IngredientsEntry>>() {
            @Override
            public void onChanged(@Nullable List<IngredientsEntry> ingredientsEntries1) {
                ingredientsEntries = ingredientsEntries1;
            }
        });


        favoriteRecipeFragmentViewModel.getStepsLiveData().observe(this, new Observer<List<StepsEntry>>() {
            @Override
            public void onChanged(@Nullable List<StepsEntry> stepsEntries1) {
                stepsEntries = stepsEntries1;
                assert stepsEntries1 != null;
                Log.d("Step","stepsEntries1 - " + stepsEntries1.size());
            }
        });


        favoriteRecipeFragmentViewModel.getRecipesLiveData().observe(this, new Observer<List<RecipeEntry>>() {
            @Override
            public void onChanged(@Nullable List<RecipeEntry> recipeEntries1) {
                recipeEntries = recipeEntries1;

                if (ingredientsEntries != null&& stepsEntries != null) {
                    List<Recipe> recipes = new ArrayList<>();

                    /**
                     * Here we convert all the POJO's used for database into POJO's used for RecyclerView Adapters
                     */
                    for (RecipeEntry entry : recipeEntries) {
                        /**
                         * For each RecipeEntry we have N number of IngredientsEntry & N' number of StepsEntry, simply
                         * each recipe has it's own number of Ingredients & steps.
                         */

                        int recipeId = entry.getRecipeId(); // get the recipe ID of the Recipe

                        // Create an ArrayList of POJOs for Ingredient & Step
                        List<Ingredient> ingredientList = new ArrayList<>();
                        List<Step> stepList = new ArrayList<>();

                        //Iterate over List of StepsEntry POJO and convert it into  List of  Step POJO
                        for (StepsEntry stepsEntry : stepsEntries) {
                            Log.d("Step","RecipeId" + recipeId);
                            if (stepsEntry.getRecipeId() == recipeId) {
                                Step step = new Step(stepsEntry.getId(), stepsEntry.getShortDescription()
                                        , stepsEntry.getDescription()
                                        , stepsEntry.getVideoURL()
                                        , stepsEntry.getThumbnailURL());
                                stepList.add(step);
                            }
                        }

                         //Iterate over List of StepsEntry POJO and convert it into  List of  Step POJO
                        for (IngredientsEntry ingredientsEntry : ingredientsEntries) {

                            if (ingredientsEntry.getRecipeId() == recipeId) {
                                Ingredient ingredient = new Ingredient(ingredientsEntry.getQuantity()
                                        , ingredientsEntry.getMeasure()
                                        , ingredientsEntry.getIngredient());
                                ingredientList.add(ingredient);
                            }
                        }

                        //Converting the RecipeEntry POJO into Recipe POJO
                        Recipe recipe = new Recipe(recipeId, entry.getName(), ingredientList, stepList, entry.getServings(), entry.getImage());
                        recipes.add(recipe);

                        Log.d("Size","Ingre" + ingredientList.size());
                        Log.d("Size","Steps" + stepList.size());
                    }

                    recipeList = recipes;
                    // Set the Adapter using Recipe List
                    setRecipeAdapter(recipes);
                }else {
                    Toast.makeText(context, "Please wait! Data Loading...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClickRecipeCard(View view, int clickedMoviePosition) {
        Toast.makeText(context, recipeList.get(clickedMoviePosition).getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context,RecipeActivity.class);
        intent.putExtra("recipe", recipeList.get(clickedMoviePosition));
        intent.putExtra(RecipeActivity.EXTRA_RECIPE_POSITION,clickedMoviePosition);
        intent.putExtra(RecipeActivity.EXTRA_IS_FROM_WIDGET,false);
        intent.putExtra(SHARED_ELEMENT_TRANSITION_EXTRA, ViewCompat.getTransitionName(view));
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                Objects.requireNonNull(getActivity()),
                view,
                ViewCompat.getTransitionName(view));

        startActivity(intent, optionsCompat.toBundle());

    }

    public void setRecipeAdapter(List<Recipe> recipeList) {
        //recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (recipeList.size() > 0) {
            GridLayoutManager layoutManager = new GridLayoutManager(context, 2, 1, false);

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recipeCardAdapter = new RecipeCardAdapter(this, recipeList);
            recyclerView.setAdapter(recipeCardAdapter);
        }else {
            recyclerView.setVisibility(View.GONE);
            linearLayoutNoRecipies.setVisibility(View.VISIBLE);
        }
    }
}
