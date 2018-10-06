package skr_developer.bakingapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import skr_developer.bakingapp.Fragments.FavoriteRecipeFragment;
import skr_developer.bakingapp.Fragments.RecipeListFragment;

public class HomeActivity extends AppCompatActivity{

    //Keys for Fragments
    private static String RECIPE_LIST_FRAG = "recipe_home_frag";
    private static String FAV_RECIPE_LIST_FRAG = "fav_recipe_frag";

    private static String BUNDLE_KEY_RECIPE_HOME_FRAG= "recipe_home_frag_bundle";
    private static String BUNDLE_KEY_RECIPE_FAV_FRAG = "recipe_fav_frag_bundle";
    private static String BUNDLE_KEY_IS_FAV = "isFav";

    private static boolean isFav = false;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.img_btn_fav_back)
    ImageButton img_btn_fav_back;
    @BindView(R.id.tv_toolbar_title)
    TextView tv_toolbar_title;

    private FragmentManager fragmentManager;
    private RecipeListFragment recipeListFragment;
    private FavoriteRecipeFragment favoriteRecipeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        updateViews();

        if (savedInstanceState == null) {

            recipeListFragment = new RecipeListFragment();
            favoriteRecipeFragment = new FavoriteRecipeFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.container_home_recipe, favoriteRecipeFragment, FAV_RECIPE_LIST_FRAG)
                    .replace(R.id.container_home_recipe, recipeListFragment, RECIPE_LIST_FRAG)
                    .commit();

        }else {
            isFav = savedInstanceState.getBoolean(BUNDLE_KEY_IS_FAV);
            if (isFav){
                favoriteRecipeFragment = (FavoriteRecipeFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_KEY_RECIPE_FAV_FRAG);
                if (favoriteRecipeFragment == null) {
                    favoriteRecipeFragment = new FavoriteRecipeFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.container_home_recipe, favoriteRecipeFragment, FAV_RECIPE_LIST_FRAG)
                        .commit();
            }else {
                recipeListFragment = (RecipeListFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_KEY_RECIPE_HOME_FRAG);
                if (recipeListFragment == null) {
                    recipeListFragment = new RecipeListFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.container_home_recipe, recipeListFragment, RECIPE_LIST_FRAG)
                        .commit();
            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFav = true;
                favoriteRecipeFragment = new FavoriteRecipeFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container_home_recipe, favoriteRecipeFragment, FAV_RECIPE_LIST_FRAG)
                        .commit();
                updateViews();
            }
        });
    }

    private void updateViews() {
        if (isFav){
            img_btn_fav_back.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            //toolbar.setTitle("Favorite Recipies");
            tv_toolbar_title.setText(R.string.favorite_recipes);
        }else {
            //toolbar.setTitle("Recipes");
            tv_toolbar_title.setText(R.string.recipes);
            img_btn_fav_back.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_KEY_IS_FAV,isFav);
        if (isFav){
            if (favoriteRecipeFragment != null && getSupportFragmentManager().findFragmentByTag(FAV_RECIPE_LIST_FRAG).isAdded()) {
                    getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_RECIPE_FAV_FRAG, favoriteRecipeFragment);
            }
        }else {
            if (recipeListFragment != null && getSupportFragmentManager().findFragmentByTag(RECIPE_LIST_FRAG).isAdded()) {
                    getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_RECIPE_HOME_FRAG, recipeListFragment);
            }
        }
    }

    @OnClick(R.id.img_btn_fav_back)
    public void onFavBackImageButton(View view){
        if (fragmentManager != null){
            isFav = false;
            recipeListFragment = new RecipeListFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_home_recipe, recipeListFragment, RECIPE_LIST_FRAG)
                    .commit();
            updateViews();
        }
    }

   /*
    Snackbar.make(fab, "Loading Error....", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}
