package com.thisobeystudio.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thisobeystudio.bakingapp.R;
import com.thisobeystudio.bakingapp.adapters.RecipeAdapter;
import com.thisobeystudio.bakingapp.base.BaseApp;
import com.thisobeystudio.bakingapp.idling.SimpleIdlingResource;
import com.thisobeystudio.bakingapp.models.Ingredient;
import com.thisobeystudio.bakingapp.models.Recipe;
import com.thisobeystudio.bakingapp.models.Step;
import com.thisobeystudio.bakingapp.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thisobeystudio.bakingapp.utilities.NetworkUtils.BASE_URL;

/**
 * Created by thisobeystudio on 7/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 * <p>
 * Udacity Android Developer Nanodegree by Google Project - "Baking App"
 * https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801
 */

public class MainActivity extends AppCompatActivity implements RecipeAdapter.Callbacks {

    // tag for debug
    private final String TAG = MainActivity.class.getSimpleName();

    // The Idling Resource which will be null in production.
    // Call initial data getter from onStart or onResume instead of in onCreate
    // to ensure there is enough time to register IdlingResource if the download is done
    // too early (i.e. in onCreate)
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    // recipes array list
    private ArrayList<Recipe> mRecipesArrayList = null;

    // recipe selection starts negative to check if it has been updated on updateWidgets(mSelection)
    private int mSelection = -1;

    // ProgressBar
    @SuppressWarnings({"CanBeFinal", "WeakerAccess"})
    @BindView(R.id.main_progress_bar)
    ProgressBar mProgressBar;

    // The detail container view will be present only in the
    // large-screen layouts (res/values-sw600dp).
    // If this view is present, then the
    // activity should be in two-pane mode.
    private boolean mTwoPane = false;

