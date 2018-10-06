package skr_developer.bakingapp;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import skr_developer.bakingapp.Fragments.RecipeStepFragment;
import skr_developer.bakingapp.Model.Step;

public class RecipeStepActivity extends AppCompatActivity {

    private static String BUNDLE_KEY_RECIPE_STEP_FRAG= "recipe_step_frag_bundle";
    private static String BUNDLE_KEY_RECIPE_STEP_LIST = "recipe_step_list_bundle";
    private static String BUNDLE_KEY_POSITION = "position_bundle";

    private static String RECIPE_STEP_FRAG = "recipe_step_frag";

    private static String videoUrl;
    private List<Step> stepList;
    private static int position;
    private static int totalSteps;

    @BindView(R.id.btn_next_video)
    FloatingActionButton btn_nextVideo;
    @BindView(R.id.btn_previous_video)
    FloatingActionButton btn_previousVideo;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_toolbar_title_reicpe_step)
    TextView tv_toolbar_title_recipe_step;
    @BindView(R.id.img_btn_step_back)
    ImageButton img_btn_step_back;

    private FragmentManager fragmentManager;
    private RecipeStepFragment recipeStepFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);
        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            stepList = bundle.getParcelableArrayList("steps");
            assert stepList != null;
            totalSteps = stepList.size();
            position = bundle.getInt("position");
            setToolBarTittle(position);
            videoUrl = stepList.get(position).getVideoURL();
            if (videoUrl != null) {
                Log.d("StepList","notNull");
            }else {
                Log.d("StepList", "notNull");
            }
            toolbar.setTitle(stepList.get(position).getShortDescription());
            if(position == 0){
                btn_previousVideo.setVisibility(View.INVISIBLE);
            }else if (position == totalSteps-1){
                btn_nextVideo.setVisibility(View.INVISIBLE);
            }
            setSupportActionBar(toolbar);

        }else {
            setSupportActionBar(toolbar);
        }

        if(savedInstanceState == null){
            recipeStepFragment = new RecipeStepFragment();
            recipeStepFragment.setStep(stepList.get(position));
            fragmentManager.beginTransaction()
                    .add(R.id.container_recipe_step, recipeStepFragment, RECIPE_STEP_FRAG)
                    .commit();
        }else {

            stepList = savedInstanceState.getParcelableArrayList(BUNDLE_KEY_RECIPE_STEP_LIST);
            assert stepList != null;
            totalSteps = stepList.size();
            position = savedInstanceState.getInt(BUNDLE_KEY_POSITION);
            setToolBarTittle(position);
            videoUrl = stepList.get(position).getVideoURL();
            recipeStepFragment = (RecipeStepFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_KEY_RECIPE_STEP_FRAG);
            recipeStepFragment.setStep(stepList.get(position));
            if (recipeStepFragment == null) {
                recipeStepFragment = new RecipeStepFragment();
                recipeStepFragment.setStep(stepList.get(position));
            }

            fragmentManager.beginTransaction()
                    .replace(R.id.container_recipe_step, recipeStepFragment, RECIPE_STEP_FRAG)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if ( getSupportFragmentManager().findFragmentByTag(RECIPE_STEP_FRAG).isAdded() ){
            getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_RECIPE_STEP_FRAG, recipeStepFragment);
        }
        outState.putParcelableArrayList(BUNDLE_KEY_RECIPE_STEP_LIST, (ArrayList<? extends Parcelable>) stepList);
        outState.putInt(BUNDLE_KEY_POSITION, position);
    }

    @OnClick(R.id.btn_next_video)
    public void onClickNext(View view){
        // Return if it's a last step
        if (position == totalSteps)return;

            /*tv_stepDesp.setText(stepList.get(position).getDescription());
            initializePlayer(videoUrl);*/
        position++;

        // Make the Previous button visible if the step is 1 and above
        if (position > 0){
            btn_previousVideo.setVisibility(View.VISIBLE);
        }
        // Make the Next button invisible if its last step
        if (position == totalSteps-1){
            btn_nextVideo.setVisibility(View.INVISIBLE);
        }
        // Return if it's last step - so as to avoid infating the frag again
        if (position == totalSteps){
            return;
        }
        setToolBarTittle(position);
        setUpFragment();
        Log.d("x - position ", String.valueOf(position));
        Log.d("x - tot ", String.valueOf(totalSteps));
    }

    @OnClick(R.id.btn_previous_video)
    public void onClickPrevious(View view){
        if (position == -1)return;
        /*tv_stepDesp.setText(stepList.get(position).getDescription());
        videoUrl = stepList.get(position).getVideoURL();
        initializePlayer(videoUrl);*/

        // position increment code
        if (position == totalSteps){
            position-=2;
        }else if(position != 0){
            position--;
        }
        // Make the Previous button visible if its 0th step
        if (position == 0 ){
            btn_previousVideo.setVisibility(View.INVISIBLE);
        }
        // Make the Next button visible if the step less than total num. of steps
        if (position < totalSteps){
            btn_nextVideo.setVisibility(View.VISIBLE);
        }
        setToolBarTittle(position);
        setUpFragment();
        Log.d("xy - position ", String.valueOf(position));
        Log.d("xy- tot ", String.valueOf(totalSteps));

    }

    private void setUpFragment() {
        videoUrl = stepList.get(position).getVideoURL();
        recipeStepFragment = new RecipeStepFragment();
        recipeStepFragment.setStep(stepList.get(position));

        fragmentManager.beginTransaction()
                .replace(R.id.container_recipe_step, recipeStepFragment,RECIPE_STEP_FRAG)
                .commit();
    }

    private void setToolBarTittle(int position){
        tv_toolbar_title_recipe_step.setText(new StringBuilder().append("Step ").append(String.valueOf(position + 1)).toString());
    }

    @OnClick(R.id.img_btn_step_back)
    public void onRecipeStepImageButtonBack(View view) {
        finish();
    }
}
