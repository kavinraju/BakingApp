package skr_developer.bakingapp.WidgetUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import skr_developer.bakingapp.Database.IngredientsEntry;
import skr_developer.bakingapp.Database.RecipeDatabase;
import skr_developer.bakingapp.Database.RecipeEntry;
import skr_developer.bakingapp.Database.StepsEntry;
import skr_developer.bakingapp.Model.Ingredient;
import skr_developer.bakingapp.Model.Recipe;
import skr_developer.bakingapp.Model.Step;
import skr_developer.bakingapp.R;
import skr_developer.bakingapp.RecipeActivity;

/**
 *      This class, extending RemoteViewsService, is for displaying the Ingredients of the recipe in a ListView.
 */
public class RecipeIngredientsListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeIngredientsListRemoteViewFactory(this.getApplicationContext());
    }
}


class RecipeIngredientsListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = RecipeIngredientsListWidgetService.class.getSimpleName();

    private Context context;
    private List<IngredientsEntry> ingredientsEntries;
    private RecipeDatabase recipeDatabase;
    private static int recipeIdOld;

    RecipeIngredientsListRemoteViewFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        //Get the instance of Database
        recipeDatabase = RecipeDatabase.getInstance(context);

        //Get all the Ingredients
        ingredientsEntries = recipeDatabase.recipesDao().loadAllIngredientsForWidget();

    }



    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (ingredientsEntries == null) return 0;

        return ingredientsEntries.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_recipe_ingredient_item);

        if (position > 0){
            recipeIdOld = ingredientsEntries.get(position - 1).getRecipeId();
        }else {
            recipeIdOld = ingredientsEntries.get(position).getRecipeId();
        }

        Log.d(TAG,"recipeIdOld " + String.valueOf(recipeIdOld));

        //Builing string to for the ingredient quantity and measure
        StringBuilder builder = new StringBuilder();
        builder.append(String.valueOf(ingredientsEntries.get(position).getQuantity()))
                .append(" ")
                .append(ingredientsEntries.get(position).getMeasure());

        if (position == 0 ){
            // Make Recipe Name VISISBLE
            remoteViews.setViewVisibility(R.id.tv_recipe_name, View.VISIBLE);
            remoteViews.setTextViewText(R.id.tv_recipe_name, ingredientsEntries.get(position).getRecipeName());
            // Set Ingridient Name & Quantity
            remoteViews.setTextViewText(R.id.tv_recipe_ingredient, ingredientsEntries.get(position).getIngredient());
            remoteViews.setTextViewText(R.id.tv_recipe_ingredient_quantity,builder.toString());

        }else if (recipeIdOld == ingredientsEntries.get(position).getRecipeId()){
            // Make Recipe Name GONE
            remoteViews.setViewVisibility(R.id.tv_recipe_name, View.GONE);
            // Set Ingridient Name & Quantity
            remoteViews.setTextViewText(R.id.tv_recipe_ingredient, ingredientsEntries.get(position).getIngredient());
            remoteViews.setTextViewText(R.id.tv_recipe_ingredient_quantity, builder.toString());

        }else {
            // Make Recipe Name VISISBLE
            remoteViews.setViewVisibility(R.id.tv_recipe_name, View.VISIBLE);
            remoteViews.setTextViewText(R.id.tv_recipe_name, ingredientsEntries.get(position).getRecipeName());
            // Set Ingridient Name & Quantity
            remoteViews.setTextViewText(R.id.tv_recipe_ingredient, ingredientsEntries.get(position).getIngredient());
            remoteViews.setTextViewText(R.id.tv_recipe_ingredient_quantity, builder.toString());
        }

        //Set up fill intent
        Bundle bundle = new Bundle();
        bundle.putInt(RecipeActivity.EXTRA_RECIPE_ID,ingredientsEntries.get(position).getRecipeId());
        bundle.putBoolean(RecipeActivity.EXTRA_IS_FROM_WIDGET,true);
        Intent fillIntent = new Intent();
        fillIntent.putExtras(bundle);
        remoteViews.setOnClickFillInIntent(R.id.linear_layout_ingredients,fillIntent);
        remoteViews.setOnClickFillInIntent(R.id.tv_recipe_name,fillIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