    // check if data retrieving failed
    private boolean initialLaunchFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Get the IdlingResource instance
        getIdlingResource();

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }

        // disable touch interactions on progress
        mProgressBar.setOnClickListener(null);

        setProgressBarVisibility(View.VISIBLE);

        // check for mTwoPane mode
        if (findViewById(R.id.details_fragment) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-sw600dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

        }

        if (savedInstanceState == null && mRecipesArrayList == null) {

            getRecipesJSON();

        } else if (savedInstanceState != null
                && savedInstanceState.containsKey(BaseApp.INTENT_EXTRA_RECIPES_ARRAY)) {

            mRecipesArrayList = savedInstanceState
                    .getParcelableArrayList(BaseApp.INTENT_EXTRA_RECIPES_ARRAY);

            setupRecyclerView();

            if (savedInstanceState.containsKey(BaseApp.INTENT_EXTRA_RECIPE_SELECTION)) {

                mSelection = savedInstanceState.getInt(BaseApp.INTENT_EXTRA_RECIPE_SELECTION);

                if (mRecipesArrayList != null
                        && mSelection >= 0
                        && mSelection < mRecipesArrayList.size()) {

                    hideChooserHint();

                    Recipe recipe = mRecipesArrayList.get(mSelection);

                    if (recipe != null && mTwoPane) {

                        setTitle(recipe.getRecipeName());

                        RecipeDetailFragment fragment = RecipeDetailFragment
                                .newInstance(mRecipesArrayList.get(mSelection));
                        replaceDetailFragment(fragment);

                    }
                }
            }


        } else {

            // hide progress bar and show error text view
            setProgressBarVisibility(View.INVISIBLE);
            updateErrorTextView(View.VISIBLE);

            Log.e(TAG, "Error retrieving data");
            //BaseApp.myToast(MainActivity.this, getString(R.string.error_retrieving));

            // update initialLaunchFailed
            initialLaunchFailed = true;

            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }
        }

        // hide choose and progressBar if recipes array is not null
        if (mRecipesArrayList != null) {
            setProgressBarVisibility(View.INVISIBLE);
            hideChooserHint();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // try to get recipes data if initialLaunchFailed and recipes array == null
        if (initialLaunchFailed && mRecipesArrayList == null) {
            initialLaunchFailed = false;
            getRecipesJSON();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mSelection != -1) {
            outState.putInt(BaseApp.INTENT_EXTRA_RECIPE_SELECTION, mSelection);
        }

        if (mRecipesArrayList != null) {
            outState.putParcelableArrayList(
                    BaseApp.INTENT_EXTRA_RECIPES_ARRAY, mRecipesArrayList);
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * recycler view callback
     *
     * @param pos click position
     */
    @Override
    public void onRecipeClick(int pos) {

        mSelection = pos;

        if (mTwoPane) {

            if (mRecipesArrayList != null) {

                hideChooserHint();

                Recipe recipe = mRecipesArrayList.get(pos);

                if (recipe != null) {

                    setTitle(recipe.getRecipeName());

                    RecipeDetailFragment fragment
                            = RecipeDetailFragment.newInstance(mRecipesArrayList.get(pos));
                    replaceDetailFragment(fragment);

                } else {
                    Log.e(TAG, "onRecipeClick: RECIPE IS NULL");
                }

            }

        } else {

            if (mRecipesArrayList != null) {

                Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
                intent.putExtra(BaseApp.INTENT_EXTRA_RECIPE, mRecipesArrayList.get(pos));
                startActivity(intent);

            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.widget_menu_action:
                if (mSelection >= 0
                        && mRecipesArrayList != null
                        && mSelection < mRecipesArrayList.size()
                        && mRecipesArrayList.get(mSelection) != null) {
                    BaseApp.updateWidgets(MainActivity.this, mRecipesArrayList.get(mSelection));
                } else {
                    BaseApp.myToast(MainActivity.this,
                            getString(R.string.add_to_widget_recipe_error));
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // show menu item only on mTwoPane mode
        if (mTwoPane) {
            // Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater
            MenuInflater inflater = getMenuInflater();
            // Use the inflater's inflate method to inflate our menu layout to this menu
            inflater.inflate(R.menu.main_menu, menu);
            // Return true so that the menu is displayed in the Toolbar
            return true;
        } else return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showExitAppDialog();
    }

    /**
     * volley json data request
     */
    private void getRecipesJSON() {

        if (NetworkUtils.isInternetAvailable(MainActivity.this)) {

            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            final StringRequest stringJSONArray
                    = new StringRequest(Request.Method.GET, BASE_URL, response -> {

                if (response != null && !response.equals("")) {

                    if (getRecipesData(response)) {

                        Log.d(TAG, "getData = true");

                        setupRecyclerView();

                    } else {

                        Log.e(TAG, "getData = false");

                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(true);
                        }

                    }

                } else {
                    Log.e(TAG, "RESPONSE = NULL");

                    if (mIdlingResource != null) {
                        mIdlingResource.setIdleState(true);
                    }

                }

                setProgressBarVisibility(View.INVISIBLE);

            }, volleyError -> {
                Log.e(TAG, "onErrorResponse = " + volleyError.toString());
                setProgressBarVisibility(View.INVISIBLE);

                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(true);
                }

            }) {
                @Override
                protected Map<String, String> getParams() {
                    //Map<String, String> params = new HashMap<>();
                    //params.put("asunto", asunto);
                    //params.put("mensaje", mensaje);
                    //return params;
                    return new HashMap<>();
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                    return params;
                }
            };

            queue.add(stringJSONArray);

        } else {
            errorDialog(getString(R.string.internet_error_dialog_title),
                    getString(R.string.internet_error_dialog_message));
            Log.e(TAG, "NO AVAILABLE INTERNET CONNECTION");
        }
    }

    /**
     * @param responseData json response data
     * @return true if success and false if error
     */
    private boolean getRecipesData(String responseData) {

        try {

            JSONArray arrayJSON = new JSONArray(responseData);

            if (arrayJSON.length() > 0) {

                mRecipesArrayList = new ArrayList<>();

                for (int i = 0; i < arrayJSON.length(); i++) {
                    JSONObject object = arrayJSON.getJSONObject(i);

                    ArrayList<Ingredient> ingredients = getIngredients(object);
                    ArrayList<Step> steps = getSteps(object);

                    if (ingredients == null || steps == null) {
                        return false;
                    }

                    // recipe images helper
                    // added 4 images manually to drawable folder one for each recipe
                    // since json image param doesn't contains images (image param is empty)
                    int imageId;

                    if (i == 0) {
                        imageId = R.drawable.nutella_pie;
                    } else if (i == 1) {
                        imageId = R.drawable.brownies;
                    } else if (i == 2) {
                        imageId = R.drawable.yellow_cake;
                    } else if (i == 3) {
                        imageId = R.drawable.cheesecake;
                    } else {
                        imageId = 0;
                    }

                    Recipe recipe = new Recipe(object.getInt("id"),
                            object.getString("name"),
                            ingredients,
                            steps,
                            object.getInt("servings"),
                            imageId,
                            object.getString("image"));

                    mRecipesArrayList.add(recipe);

                }

            } else {

                return false;

            }

            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * @param jsonObject json object form recipe json
     * @return array list of ingredients for each recipe
     */
    private ArrayList<Ingredient> getIngredients(JSONObject jsonObject) {

        ArrayList<Ingredient> ingredients = new ArrayList<>();

        try {

            JSONArray ingredientsJsonArray = jsonObject.getJSONArray("ingredients");

            for (int i = 0; i < ingredientsJsonArray.length(); i++) {

                JSONObject object = ingredientsJsonArray.getJSONObject(i);


                Ingredient ingredient = new Ingredient(
                        Float.parseFloat(object.getString("quantity")),
                        object.getString("measure"),
                        object.getString("ingredient"));

                ingredients.add(ingredient);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return ingredients;
        }

        return ingredients;

    }

    /**
     * @param jsonObject json object form recipe json
     * @return array list of steps for each recipe
     */
    private ArrayList<Step> getSteps(JSONObject jsonObject) {

        ArrayList<Step> steps = new ArrayList<>();

        try {

            JSONArray ingredientsJsonArray = jsonObject.getJSONArray("steps");

            for (int i = 0; i < ingredientsJsonArray.length(); i++) {

                JSONObject object = ingredientsJsonArray.getJSONObject(i);

                Step step = new Step(
                        object.getInt("id"),
                        object.getString("shortDescription"),
                        object.getString("description"),
                        object.getString("videoURL"),
                        object.getString("thumbnailURL"));

                steps.add(step);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return steps;
        }

        return steps;

    }

    /**
     * setup recycler view
     */
    private void setupRecyclerView() {

        if (findViewById(R.id.main_activity_recycler_view) != null) {

            RecyclerView recyclerView = findViewById(R.id.main_activity_recycler_view);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(false);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);

            //int margin = (int) getResources().getDimension(R.dimen.card_view_margin);
            //recyclerView.addItemDecoration(new GridSpacingItemDecoration(columns, margin));

            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.setNestedScrollingEnabled(false); // Make scroll smoothly

            // specify an adapter
            RecipeAdapter adapter = new RecipeAdapter(MainActivity.this, mRecipesArrayList);

            // set Adapter CallBacks
            adapter.setCallbacks(this);

            // set recyclerView adapter
            recyclerView.setAdapter(adapter);

            // set recyclerView VISIBLE
            recyclerView.setVisibility(View.VISIBLE);

        }

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }

    }

    /**
     * update ProgressBar visibility
     *
     * @param visibility visibility state invisible/visible/gone
     */
    private void setProgressBarVisibility(int visibility) {

        mProgressBar.setVisibility(visibility);

        if (visibility == View.INVISIBLE) {
            updateErrorTextView(visibility);
        }

    }

    /**
     * update error TextView visibility
     *
     * @param visibility visibility state invisible/visible/gone
     */
    private void updateErrorTextView(int visibility) {

        if (findViewById(R.id.main_error_text_view) != null) {
            TextView errorTextView = findViewById(R.id.main_error_text_view);
            errorTextView.setVisibility(visibility);
        }

    }

    /**
     * replace fragments
     *
     * @param fragment container
     */
    private void replaceDetailFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.details_fragment, fragment)
                .commit();

    }

    /**
     * Hides Recipe chooser hint TextView
     */
    private void hideChooserHint() {

        if (findViewById(R.id.choose_hint) != null) {
            TextView chooseHint = findViewById(R.id.choose_hint);
            chooseHint.setVisibility(View.GONE);
        }

    }

    /**
     * Shows an error dialog
     *
     * @param title   error dialog title
     * @param message error dialog message
     */
    private void errorDialog(String title, String message) {

        updateErrorTextView(View.VISIBLE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton(getString(R.string.ok), null);

        alertDialogBuilder.setNegativeButton(getString(R.string.retry),
                (arg0, arg1) -> getRecipesJSON());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

    }

    /**
     * Shows a dialog when back button is pressed that lets user quit the app
     */
    private void showExitAppDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(getString(R.string.exit_app_dialog_title));
        alertDialogBuilder.setMessage(getString(R.string.exit_app_dialog_message));

        alertDialogBuilder.setPositiveButton(getString(R.string.exit),
                (arg0, arg1) -> MainActivity.super.onBackPressed());

        alertDialogBuilder.setNegativeButton(getString(R.string.no), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

    }
}
