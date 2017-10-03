package com.thisobeystudio.bakingapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.thisobeystudio.bakingapp.R;
import com.thisobeystudio.bakingapp.models.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thisobeystudio on 7/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface Callbacks {
        void onRecipeClick(int pos);
    }

    // onclick callbacks
    private Callbacks mCallbacks;

    // recipes array
    private final ArrayList<Recipe> recipes;

    private final Context context;

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_title)
        TextView recipeName;
        @BindView(R.id.recipe_servings)
        TextView recipeServings;
        @BindView(R.id.recipe_image)
        ImageView recipeImageView;

        RecipeViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }
    }

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_recipe, parent, false);
        return new RecipeViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder viewHolder, int i) {

        if (recipes != null && recipes.get(i) != null) {

            viewHolder.recipeName.setText(recipes.get(i).getRecipeName());
            viewHolder.recipeName.setShadowLayer(5, -1, 3, Color.BLACK);

            String servings = "" + recipes.get(i).getRecipeServings();
            viewHolder.recipeServings.setText(servings);

            String imageUrl = recipes.get(i).getRecipeImageUrl();

            // if recipe image url is empty set helper image resource
            if (TextUtils.isEmpty(imageUrl)) {

                // direct way to set image resource
                //viewHolder.recipeImageView.setImageResource(recipes.get(i).getRecipeImage());

                // using glide >>> set drawable resource image as bitmap
                Glide.with(context)
                        .asBitmap()
                        .load(recipes.get(i).getRecipeImage())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource,
                                                        Transition<? super Bitmap> transition) {
                                Drawable drawable =
                                        new BitmapDrawable(context.getResources(), resource);
                                viewHolder.recipeImageView.setImageDrawable(drawable);
                            }
                        });

            } else {

                //This simple Glide load wont save recyclerView scroll position.
                /*
                Glide.with(context)
                        .load(imageUrl)
                        .into(viewHolder.recipeImageView);
                */

                // using glide >>> set image url resource as bitmap
                Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource,
                                                        Transition<? super Bitmap> transition) {
                                Drawable drawable =
                                        new BitmapDrawable(context.getResources(), resource);
                                viewHolder.recipeImageView.setImageDrawable(drawable);
                            }
                        });

            }


            final int pos = i;

            viewHolder.recipeImageView.setOnClickListener(view -> {
                if (mCallbacks != null) {
                    mCallbacks.onRecipeClick(pos);
                }
            });
        }

    }

    // sets recipe click callback
    public void setCallbacks(Callbacks callbacks) {
        this.mCallbacks = callbacks;
    }

}