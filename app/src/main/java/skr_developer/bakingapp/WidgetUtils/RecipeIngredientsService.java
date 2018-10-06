package skr_developer.bakingapp.WidgetUtils;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import skr_developer.bakingapp.Database.RecipeDatabase;
import skr_developer.bakingapp.R;

public class RecipeIngredientsService extends IntentService {

    public static final String ACTION_VIEW_INGREDIENTS = "view_ingredients";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *  name - "RecipeIngredientsService" Used to name the worker thread, important only for debugging.
     */

    public RecipeIngredientsService() {
        super("RecipeIngredientsService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        assert intent != null;
        if (intent.getAction().equals(ACTION_VIEW_INGREDIENTS)){
            queryNumberOfRecipes(getApplicationContext());
        }
    }

    private void queryNumberOfRecipes(Context context) {

        RecipeDatabase recipeDatabase = RecipeDatabase.getInstance(context);
        int size = recipeDatabase.recipesDao().getRecipeIdsForWidget().length;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeWidget.class));

        //Notify the collection, here ListView, that Data has changed.
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_recipe_ingredients);

        //Call updateRecipeAppWidgets() so as to update the widgets
        RecipeWidget.updateRecipeAppWidgets(context,appWidgetManager,size,appWidgetIds);
    }

    public static void startQueryingNumberOfRecipes(Context context){
        Intent intent = new Intent(context,RecipeIngredientsService.class);
        intent.setAction(ACTION_VIEW_INGREDIENTS);
        context.startService(intent);
    }

}
