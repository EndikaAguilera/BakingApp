package com.thisobeystudio.bakingapp.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.thisobeystudio.bakingapp.R;
import com.thisobeystudio.bakingapp.adapters.SectionsPagerAdapter;
import com.thisobeystudio.bakingapp.base.BaseApp;
import com.thisobeystudio.bakingapp.models.Recipe;

/**
 * Created by thisobeystudio on 7/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class RecipeDetailActivity extends AppCompatActivity {

    // tag for debug
    private final String TAG = RecipeDetailActivity.class.getSimpleName();

    // selected recipe
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null && getIntent() != null
                && getIntent().hasExtra(BaseApp.INTENT_EXTRA_RECIPE)) {

            mRecipe = getIntent().getParcelableExtra(BaseApp.INTENT_EXTRA_RECIPE);

        } else if (savedInstanceState != null
                && savedInstanceState.containsKey(BaseApp.INTENT_EXTRA_RECIPE)) {

            mRecipe = savedInstanceState.getParcelable(BaseApp.INTENT_EXTRA_RECIPE);

        } else {

            Log.e(TAG, getString(R.string.error_retrieving));

        }

        if (mRecipe != null) {

            setTitle(mRecipe.getRecipeName());

            setupPager();

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mRecipe != null) {
            outState.putParcelable(BaseApp.INTENT_EXTRA_RECIPE, mRecipe);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         /*
         * Normally, calling setDisplayHomeAsUpEnabled(true) (we do so in onCreate here) as well as
         * declaring the parent activity in the AndroidManifest is all that is required to get the
         * up button working properly. However, in this case, we want to navigate to the previous
         * screen the user came from when the up button was clicked, rather than a single
         * designated Activity in the Manifest.
         *
         * We use the up button's ID (android.R.id.home) to listen for when the up button is
         * clicked and then call onBackPressed to navigate to the previous Activity when this
         * happens.
         */

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //finish();
                return true;

            case R.id.widget_menu_action:
                if (mRecipe != null) {
                    BaseApp.updateWidgets(RecipeDetailActivity.this, mRecipe);
                } else {
                    BaseApp.myToast(RecipeDetailActivity.this,
                            getString(R.string.add_to_widget_recipe_error));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * setup ViewPager
     */
    private void setupPager() {

        // pager adapter
        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager(),
                        mRecipe.getRecipeIngredients(),
                        mRecipe.getRecipeSteps());

        // view pager
        ViewPager mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // tab layout
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

}
