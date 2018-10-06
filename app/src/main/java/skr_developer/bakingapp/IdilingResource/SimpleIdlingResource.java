package skr_developer.bakingapp.IdilingResource;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;
import skr_developer.bakingapp.Fragments.RecipeListFragment;

/*
    This class is used for making IdlingResource instance so as to make Espresso Testing work properly when network call is made.
 */
public class SimpleIdlingResource implements IdlingResource {

    @Nullable
    private volatile ResourceCallback resourceCallback;

    private RecipeListFragment recipeListFragment;
    private RecipeListFragment.UITestListener uiTestListener;

    public SimpleIdlingResource(RecipeListFragment recipeListFragment){

        this.recipeListFragment =  recipeListFragment;

        uiTestListener = new RecipeListFragment.UITestListener() {
            @Override
            public void onBackgroundTaskComplete() {

            }
            @Override
            public void onBackgroundTaskDismissed() {

                if (resourceCallback == null){
                    return ;
                }
                //Called when the resource goes from busy to idle.
                resourceCallback.onTransitionToIdle();
            }
        };

        recipeListFragment.setUITestListener(uiTestListener);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return !recipeListFragment.isInBackgroundProcess();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}
