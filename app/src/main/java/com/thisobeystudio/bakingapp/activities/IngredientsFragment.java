package com.thisobeystudio.bakingapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thisobeystudio.bakingapp.R;
import com.thisobeystudio.bakingapp.adapters.IngredientsAdapter;
import com.thisobeystudio.bakingapp.models.Ingredient;

import java.util.ArrayList;

/**
 * Created by thisobeystudio on 7/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class IngredientsFragment extends Fragment {

    // tag used for debug logs
    //private final String TAG = getClass().getSimpleName();

    // key used for onSaveInstanceState
    private final static String KEY_INGREDIENTS = "ingredients";

    // array to store recipe ingredients
    private ArrayList<Ingredient> mIngredients = null;

    public IngredientsFragment() {
    }

    /**
     * Returns a new instance of this fragment
     */
    public static IngredientsFragment newInstance(@NonNull ArrayList<Ingredient> ingredients) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(IngredientsFragment.KEY_INGREDIENTS, ingredients);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null
                && getArguments().containsKey(IngredientsFragment.KEY_INGREDIENTS)) {

            mIngredients = new ArrayList<>();
            mIngredients = getArguments()
                    .getParcelableArrayList(IngredientsFragment.KEY_INGREDIENTS);

            setupIngredientsRecyclerView(view);

        } else if (savedInstanceState != null
                && savedInstanceState.containsKey(IngredientsFragment.KEY_INGREDIENTS)) {

            mIngredients = new ArrayList<>();
            mIngredients = savedInstanceState
                    .getParcelableArrayList(IngredientsFragment.KEY_INGREDIENTS);

            setupIngredientsRecyclerView(view);

        } else if (mIngredients != null) {

            setupIngredientsRecyclerView(view);

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mIngredients != null) {
            outState.putParcelableArrayList(IngredientsFragment.KEY_INGREDIENTS, mIngredients);
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * setup recycler view
     *
     * @param v root view
     */
    private void setupIngredientsRecyclerView(View v) {

        // recycler view
        RecyclerView recyclerView = v.findViewById(R.id.ingredients_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        // layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        // set layout manager
        recyclerView.setLayoutManager(mLayoutManager);

        // this makes scroll smoothly
        recyclerView.setNestedScrollingEnabled(false);

        // specify an adapter
        IngredientsAdapter adapter = new IngredientsAdapter(mIngredients);

        // set recyclerView adapter
        recyclerView.setAdapter(adapter);

        // set recyclerView VISIBLE
        recyclerView.setVisibility(View.VISIBLE);

    }

}
