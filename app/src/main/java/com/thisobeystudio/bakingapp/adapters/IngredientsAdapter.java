package com.thisobeystudio.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thisobeystudio.bakingapp.R;
import com.thisobeystudio.bakingapp.models.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thisobeystudio on 7/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class IngredientsAdapter
        extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {

    // selected recipe ingredients array list
    private final ArrayList<Ingredient> ingredients;

    class IngredientsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ingredient_quantity)
        TextView quantity;
        @BindView(R.id.ingredient_ingredient)
        TextView ingredient;

        IngredientsViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public IngredientsAdapter(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public int getItemCount() {
        if (ingredients == null) return 0;
        else return ingredients.size();
    }

    @Override
    public IngredientsAdapter.IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_ingredients, parent, false);
        return new IngredientsAdapter.IngredientsViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final IngredientsAdapter.IngredientsViewHolder viewHolder, int i) {

        // get formatted quantity
        String sQuantity = ingredients.get(i).getQuantity() + " " + ingredients.get(i).getMeasure();

        // set texts
        viewHolder.quantity.setText(sQuantity);
        viewHolder.ingredient.setText(ingredients.get(i).getIngredient());

    }

}