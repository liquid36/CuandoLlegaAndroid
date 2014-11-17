package com.samsoft.cuandollega.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.samsoft.cuandollega.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link favoritaWConfigureActivity favoritaWConfigureActivity}
 */
public class favoritaW extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        //for (int i=0; i<N; i++) {
        //    favoritaWConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
        //}
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        Intent intent = new Intent(context.getApplicationContext(),widgetUpdate.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        context.startService(intent);
        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorita_w);
        //appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


