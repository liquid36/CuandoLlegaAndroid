package com.samsoft.cuandollega.Fragments;

/**
 * Created by sam on 26/10/15.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import com.samsoft.cuandollega.Activities.MainTabActivity;
import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.extra.colectivoDialog;
import com.samsoft.cuandollega.objects.Maps.MarkerWithRadius;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;


public class mapViewer extends Fragment implements MapEventsReceiver , LocationListener {
    public static final String ACTION_KEY = "ACTION";
    public static final String RECORRIDO_ACTION = "RECORRIDO";
    public static final String PARADAS_ACTION = "PARADAS";

    public static final String MYPOSITION_LAT_ID = "MYPOSITION_LAT";
    public static final String MYPOSITION_LNG_ID = "MYPOSITION_LNG";
    public static final String CLICKPOSITION_LAT_ID = "CLICKPOSITION_LAT";
    public static final String CLICKPOSITION_LNG_ID = "CLICKPOSITION_LNG";
    public static final String CENTER_LAT_ID = "CENTER_LAT";
    public static final String CENTER_LNG_ID = "CENTER_LNG";
    public static final String RADIUS_ID = "RADIUS";
    public static final String ZOOM_ID = "ZOOM";

    private MenuItem searchMenuItem;
    private Activity activity;
    private MapView map;
    private IMapController mapCtl;
    private Polyline lines;
    protected Marker mPosition;
    protected MarkerWithRadius mParada;
    private ArrayList<Marker> mPoints;
    private DataBase db;
    private String action;
    private View _view;
    private Paint mPaint;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //if (_view == null) {
            db = new DataBase(getActivity().getApplicationContext());

            Integer pos = ((ActionBarActivity) getActivity()).getSupportActionBar().getSelectedTab().getPosition();
            if (pos == 2) {
                ((MainTabActivity) getActivity()).setScrollView(true);
            }

            //Bitmap fillBMP = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.abc_ic_search);
            //BitmapShader fillBMPshader = new BitmapShader(fillBMP  , Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            //this.mPaint.setShader(fillBMPshader);
            mPoints = new ArrayList<Marker>();
            View v = inflater.inflate(R.layout.map_viewer_fragment, container, false);
            map = (MapView) v.findViewById(R.id.openmapview);
            if (Build.VERSION.SDK_INT >= 11) {
                map.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            map.setTilesScaledToDpi(true);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);
            MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity().getApplicationContext(), this);
            map.getOverlays().add(0, mapEventsOverlay);

            Bundle datos = this.getArguments();
            action = datos.getString(ACTION_KEY);
            GeoPoint p = getLastLocation();
            Integer zoom = 13;
            if (p == null) {
                p = new GeoPoint(-32.939319, -60.661082);
            }

            if (action.equals(RECORRIDO_ACTION)) {
                Integer idColectivo = datos.getInt("idColectivo");
                GetRecorrido searchRecorrido = new GetRecorrido();
                searchRecorrido.execute(idColectivo);

                mPosition = new Marker(map);
                mPosition.setPosition(p);
                mPosition.setDraggable(false);
                mPosition.setIcon(getResources().getDrawable(R.drawable.ic_marker_blue));
                mPosition.setAnchor(0.5f, 1f);
                map.getOverlays().add(mPosition);

            } else if (action.equals(PARADAS_ACTION)) {
                mParada = new MarkerWithRadius(map);
                mParada.setPosition(p);
                mParada.setDraggable(true);
                mParada.setIcon(getResources().getDrawable(R.drawable.ic_location2));
                //mParada.setAnchor(0.5f, 1f);
                mParada.setRadius(500);
                mParada.setOnMarkerDragListener(dragEvents);
                map.getOverlays().add(mParada);
                //recalcularParadas(p);
            }

            // Lectura de viejas condiciones
            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(CLICKPOSITION_LAT_ID)) {
                    mParada.setPosition(new GeoPoint(savedInstanceState.getDouble(CLICKPOSITION_LAT_ID), savedInstanceState.getDouble(CLICKPOSITION_LNG_ID)));
                }
                p = new GeoPoint(savedInstanceState.getDouble(CENTER_LAT_ID), savedInstanceState.getDouble(CENTER_LNG_ID));
                zoom = savedInstanceState.getInt(ZOOM_ID);
            }

            mapCtl = map.getController();
            if (p != null) mapCtl.setCenter(p);
            mapCtl.setZoom(zoom);
            _view = v;
            return v;
        /*} else {
            ViewGroup parent = (ViewGroup) _view.getParent();
            parent.removeView(_view);
            return _view;
        }*/
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //requestPosition();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (action.equals(PARADAS_ACTION)) {
            inflater.inflate(R.menu.calle_search, menu);
        }

        //inflater.inflate(R.menu.map_viewer, menu);

        /*
        searchMenuItem = menu.findItem(R.id.act_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                GeoSearch t = new GeoSearch();
                t.setOnSearchListener(mapViewer.this);
                t.execute(s);
                if (android.os.Build.VERSION.SDK_INT >= 14) searchMenuItem.collapseActionView();
                else MenuItemCompat.collapseActionView(searchMenuItem);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/
    }
/*
    @Override
    public void onSearchEnd(GeoPoint p)
    {
        mapCtl.setCenter(p);
        mapCtl.setZoom(14);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.act_search:
                if (mListener != null) {
                    mListener.OnPointSelected(mParada.getPosition().getLatitude(), mParada.getPosition().getLongitude(), mParada.getRadius());
                }


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putAll(getInfo());
    }

    public void setPoint(Double lat,Double lng,Integer radius)
    {
        /*
        if (mMarker == null) {
            mMarker = new MarkerWithRadius(map);
            mMarker.setPosition(new GeoPoint(lat,lng));
            mMarker.setDraggable(true);
            mMarker.setRadius(radius);
            mMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker_red));
            mMarker.setAnchor(0.5f,1.0f);
            map.getOverlays().add(mMarker);
        } else {
            mMarker.setPosition(new GeoPoint(lat,lng));
        }*/
        mapCtl.animateTo(new GeoPoint(lat,lng));
    }

    public Bundle getInfo()
    {
        Bundle info = new Bundle();
        Log.d("mapViewer","SUCEDE SAVEINSTANCE");
        if (action.equals(PARADAS_ACTION)) {
            info.putDouble(CLICKPOSITION_LAT_ID, mParada.getPosition().getLatitude());
            info.putDouble(CLICKPOSITION_LNG_ID, mParada.getPosition().getLongitude());
            info.putInt(RADIUS_ID,mParada.getRadius());
        }
        info.putDouble(CENTER_LAT_ID, map.getMapCenter().getLatitude());
        info.putDouble(CENTER_LNG_ID, map.getMapCenter().getLongitude());
        info.putInt(ZOOM_ID,map.getZoomLevel());
        return info;
    }

    public void locateClick(View v)
    {
        //requestPosition();
        mapCtl.animateTo(mPosition.getPosition());
    }

    public void requestPosition()
    {
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        //lm.requestSingleUpdate(criteria, this, null);
        lm.requestLocationUpdates(0,0,criteria,this,null);
    }

    public GeoPoint getLastLocation()
    {
        float bestAccuracy = Float.MAX_VALUE;
        long minTime = Long.MIN_VALUE,bestTime = Long.MIN_VALUE;
        Location bestResult = null;
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> matchingProviders = lm.getAllProviders();
        for (String provider: matchingProviders) {
            Location location = lm.getLastKnownLocation(provider);
            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();
                if (time > bestTime ){
                    bestResult = location;
                    bestTime = time;
                }
            }
        }
        if (bestResult != null )
            return new GeoPoint(bestResult);

        return null;
    }


    public void recalcularParadas(GeoPoint p)
    {
        deleteMarker();
        ArrayList<ContentValues> paradas  = db.getNearBuses(Double.toString(p.getLatitude()), Double.toString(p.getLongitude()), mParada.getRadius());

        for (ContentValues e:paradas) {
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(e.getAsDouble("lat"),e.getAsDouble("lng")));
            m.setDraggable(false);
            //m.setIcon(getResources().getDrawable(R.drawable.ic_infowin));
            m.setIcon(writeOnDrawable(R.drawable.ic_infowin,"hola"));
            m.setAnchor(1f, 1f);
            m.setRelatedObject(e);
            m.setOnMarkerClickListener(clickEvents);
            map.getOverlays().add(1,m);
            mPoints.add(m);
        }
        map.invalidate();
    }

    public void deleteMarker()
    {
        for (Marker m:mPoints) {
            map.getOverlays().remove(m);
        }
        mPoints.clear();
    }

    public BitmapDrawable writeOnDrawable(int drawableId, String text){

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, bm.getWidth()/2, bm.getHeight()/2, paint);
        return new BitmapDrawable(bm);
    }

    /*
        MAP EVENTS HANDLER

     */

    @Override public boolean singleTapConfirmedHelper(GeoPoint p) {
        if (mParada != null) {
            mParada.setPosition(p);
            //recalcularParadas(p);
        }
        map.invalidate();
        return false;
    }

    @Override public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    /*
        LocationListener

     */

    @Override
    public void onLocationChanged(Location l) {
        Log.d("mapViewr","Evento Localitation");
        mPosition.setPosition(new GeoPoint(l));
        //mapCtl.animateTo(new GeoPoint(l));
        map.invalidate();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // called when the GPS provider is turned off (user turning off the GPS on the phone)
    }

    @Override
    public void onProviderEnabled(String provider) {
        // called when the GPS provider is turned on (user turning on the GPS on the phone)
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // called when the status of the GPS provider changes
    }


    private Marker.OnMarkerClickListener clickEvents = new Marker.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker, MapView mapView) {
            colectivoDialog d = new colectivoDialog(getActivity(),db,(ContentValues) marker.getRelatedObject());
            d.show();
            return true;
        }
    };

    private MarkerWithRadius.OnMarkerDragListener dragEvents = new MarkerWithRadius.OnMarkerDragListener() {
        @Override
        public void onMarkerRadius(MarkerWithRadius marker) {

        }

        @Override
        public void onMarkerRadiusEnd(MarkerWithRadius marker) {
            //recalcularParadas(marker.getPosition());
        }

        @Override
        public void onMarkerRadiusStart(MarkerWithRadius marker) {

        }
    };


    private class GetRecorrido extends AsyncTask<Integer, Integer, Boolean>  {
        private Polyline recorridoIda;
        private Polyline recorridoVuelta;

        @Override
        protected Boolean doInBackground(Integer... idCole) {
            Integer idColectivo = idCole[0];
            try {
                ArrayList<ContentValues> points = db.getRecorrido(idColectivo,"ida");
                ArrayList<GeoPoint> pointsList = new ArrayList<GeoPoint>();
                recorridoIda = new Polyline(getActivity().getApplicationContext());
                recorridoIda.setColor(Color.argb(100,255,0,0));
                recorridoIda.setWidth(10.0f);
                for(int i = 0; i< points.size();i++) {
                    pointsList.add(new GeoPoint(points.get(i).getAsDouble("lat"),points.get(i).getAsDouble("lon")));
                }
                recorridoIda.setPoints(pointsList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ArrayList<ContentValues> points = db.getRecorrido(idColectivo,"vuelta");
                ArrayList<GeoPoint> pointsList = new ArrayList<GeoPoint>();
                recorridoVuelta = new Polyline(getActivity().getApplicationContext());
                recorridoVuelta.setColor(Color.argb(100,0,0,255));
                recorridoVuelta.setWidth(10.0f);
                for(int i = 0; i< points.size();i++) {
                    pointsList.add(new GeoPoint(points.get(i).getAsDouble("lat"),points.get(i).getAsDouble("lon")));
                }
                recorridoVuelta.setPoints(pointsList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*try {
                JSONArray points = db.getRecorrido(idColectivo,"vuelta");
                ArrayList<GeoPoint> pointsList = new ArrayList<GeoPoint>();
                recorridoVuelta = new Polyline(getActivity().getApplicationContext());
                recorridoVuelta.setColor(Color.argb(100,0,0,255));
                recorridoVuelta.setWidth(10.0f);
                for(int i = 0; i< points.length();i++) {
                    pointsList.add(new GeoPoint(points.getJSONObject(i).getDouble("lat"),points.getJSONObject(i).getDouble("lng")));
                }
                recorridoVuelta.setPoints(pointsList);

            } catch (Exception e) {
                e.printStackTrace();
            }*/


            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            return;
        }

        protected void onPostExecute(Boolean result) {
            map.getOverlays().add(recorridoIda);
            map.getOverlays().add(recorridoVuelta);
            map.invalidate();
            return;
        }

    };


    private pointSelectedListener mListener;

    public void setListener(pointSelectedListener listener) {mListener = listener;}

    public interface pointSelectedListener {
        public void OnPointSelected(Double lat,Double lng, Integer radius);
    }

}


//recorridoOverlay.getPaint().setColor(0xFFFFFFFF);
//recorridoOverlay.getPaint().setStyle(Paint.Style.FILL);
//recorridoOverlay.getPaint().setShader(fillBMPshader);