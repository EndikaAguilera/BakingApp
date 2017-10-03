package com.thisobeystudio.bakingapp.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thisobeystudio.bakingapp.activities.RecipeStepsFragment;
import com.thisobeystudio.bakingapp.activities.IngredientsFragment;
import com.thisobeystudio.bakingapp.models.Ingredient;
import com.thisobeystudio.bakingapp.models.Step;

import java.util.ArrayList;

/**
 * Created by thisobeystudio on 7/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    // selected recipe ingredients and steps
    private final ArrayList<Ingredient> mIngredients;
    private final ArrayList<Step> mSteps;

    public SectionsPagerAdapter(
            FragmentManager fm,
            @NonNull ArrayList<Ingredient> ingredients,
            @NonNull ArrayList<Step> steps) {
        super(fm);
        this.mIngredients = ingredients;
        this.mSteps = steps;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        switch (position) {
            case 0:
                return IngredientsFragment.newInstance(mIngredients);
            case 1:
                return RecipeStepsFragment.newInstance(mSteps);
            //return TimelineFragment.newInstance(mSteps);
        }

        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "INGREDIENTS";
            case 1:
                return "STEPS";
            default:
                return null;
        }
    }

}