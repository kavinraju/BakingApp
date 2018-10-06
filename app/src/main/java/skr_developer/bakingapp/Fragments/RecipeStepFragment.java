package skr_developer.bakingapp.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.bakingapp.Model.Step;
import skr_developer.bakingapp.R;

public class RecipeStepFragment extends Fragment {

    //Keys used for savedInstanceState
    private final static String BUNDLE_KEY_RESUME_WINDOW = "resumeWindows";
    private final static String BUNDLE_KEY_RESUME_POSITION = "resumePosition";
    private final static String BUNDLE_KEY_PLAYER_FULLSCREEN = "playerFullscreen";
    private final static String BUNDLE_KEY_PLAYER_ORIENTATION_FULLSCREEN = "playerOrientationFullscreen";
    private static String BUNDLE_KEY_STEP = "step";
    private static String BUNDLE_KEY_GET_PLAY_WHEN_READY = "playWhenReady";

    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private FrameLayout fullScreenButton;
    private ImageView fullScreenIcon;
    private Dialog fullScreenDialog;

    private int resumeWindow;
    private long resumePosition;
    private static boolean isExoPlayerFullscreen = false;
    private static boolean isExoPlayerOrientationFullscreen = false;
    private static boolean playWhenReady = true;
    private String videoUrl;

    private Step step;

    private Context context;

    private static final DefaultBandwidthMeter BANDWIDTH_METER =
            new DefaultBandwidthMeter();

    @BindView(R.id.smipleExoPlayerView)
    SimpleExoPlayerView simpleExoPlayerView;
    @BindView(R.id.tv_step_desp)
    TextView tv_stepDesp;
    
    public RecipeStepFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_step, container,false);
        ButterKnife.bind(this,view);
        context = view.getContext();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null){
            tv_stepDesp.setText(step.getDescription());
            videoUrl = step.getVideoURL();

        }else {
            resumeWindow = savedInstanceState.getInt(BUNDLE_KEY_RESUME_WINDOW);
            resumePosition = savedInstanceState.getLong(BUNDLE_KEY_RESUME_POSITION);
            isExoPlayerFullscreen = savedInstanceState.getBoolean(BUNDLE_KEY_PLAYER_FULLSCREEN);
            isExoPlayerOrientationFullscreen = savedInstanceState.getBoolean(BUNDLE_KEY_PLAYER_ORIENTATION_FULLSCREEN);
            playWhenReady = savedInstanceState.getBoolean(BUNDLE_KEY_GET_PLAY_WHEN_READY);

            step = savedInstanceState.getParcelable(BUNDLE_KEY_STEP);
            tv_stepDesp.setText(step.getDescription());
            if (step.getVideoURL() != null){
                videoUrl = step.getVideoURL();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT > 23){
            initializeAllForPlayer(videoUrl);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT <= 23 || player == null){
            initializeAllForPlayer(videoUrl);
        }
    }

    @Override
    public void onPause() {

        super.onPause();

        if(Build.VERSION.SDK_INT <= 23) {
            if (simpleExoPlayerView != null && player != null) {
                resumeWindow = player.getCurrentWindowIndex();
                resumePosition = Math.max(0, player.getContentPosition());
                playWhenReady = player.getPlayWhenReady();
                releasePlayer();
            }

            if (fullScreenDialog != null)
                fullScreenDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt(BUNDLE_KEY_RESUME_WINDOW, resumeWindow);
        outState.putLong(BUNDLE_KEY_RESUME_POSITION, resumePosition);
        outState.putBoolean(BUNDLE_KEY_PLAYER_FULLSCREEN, isExoPlayerFullscreen);
        outState.putBoolean(BUNDLE_KEY_PLAYER_ORIENTATION_FULLSCREEN, isExoPlayerOrientationFullscreen);
        outState.putParcelable(BUNDLE_KEY_STEP, step);
        outState.putBoolean(BUNDLE_KEY_GET_PLAY_WHEN_READY, playWhenReady);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ){
            isExoPlayerOrientationFullscreen = true;
            Log.d(RecipeStepFragment.class.getSimpleName(),"Landscape");
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            isExoPlayerOrientationFullscreen = false;
           Log.d(RecipeStepFragment.class.getSimpleName(),"Potrait");
        }
    }

    private void initFullscreenDialog() {

        fullScreenDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (isExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {

        ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
        fullScreenDialog.addContentView(simpleExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fullScreenIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_skrink));
        isExoPlayerFullscreen = true;
        fullScreenDialog.show();
    }


    private void closeFullscreenDialog() {

        ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
        ((FrameLayout) getActivity().findViewById(R.id.frameLayout_media)).addView(simpleExoPlayerView);
        simpleExoPlayerView.setMinimumHeight(750);
        isExoPlayerFullscreen = false;
        fullScreenDialog.dismiss();
        fullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fullscreen_expand));
    }


    private void initFullscreenButton() {

        PlaybackControlView controlView = simpleExoPlayerView.findViewById(R.id.exo_controller);
        fullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        fullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }


    private void initializePlayer(String videoUrl) {

        //BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getActivity()), trackSelector, loadControl);

        simpleExoPlayerView.setPlayer(player);

        player.setPlayWhenReady(playWhenReady);

        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        if (haveResumePosition) { // if have Resume Position
            simpleExoPlayerView.getPlayer().seekTo(resumeWindow, resumePosition);

            Log.d("haveResumePosition " + resumeWindow, "true");
        }else {
            Log.d("haveResumePosition", "false");
        }
        initMediaSource(videoUrl);
        player.prepare(mediaSource,true,false);
    }

    public void releasePlayer(){
        if(simpleExoPlayerView != null){
            player.release();
            player =null;
        }
    }
    private void initMediaSource(String videoUrl){
        String streamUrl = "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4";
        String userAgent = Util.getUserAgent(getActivity(), context.getApplicationInfo().packageName);
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, null, httpDataSourceFactory);
        Uri daUri = Uri.parse(videoUrl);
        if(videoUrl.equals("")){
            Toast.makeText(getContext(), "URL not found", Toast.LENGTH_SHORT).show();
        }
        //mediaSource = new HlsMediaSource(daUri, dataSourceFactory, 1, null, null);
        mediaSource = new ExtractorMediaSource(daUri,dataSourceFactory,new DefaultExtractorsFactory(),null,null);
    }

    private void initializeAllForPlayer(String videoUrl){
        initFullscreenDialog();
        initFullscreenButton();
        initializePlayer(videoUrl);

        if (isExoPlayerFullscreen) {
            ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
            fullScreenDialog.addContentView(simpleExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            fullScreenIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_skrink));
            fullScreenDialog.show();
        }

        if (isExoPlayerOrientationFullscreen){
            openFullscreenDialog();
        }else {
            closeFullscreenDialog();
        }
    }
    public void setStep(Step step) {
        this.step = step;
    }
}
