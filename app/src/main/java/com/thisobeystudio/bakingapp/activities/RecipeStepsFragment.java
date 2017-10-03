package com.thisobeystudio.bakingapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.thisobeystudio.bakingapp.R;
import com.thisobeystudio.bakingapp.base.BaseApp;
import com.thisobeystudio.bakingapp.models.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thisobeystudio on 29/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class RecipeStepsFragment extends Fragment {

    //private static final String TAG = RecipeStepsFragment.class.getSimpleName();

    @SuppressWarnings({"WeakerAccess", "CanBeFinal"})
    @BindView(R.id.player)
    SimpleExoPlayerView mExoPlayer;

    private MenuItem prevMenuItem;
    private MenuItem nextMenuItem;

    @SuppressWarnings({"WeakerAccess", "CanBeFinal"})
    @BindView(R.id.step_pos)
    TextView stepPositionTextView;
    @SuppressWarnings({"WeakerAccess", "CanBeFinal"})
    @BindView(R.id.step_description)
    TextView stepDescriptionTextView;

    private SimpleExoPlayer recipeVideoPlayer;

    private ArrayList<Step> mStepsList = new ArrayList<>();

    private int resumeWindow = C.INDEX_UNSET;
    private long resumePosition = C.TIME_UNSET;
    private boolean isPlaying = false;

    private int mStepSelection = 0;

    private Bitmap placeholderBitmap;

    public static RecipeStepsFragment newInstance(ArrayList<Step> steps) {
        Bundle args = new Bundle();
        RecipeStepsFragment fragment = new RecipeStepsFragment();

        args.putParcelableArrayList(BaseApp.INTENT_EXTRA_RECIPE_STEPS, steps);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recipe_steps, container, false);

        placeholderBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.recipe_placeholder);

        ButterKnife.bind(this, view);

        if (getArguments() != null
                && getArguments().containsKey(BaseApp.INTENT_EXTRA_RECIPE_STEPS)) {

            if (getArguments().containsKey(BaseApp.INTENT_EXTRA_RECIPE_STEP_SELECTION)) {
                mStepSelection = getArguments().getInt(BaseApp.INTENT_EXTRA_RECIPE_STEP_SELECTION, 0);
            }

            mStepsList = new ArrayList<>();
            mStepsList = getArguments().getParcelableArrayList(BaseApp.INTENT_EXTRA_RECIPE_STEPS);

        } else if (savedInstanceState != null
                && savedInstanceState.containsKey(BaseApp.INTENT_EXTRA_RECIPE_STEPS)) {

            if (savedInstanceState.containsKey(BaseApp.INTENT_EXTRA_RECIPE_STEP_SELECTION)) {
                mStepSelection = getArguments().getInt(BaseApp.INTENT_EXTRA_RECIPE_STEP_SELECTION, 0);
            }

            mStepsList = new ArrayList<>();
            mStepsList = savedInstanceState
                    .getParcelableArrayList(BaseApp.INTENT_EXTRA_RECIPE_STEPS);

        }

        BottomNavigationView navigation = view.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // access navigation menu items to handle setEnabled
        Menu menuNav = navigation.getMenu();
        prevMenuItem = menuNav.findItem(R.id.navigation_prev);
        prevMenuItem.setEnabled(false);
        nextMenuItem = menuNav.findItem(R.id.navigation_next);

        initPlayer();

        // update player dimens
        view.post(() -> {

            final int w = view.getMeasuredWidth();
            final int h = view.getMeasuredHeight();

            if (w < h) {
                // Portrait
                mExoPlayer.setMinimumWidth(w);
                mExoPlayer.setMinimumHeight(h / 2);

            } else {

                // Landscape
                navigation.post(() -> {

                    int height = view.getMeasuredWidth() - (navigation.getMeasuredHeight() * 2);
                    mExoPlayer.setMinimumWidth(w);
                    mExoPlayer.setMinimumHeight(height / 2);

                });

            }

        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(BaseApp.INTENT_EXTRA_RECIPE_STEP_SELECTION, mStepSelection);

        super.onSaveInstanceState(outState);
    }

    private void initPlayer() {

        if (mStepSelection >= 0 && mStepSelection < mStepsList.size() && mStepsList != null) {

            // set step position text
            String pos = "Step " + (mStepsList.get(mStepSelection).getStepId() + 1);
            stepPositionTextView.setText(pos);

            // set step description text
            String description = mStepsList.get(mStepSelection).getStepShortDescription();
            stepDescriptionTextView.setText(description);

            if (mStepSelection == 0) {
                prevMenuItem.setEnabled(false);
            } else {
                prevMenuItem.setEnabled(true);
            }

            if (mStepSelection == mStepsList.size() - 1) {
                nextMenuItem.setEnabled(false);
            } else {
                nextMenuItem.setEnabled(true);
            }

            // disable and hide player controller
            mExoPlayer.setUseController(false);
            mExoPlayer.hideController();

            mExoPlayer.setDefaultArtwork(placeholderBitmap);

            // Create a default TrackSelector
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BaseApp.bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            RenderersFactory render = new DefaultRenderersFactory(getContext());

            // Create a default LoadControl
            LoadControl loadControl = new DefaultLoadControl();

            // Create the player
            recipeVideoPlayer = ExoPlayerFactory.newSimpleInstance(render, trackSelector, loadControl);

            mExoPlayer.requestFocus();

            // Bind the player to the view.
            mExoPlayer.setPlayer(recipeVideoPlayer);

            // check for invalid video ur and valid thumbnail
            if (TextUtils.isEmpty(mStepsList.get(mStepSelection).getStepVideoURL()) &&
                    !TextUtils.isEmpty(mStepsList.get(mStepSelection).getStepThumbnailURL())) {

                // disable and hide player controller
                mExoPlayer.setUseController(false);
                mExoPlayer.hideController();

                // set player art work from thumbnail url
                Glide.with(getActivity())
                        .asBitmap()
                        .load(mStepsList.get(mStepSelection).getStepThumbnailURL())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource,
                                                        Transition<? super Bitmap> transition) {

                                // fix thumbnail scale to fit placeholder scale
                                int h = placeholderBitmap.getHeight();
                                int w = placeholderBitmap.getWidth();

                                Bitmap resized = Bitmap.createScaledBitmap(resource, w, h, true);

                                mExoPlayer.setDefaultArtwork(resized);

                            }
                        });

            } else if (TextUtils.isEmpty(mStepsList.get(mStepSelection).getStepVideoURL()) &&
                    TextUtils.isEmpty(mStepsList.get(mStepSelection).getStepThumbnailURL())) {

                // disable and hide player controller
                mExoPlayer.setUseController(false);
                mExoPlayer.hideController();

                mExoPlayer.setDefaultArtwork(placeholderBitmap);

            } else {

                // Set media controller
                mExoPlayer.setUseController(true);
                mExoPlayer.showController();

                mExoPlayer.setVisibility(View.VISIBLE);
                Uri video = Uri.parse(mStepsList.get(mStepSelection).getStepVideoURL());
                DefaultDataSourceFactory dataSourceFactory =
                        new DefaultDataSourceFactory(getContext(),
                                Util.getUserAgent(getContext(), "steps"),
                                null);
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                MediaSource videoSource = new ExtractorMediaSource(
                        video,
                        dataSourceFactory,
                        extractorsFactory,
                        null,
                        null);

                if (resumeWindow != C.INDEX_UNSET && resumePosition != C.TIME_UNSET) {
                    recipeVideoPlayer.seekTo(resumeWindow, resumePosition);
                    recipeVideoPlayer.prepare(videoSource, true, false);
                } else {
                    recipeVideoPlayer.prepare(videoSource, false, false);
                }

                recipeVideoPlayer.setPlayWhenReady(isPlaying);

                recipeVideoPlayer.addListener(new Player.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups,
                                                TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                    }

                    @Override
                    public void onRepeatModeChanged(int repeatMode) {

                    }


                    @Override
                    public void onPlayerError(ExoPlaybackException error) {
                        resetResumePosition();
                    }

                    @Override
                    public void onPositionDiscontinuity() {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }

                });
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (recipeVideoPlayer == null && mStepsList != null) {

            initPlayer();

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        releasePlayer();
    }

    // update player window, position and is playing
    private void updateResumePosition() {
        resumeWindow = recipeVideoPlayer.getCurrentWindowIndex();
        resumePosition = recipeVideoPlayer.isCurrentWindowSeekable()
                ? Math.max(0, recipeVideoPlayer.getCurrentPosition())
                : C.TIME_UNSET;
        isPlaying = recipeVideoPlayer.getPlayWhenReady();
    }


    // reset player window, position and is playing
    private void resetResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
        isPlaying = false;
    }

    // release player
    private void releasePlayer() {
        if (recipeVideoPlayer != null) {
            updateResumePosition();
            recipeVideoPlayer.stop();
            recipeVideoPlayer.release();
            recipeVideoPlayer = null;
        }
    }

    // bottom navigation item selected listener
    @SuppressWarnings("CanBeFinal")
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        switch (item.getItemId()) {
            case R.id.navigation_prev:
                mStepSelection--;
                releasePlayer();
                resetResumePosition();
                initPlayer();
                return true;
            case R.id.navigation_next:
                mStepSelection++;
                releasePlayer();
                resetResumePosition();
                initPlayer();
                return true;
        }
        return false;
    };

}
