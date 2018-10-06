package skr_developer.bakingapp.ViewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import skr_developer.bakingapp.Database.RecipeDatabase;

public class RecipeActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private RecipeDatabase recipeDatabase;
    private int recipeID;
    private boolean isFromWidget;

    public RecipeActivityViewModelFactory(RecipeDatabase recipeDatabase, int recipeID, boolean isFromWidget) {
        this.recipeDatabase = recipeDatabase;
        this.recipeID = recipeID;
        this.isFromWidget =isFromWidget;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RecipeActivityViewModel(recipeDatabase,recipeID,isFromWidget);
    }
}
