package com.thisobeystudio.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.thisobeystudio.bakingapp.R;
import com.thisobeystudio.bakingapp.activities.MainActivity;
import com.thisobeystudio.bakingapp.activities.RecipeDetailActivity;
import com.thisobeystudio.bakingapp.base.BaseApp;
import com.thisobeystudio.bakingapp.models.Recipe;

/**
 * Created by thisobeystudio on 8/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class CollectionAppWidgetProvider extends AppWidgetProvider {

    //private static String TAG = CollectionAppWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        CollectionAppWidgetProvider.updateAllWidgets(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all widgets
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, CollectionAppWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_list_view);
        }

        super.onReceive(context, intent);
    }

    /**
     * static method to update all widgets data this will help to update the widget title
     * does the same as onUpdate when called
     *
     * @param context context
     * @param appWidgetManager widget manager
     * @param appWidgetIds widgets ids
     */
    private static void updateAllWidgets(Context context,
                                         AppWidgetManager appWidgetManager,
                                         int[] appWidgetIds) {

        RemoteViews views = new RemoteViews(

                context.getPackageName(),
                R.layout.collection_widget

        );

        // click event handler for the title, launches the app when the user clicks on title
        Intent titleIntent = new Intent(context, MainActivity.class);
        PendingIntent titlePendingIntent =
                PendingIntent.getActivity(context, 0, titleIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_title_label, titlePendingIntent);

        Recipe recipe = BaseApp.getRecipeFromPreferences(context);
        if (recipe == null) {
            // set app name if recipe is null
            views.setTextViewText(R.id.widget_title_label, "BAKING APP");
        } else {
            // set recipe nanme
            views.setTextViewText(R.id.widget_title_label,
                    BaseApp.getRecipeFromPreferences(context).getRecipeName());
        }

        // set remote adapter
        Intent intent = new Intent(context, MyWidgetRemoteViewsService.class);
        views.setRemoteAdapter(R.id.widget_list_view, intent);

        // template to handle the click listener for each item
        Intent clickIntentTemplate = new Intent(context, RecipeDetailActivity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addParentStack(MainActivity.class)             // set MainActivity as parent on stack
                .addParentStack(RecipeDetailActivity.class)     // set RecipeDetailsActivity as parent on stack
                .addNextIntent(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntentTemplate);

        appWidgetManager.updateAppWidget(appWidgetIds, views);

    }

    /**
     * static method to refresh widgets data form menu buttons
     *
     * @param context context
     */
    public static void sendRefreshBroadcast(Context context) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, CollectionAppWidgetProvider.class);
        mgr.getAppWidgetIds(cn);
        CollectionAppWidgetProvider.updateAllWidgets(context, mgr, mgr.getAppWidgetIds(cn));

        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, CollectionAppWidgetProvider.class));
        context.sendBroadcast(intent);
    }

}
