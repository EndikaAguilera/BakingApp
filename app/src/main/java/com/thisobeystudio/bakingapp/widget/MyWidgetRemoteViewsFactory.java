package com.thisobeystudio.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.thisobeystudio.bakingapp.R;
import com.thisobeystudio.bakingapp.base.BaseApp;
import com.thisobeystudio.bakingapp.models.Recipe;

/**
 * Created by thisobeystudio on 8/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    //private Recipe mRecipe;
    private Recipe mRecipe;

    MyWidgetRemoteViewsFactory(Context applicationContext) {
        this.mContext = applicationContext;
        this.mRecipe = BaseApp.getRecipeFromPreferences(mContext);
    }

    @Override
    public void onCreate() {
        this.mRecipe = BaseApp.getRecipeFromPreferences(mContext);
    }

    @Override
    public void onDataSetChanged() {
        this.mRecipe = BaseApp.getRecipeFromPreferences(mContext);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (this.mRecipe == null || this.mRecipe.getRecipeIngredients() == null) return 0;
        else return this.mRecipe.getRecipeIngredients().size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION
                || this.mRecipe == null
                || this.mRecipe.getRecipeIngredients() == null
                || this.mRecipe.getRecipeIngredients().size() == 0
                || position >= this.mRecipe.getRecipeIngredients().size()) {

            return null;
        }

        String ingredients = this.mRecipe.getRecipeIngredients().get(position).getIngredient();
        String quantity = this.mRecipe.getRecipeIngredients().get(position).getQuantity()
                + " " + this.mRecipe.getRecipeIngredients().get(position).getMeasure();

        RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                R.layout.collection_widget_list_item);

        rv.setTextViewText(R.id.widget_ingredient, ingredients);
        rv.setTextViewText(R.id.widget_quantity, quantity);

        // onItemClick fillIntent of specific recipe for each row
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(BaseApp.INTENT_EXTRA_RECIPE, mRecipe);
        rv.setOnClickFillInIntent(R.id.widget_item_container, fillInIntent);

        return rv;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
