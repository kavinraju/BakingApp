package skr_developer.bakingapp.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RecipesDao {

    @Query("SELECT * FROM recipes")
    LiveData<List<RecipeEntry>> loadAllRecipes();

    @Query("SELECT * FROM ingredients")
    LiveData<List<IngredientsEntry>> loadIngredients();

    @Query("SELECT * FROM steps")
    LiveData<List<StepsEntry>> loadSteps();

    @Query("SELECT * FROM recipes WHERE recipeId = :recipeId")
    LiveData<RecipeEntry> loadRecipe(int recipeId);

    @Query("SELECT * FROM ingredients WHERE recipeId = :recipeId")
    LiveData<List<IngredientsEntry>> loadIngredients(int recipeId);

    @Query("SELECT * FROM steps WHERE recipeId = :recipeId")
    LiveData<List<StepsEntry>> loadSteps(int recipeId);


    @Query("SELECT recipeId FROM recipes")
    int[] loadRecipeIDs();

    @Insert
    void insertRecipe(RecipeEntry recipeEntry);

    @Insert
    void insertIngredients(List<IngredientsEntry> ingredientsEntries);

    @Insert
    void insertSteps(List<StepsEntry> stepsEntries);

    @Delete
    void deleteRecipe(RecipeEntry recipeEntry);

    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    void deleteIngredients(int recipeId);

    @Query("DELETE FROM steps WHERE recipeId = :recipeId")
    void deleteSteps(int recipeId);

    // Queries For Widget
    @Query("SELECT * FROM recipes")
    List<RecipeEntry> loadAllRecipesForWidgets();

    @Query("SELECT * FROM ingredients")
    List<IngredientsEntry> loadAllIngredientsForWidget();

    @Query("SELECT * FROM steps")
    List<StepsEntry> loadAllStepsForWidget();

    @Query("SELECT recipeId FROM recipes")
    int[] getRecipeIdsForWidget();

    //For Wigdet to RecipeActivity
    @Query("SELECT * FROM recipes WHERE recipeId = :recipeId")
    RecipeEntry loadRecipeEntry(int recipeId);

    @Query("SELECT * FROM ingredients WHERE recipeId = :recipeId")
    List<IngredientsEntry> loadLisIngredientsEntries(int recipeId);

    @Query("SELECT * FROM steps WHERE recipeId = :recipeId")
    List<StepsEntry> loadListStepsEntry(int recipeId);
}