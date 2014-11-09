package com.samsoft.cuandollega.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.extra.Action;
import com.samsoft.cuandollega.extra.DialogAccion;
import com.samsoft.cuandollega.extra.FavAction;
import com.samsoft.cuandollega.paradasinfo;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The configuration screen for the {@link favoritaW favoritaW} AppWidget.
 */
public class favoritaWConfigureActivity extends Activity {
    private LinearLayout listItems;
    private DataBase db;
    private LayoutInflater inflater;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String PREFS_NAME = "com.samsoft.cuandollega.widgets.favoritaW";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    public favoritaWConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.simple_list);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listItems = (LinearLayout) findViewById(R.id.listItems);
        db =  new DataBase(getApplicationContext());

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }


    public void ShowFavList()
    {
        listItems.removeAllViews();
        JSONArray l = db.getFavoritos();
        Log.d("FAVORITES", "Length de favorites: " + l.length());
        for(int i = 0;i < l.length();i++) {
            try {
                final JSONObject o = l.getJSONObject(i);
                final Integer id = o.getInt("id");
                final String name = o.getString("name");
                View v = inflater.inflate(R.layout.rowsimple, null);
                TextView t = (TextView) v.findViewById(R.id.label);
                t.setText(name);
                v.setTag(id);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Context context = favoritaWConfigureActivity.this;
                        saveTitlePref(context,mAppWidgetId,id);
                        // It is the responsibility of the configuration activity to update the app widget
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        favoritaW.updateAppWidget(context, appWidgetManager, mAppWidgetId);
                        // Make sure we pass back the original appWidgetId
                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                        setResult(RESULT_OK, resultValue);
                        finish();
                    }
                });

                listItems.addView(v);
            } catch (Exception e) {e.printStackTrace();}

        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, int favid) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, favid);
        prefs.commit();
    }

}



