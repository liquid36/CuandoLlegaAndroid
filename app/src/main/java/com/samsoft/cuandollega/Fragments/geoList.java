package com.samsoft.cuandollega.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.objects.LocationHelper;
import com.samsoft.cuandollega.objects.geoAdapter;
import com.samsoft.cuandollega.objects.settingRep;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam on 29/05/15.
 */
public class geoList extends Fragment implements LocationListener{
    private DataBase db;
    private geoAdapter mAdapter;
    private geoListListener mListener;
    private Double lat,lng;
    private settingRep settings;
    private TextView txtDist;
    private Integer precision;
    Integer radius;
    public geoList()
    {
        lat = null;
        lng = null;
        radius = 500;
        precision = 50;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        db = new DataBase(getActivity().getApplicationContext());
        settings = new settingRep(getActivity().getApplicationContext());

        radius = settings.getInteger("radio");
        if (radius == 0) {
            radius = 300;
            settings.putInteger("radio",radius);
        }
        Location l = LocationHelper.getLastLocation(getActivity());
        lat = l.getLatitude();
        lng = l.getLongitude();

        View v = inflater.inflate(R.layout.list_view, container, false);
        ListView lw = (ListView) v.findViewById(R.id.listView);
        mAdapter = new geoAdapter(getActivity().getApplicationContext(),new ArrayList<JSONObject>(),db,events);
        recalcularAdapter();
        lw.setAdapter(mAdapter);

        LinearLayout mView  = (LinearLayout) v.findViewById(R.id.msgDistancia);
        mView.setVisibility(View.VISIBLE);
        txtDist = (TextView) v.findViewById(R.id.labDistancia);
        ImageView minus = (ImageView) v.findViewById(R.id.btnMinus);
        ImageView plus = (ImageView) v.findViewById(R.id.btnPlus);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusClick();
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusClick();
            }
        });
        txtDist.setText(radius + "mts");


        return v;
    }

    public void recalcularAdapter()
    {
        JSONArray arr = db.getClosePoint(lat.toString(),lng.toString(),radius);
        mAdapter.clear();
        for(int i = 0; i < arr.length();i++)
            try {mAdapter.add(arr.getJSONObject(i));} catch (Exception e) {e.printStackTrace();}
    }

    public void refreshScreen()
    {
        recalcularAdapter();
        mAdapter.notifyDataSetChanged();
    }


    public void requestLocation()
    {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            LocationHelper.showGPSDisabledAlertToUser(getActivity());
            return;
        }

        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        lm.requestSingleUpdate(criteria, this, null);

    }

    private MenuItem progressItem;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_geo, menu);
        progressItem = menu.findItem(R.id.act_refresh);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.act_refresh) {
            if (Build.VERSION.SDK_INT > 10) item.setActionView(R.layout.actionview_progress);
            else MenuItemCompat.setActionView(item,R.layout.actionview_progress);
            requestLocation();
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        boolean v = ((ActionBarActivity) getActivity()).getSupportActionBar().getSelectedTab().getPosition() == 0;
        Log.d("calleList", "Happening onPrepareOptionsMenu " + v + "  " );
        menu.findItem(R.id.act_refresh).setVisible(v);
    }

    private geoAdapter.geoAdapterListener events = new geoAdapter.geoAdapterListener() {
        @Override
        public void OnItemClick(Integer position){
            if (mListener != null){
                JSONObject o = mAdapter.getItem(position);
                JSONObject ret = new JSONObject();
                try {
                    ret.put("colectivo", "");
                    ret.put("idCalle", o.getInt("idCalle"));
                    ret.put("idInter", o.getInt("idInter"));
                    mListener.OnGeoClick(ret);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void OnItemLongClick(Integer position) {

        }
    };

    public void minusClick() {
        if (radius > 100) {
            radius -= 100;
            txtDist.setText(radius + "mts");
            settings.putInteger("radio",radius);
            refreshScreen();
        }
    }

    public void plusClick() {
        radius += 100;
        txtDist.setText(radius + "mts");
        settings.putInteger("radio",radius);
        refreshScreen();
    }

    public void makeToast(String s) {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setListener(geoListListener listener){mListener = listener;}

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface geoListListener {
        public void OnGeoClick(JSONObject o);
    }

    @Override
    public void onLocationChanged(Location l) {
        lat = l.getLatitude();
        lng = l.getLongitude();
        precision =  (int) l.getAccuracy();
        refreshScreen();
        makeToast("Ubicacion establecida");
        if (Build.VERSION.SDK_INT > 10) progressItem.setActionView(null);
        else MenuItemCompat.setActionView(progressItem,null);
    }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

}
