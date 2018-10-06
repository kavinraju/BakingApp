package skr_developer.bakingapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import skr_developer.bakingapp.Adapter.RecipeCardAdapter;
import skr_developer.bakingapp.Model.Recipe;
import skr_developer.bakingapp.R;
import skr_developer.bakingapp.RecipeActivity;
import skr_developer.bakingapp.Retrofit.ApiClient;
import skr_developer.bakingapp.Retrofit.ApiService;

public class RecipeListFragment extends Fragment implements  RecipeCardAdapter.OnClickRecipeCardListener {

    //Keys used for savedInstanceState
    public static String SHARED_ELEMENT_TRANSITION_EXTRA = "sharedElementTransition";
    public static String RECIPE_LIST_BUNDLE_KEY = "recipeList";

    private ApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();

    private List<Recipe> recipeList = new ArrayList<>();
    private RecipeCardAdapter recipeCardAdapter;

    private Context context;

    @BindView(R.id.recyclerview_home)
    RecyclerView recyclerView;
    @BindView(R.id.linear_layout_no_fav_recipies)
    LinearLayout linearLayoutNoRecipies;

    //UI Testing
    @VisibleForTesting
    private boolean isInBackgroundProcess;
    @VisibleForTesting
    private UITestListener uiTestListener;

    @VisibleForTesting
    public interface UITestListener{
        public void onBackgroundTaskComplete();
        public void onBackgroundTaskDismissed();
    }

    public RecipeListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list_home,container,false);
        ButterKnife.bind(this,view);
        context = view.getContext();
        linearLayoutNoRecipies.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {

            //Gets Retrofit instance using getClient method and creates and instance for ApiService.
            apiService = ApiClient.getClient(context).create(ApiService.class);
            isInBackgroundProcess = true;
            notifyListener(uiTestListener);
            disposable.add(
                    apiService.fetchAllRecipes()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<Recipe>>() {
                                @Override
                                public void onSuccess(List<Recipe> recipes) {
                                    Log.d("Size", String.valueOf(recipes.size()));
                                    recipeList = recipes;

                                    isInBackgroundProcess = false;
                                    setRecipeAdapter(recipes);
                                    notifyListener(uiTestListener);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e("error", "onError: " + e.getMessage());
                                }
                            })
            );
        }else {

            recipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST_BUNDLE_KEY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recipeList != null){
            setRecipeAdapter(recipeList);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE_LIST_BUNDLE_KEY, (ArrayList<? extends Parcelable>) recipeList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
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
        }
    }

    @VisibleForTesting
    public boolean isInBackgroundProcess() {
        return isInBackgroundProcess;
    }

    @VisibleForTesting
    public void setUITestListener(UITestListener uiTestListener){
        this.uiTestListener = uiTestListener;
    }

    @VisibleForTesting
    private void notifyListener(UITestListener listener) {
        if (listener == null){
            return;
        }
        if (isInBackgroundProcess){
            listener.onBackgroundTaskComplete();
        }
        else {
            listener.onBackgroundTaskDismissed();
        }
    }
}

