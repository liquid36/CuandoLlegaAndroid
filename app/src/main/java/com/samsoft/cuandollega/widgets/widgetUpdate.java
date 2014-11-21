package com.samsoft.cuandollega.widgets;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.samsoft.cuandollega.R;

public class widgetUpdate extends Service {
    private static final String PREFS_NAME = "com.samsoft.cuandollega.widgets.favoritaW";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    public widgetUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onStart(Intent intent, int startId)
    {
        Context context = getApplicationContext();
        String command = intent.getAction();
        int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.favorita_w);

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int favID = prefs.getInt(PREF_PREFIX_KEY + appWidgetId,0);
        String name = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_NAME","No Name");

        Intent acti = new Intent(context, adapterList.class);
        acti.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        acti.putExtra("FAVID",favID);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        remoteView.setRemoteAdapter(R.id.listView, acti);


        appWidgetManager.updateAppWidget(appWidgetId, remoteView);

        stopSelf();
        super.onStart(intent, startId);


    }
}
