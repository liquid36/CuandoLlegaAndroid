package com.samsoft.cuandollega.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.extra.getTimeArrive;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam on 20/11/14.
 */
public class adapterList extends RemoteViewsService
{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        return (new WidgetRemoteViewsFactory(this.getApplicationContext(), intent));
    }


    public class WidgetRemoteViewsFactory implements RemoteViewsFactory
    {
        private Context context = null;
        private int appWidgetId;
        private int length;
        private int favId;
        private JSONArray ar;
        private DataBase db;

        public WidgetRemoteViewsFactory(Context context, Intent intent)
        {
            this.context = context;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            favId = intent.getIntExtra("FAVID", AppWidgetManager.INVALID_APPWIDGET_ID);
            length = appWidgetId;
            db = new DataBase(context);
            Log.d("AppWidgetId", String.valueOf(appWidgetId));

        }

        private void updateWidgetListView()
        {
            ar = db.getStopsFromFavorite(favId);
            length = ar.length();
        }

        @Override
        public int getCount()
        {
            return length;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public RemoteViews getLoadingView()
        {
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.waitingrow);
            return remoteView;
        }

        @Override
        public RemoteViews getViewAt(int position)
        {
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.waitingrow);
            try {
                JSONObject o = ar.getJSONObject(position);
                remoteView.setTextViewText(R.id.txtBus, o.getString("name"));
                remoteView.setTextViewText(R.id.txtDest, o.getString("desc"));
                Log.d("WidgetCreatingView", o.getString("desc"));
                String txtcalle = db.getCalleName(o.getInt("idCalle"));
                String txtinter = db.getCalleName(o.getInt("idInter"));
                remoteView.setTextViewText(R.id.txtLugar, txtcalle + " Y " + txtinter);
                remoteView.setViewVisibility(R.id.txtLugar, View.VISIBLE);

                Integer parada = o.getInt("parada");
                String linea = o.getString("name");
                ArrayList<String> datos = new getTimeArrive(linea,parada).run();


            } catch (Exception e) {e.printStackTrace();}
            return remoteView;
        }

        @Override
        public int getViewTypeCount()
        {
            // TODO Auto-generated method stub
            return length;
        }

        @Override
        public boolean hasStableIds()
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onCreate()
        {
            // TODO Auto-generated method stub
            updateWidgetListView();
        }

        @Override
        public void onDataSetChanged()
        {
            // TODO Auto-generated method stub
            updateWidgetListView();
        }

        @Override
        public void onDestroy()
        {
            // TODO Auto-generated method stub

        }
    }

}
