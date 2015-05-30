package com.samsoft.cuandollega.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.objects.geoAdapter;
import com.samsoft.cuandollega.objects.settingRep;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam on 29/05/15.
 */
public class geoList extends Fragment {
    private DataBase db;
    private geoAdapter mAdapter;
    private geoListListener mListener;
    private Double lat,lng;
    private settingRep settings;
    private TextView txtDist;
    Integer radius;
    public geoList()
    {
        lat = null;
        lng = null;
        radius = 500;
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
        Location l = getLastLocation();
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

    public Location getLastLocation()
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
        return  bestResult;
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

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Ningun metodo de localizacion esta activado. ¿Desea activar alguno?")
                .setCancelable(false)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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
}
