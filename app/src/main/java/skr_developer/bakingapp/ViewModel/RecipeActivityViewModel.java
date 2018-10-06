package skr_developer.bakingapp.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.List;

import skr_developer.bakingapp.Database.IngredientsEntry;
import skr_developer.bakingapp.Database.RecipeDatabase;
import skr_developer.bakingapp.Database.RecipeEntry;
import skr_developer.bakingapp.Database.StepsEntry;

public class RecipeActivityViewModel extends ViewModel {

    private LiveData<RecipeEntry> recipeEntryLiveData;
    private LiveData<List<IngredientsEntry>> ingredientsListLiveData;
    private LiveData<List<StepsEntry>> stepsListLiveData;

    RecipeActivityViewModel(RecipeDatabase recipeDatabase, int recipeID, boolean isFromWidget) {
        super();

        Log.d("RecipeActivityViewModel","Querying for Recipe Entry");

        if (isFromWidget) {

            recipeEntryLiveData = recipeDatabase.recipesDao().loadRecipe(recipeID);
            ingredientsListLiveData = recipeDatabase.recipesDao().loadIngredients(recipeID);
            stepsListLiveData = recipeDatabase.recipesDao().loadSteps(recipeID);
        }else {

            recipeEntryLiveData = recipeDatabase.recipesDao().loadRecipe(recipeID);
        }
    }

    public LiveData<RecipeEntry> getRecipeEntryLiveData() {
        return recipeEntryLiveData;
    }

    public LiveData<List<IngredientsEntry>> getIngredientsListLiveData() {
        return ingredientsListLiveData;
    }

    public LiveData<List<StepsEntry>> getStepsListLiveData() {
        return stepsListLiveData;
    }
}
