package skr_developer.bakingapp.WidgetUtils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import skr_developer.bakingapp.HomeActivity;
import skr_developer.bakingapp.R;
import skr_developer.bakingapp.RecipeActivity;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,int size,
                                int appWidgetId) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        RemoteViews views;
        if (width < 80 || height < 80) {
            views = getSmallSizedRemoteView(context,size);
        }else {
            views = getRecipeIngredientsListRemoteView(context);
        }

      /*  Intent intent = new Intent(context,RecipeIngredientsService.class);
        views.setRemoteAdapter(R.id.lv_recipe_ingredients,intent);

        Intent appIntent = new Intent(context, HomeActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context,0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lv_recipe_ingredients, appPendingIntent);*/


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateRecipeAppWidgets(Context context, AppWidgetManager appWidgetManager, int size,
                                              int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, size, appWidgetId);
        }

    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
       RecipeIngredientsService.startQueryingNumberOfRecipes(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RecipeIngredientsService.startQueryingNumberOfRecipes(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    private static RemoteViews getSmallSizedRemoteView(Context context, int size){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
        Intent intent = new Intent(context, RecipeIngredientsService.class);
        intent.setAction(RecipeIngredientsService.ACTION_VIEW_INGREDIENTS);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setTextViewText(R.id.tv_num, "You have " + String.valueOf(size) + " Recipies as your favorite one.");

        views.setOnClickPendingIntent(R.id.tv_num, pendingIntent);
        return views;
    }

    private static RemoteViews getRecipeIngredientsListRemoteView(Context context){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_listview);

        Intent intent = new Intent(context, RecipeIngredientsListWidgetService.class);
        views.setRemoteAdapter(R.id.lv_recipe_ingredients,intent);

        Intent appIntent = new Intent(context, RecipeActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context,0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lv_recipe_ingredients, appPendingIntent);

        views.setEmptyView(R.id.lv_recipe_ingredients,R.id.rl_emptyView);
        return views;
    }
}

