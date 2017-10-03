package com.thisobeystudio.bakingapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thisobeystudio.bakingapp.R;
import com.thisobeystudio.bakingapp.adapters.SectionsPagerAdapter;
import com.thisobeystudio.bakingapp.base.BaseApp;
import com.thisobeystudio.bakingapp.models.Recipe;

/**
 * Created by thisobeystudio on 8/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class RecipeDetailFragment extends Fragment {

    // tag used for debug logs
    private final String TAG = getClass().getSimpleName();

    // selected recipe
    private Recipe mRecipe = null;

    public RecipeDetailFragment() {
    }

    /**
     * Returns a new instance of this fragment
     */
    public static RecipeDetailFragment newInstance(@NonNull Recipe recipe) {
        Bundle args = new Bundle();
        args.putParcelable(BaseApp.INTENT_EXTRA_RECIPE, recipe);

        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mRecipe != null) {
            outState.putParcelable(BaseApp.INTENT_EXTRA_RECIPE, mRecipe);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_recipe_details, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null
                && getArguments().containsKey(BaseApp.INTENT_EXTRA_RECIPE)) {

            mRecipe = getArguments().getParcelable(BaseApp.INTENT_EXTRA_RECIPE);

            setupPager(view);

        } else if (savedInstanceState != null
                && savedInstanceState.containsKey(BaseApp.INTENT_EXTRA_RECIPE)) {

            mRecipe = savedInstanceState.getParcelable(BaseApp.INTENT_EXTRA_RECIPE);

            setupPager(view);

        } else if (mRecipe != null) {

            setupPager(view);

        } else {

            Log.e(TAG, "mRecipe == null");

        }

    }

    /**
     * setup ViewPager
     */
    private void setupPager(View v) {

        // pager adapter
        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(getChildFragmentManager(),
                        mRecipe.getRecipeIngredients(),
                        mRecipe.getRecipeSteps());

        // view pager
        ViewPager mViewPager = v.findViewById(R.id.view_pager);
        mViewPager.setAdapter(null);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // tab layout
        TabLayout mTabLayout = v.findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(null);
        mTabLayout.setupWithViewPager(mViewPager);

    }

}
