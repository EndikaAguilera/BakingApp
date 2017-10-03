package com.thisobeystudio.bakingapp.base;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.gson.Gson;
import com.thisobeystudio.bakingapp.R;
import com.thisobeystudio.bakingapp.models.Recipe;
import com.thisobeystudio.bakingapp.widget.CollectionAppWidgetProvider;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * Created by thisobeystudio on 7/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class BaseApp extends Application {

    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    public static final BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

    // intents extras keys
    public static final String INTENT_EXTRA_RECIPE = "recipe";
    public static final String INTENT_EXTRA_RECIPES_ARRAY = "recipes_array";
    public static final String INTENT_EXTRA_RECIPE_STEPS = "steps";
    public static final String INTENT_EXTRA_RECIPE_STEP_SELECTION = "step_selection";
    public static final String INTENT_EXTRA_RECIPE_SELECTION = "selection";

    // preference key
    private static final String PREFERENCE_KEY = "preference_recipe";

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // adopt from ExoPlayer demo.
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

    }

    /*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // locale changes and so on.
    }
    */

    // global app toast
    private static Toast mToast;

    /**
     * @param context context
     * @param message toast message
     */
    public static void myToast(Context context, String message) {

        // cancel toast if not null
        if (BaseApp.mToast != null) {
            BaseApp.mToast.cancel();
        }

        // make and show toast
        BaseApp.mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        BaseApp.mToast.show();

    }

    /**
     * @param context context
     * @return a recipe from saved jsonString
     */
    public static Recipe getRecipeFromPreferences(Context context) {
        Gson gson = new Gson();
        return gson.fromJson(getPreferences(context), Recipe.class);
    }

    /**
     * @return jsonString that contains recipe data
     */
    private static String getPreferences(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(BaseApp.PREFERENCE_KEY, null);
    }

    /**
     * used to save recipe as stringJson
     *
     * @param context    context
     * @param jsonString selected recipe jsonString
     */
    private static void setPreference(Context context, String jsonString) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(BaseApp.PREFERENCE_KEY, jsonString);
        editor.apply();
    }

    /**
     * @param recipe selected reipe
     * @return recipe as jsonString
     */
    private static String recipeToString(Recipe recipe) {
        Gson gson = new Gson();
        return gson.toJson(recipe);
    }

    /**
     * update widgets data
     *
     * @param context context
     * @param recipe  selected recipe
     */
    public static void updateWidgets(Context context, Recipe recipe) {

        String recipeString = BaseApp.recipeToString(recipe);
        BaseApp.setPreference(context, recipeString);

        if (BaseApp.isWidgetActive(context)) {

            if (recipe != null) {

                CollectionAppWidgetProvider.sendRefreshBroadcast(context);

                // show success toast
                BaseApp.myToast(context, context.getString(R.string.add_to_widget_success));

            }

        } else {

            BaseApp.myToast(context, context.getString(R.string.add_to_widget_error));

        }

    }

    /**
     * check for active widgets
     *
     * @param context context
     * @return true if found any available widget, false if no widgets found
     */
    private static boolean isWidgetActive(Context context) {

        int ids[] = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, CollectionAppWidgetProvider.class));

        return ids.length != 0;

    }

}
