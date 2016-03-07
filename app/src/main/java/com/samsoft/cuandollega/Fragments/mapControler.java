package com.samsoft.cuandollega.Fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.samsoft.cuandollega.Activities.MainTabActivity;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.objects.settingRep;
import com.samsoft.cuandollega.paradasinfo;

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;

/**
 * Created by sam on 26/10/15.
 */

public class mapControler extends Fragment implements  mapActionSelector.actionSelectListener,
                                                       colectivoList.colectivoListListener,
                                                       mapViewer.pointSelectedListener
{
    private static final String TAG = "mapControler";
    private static final String MENU_ID = "MENU";
    private static final String CALLE_ID = "STREET";
    private static final String COLECTIVOS_ID = "BUSES";
    private String action;
    private Integer idCalle,idInter;
    private String colectivo;
    private controlerSelectorListener mLister;

    public mapControler()
    {
        action = MENU_ID;
    }
    public void setListener(controlerSelectorListener listener){mLister = listener;}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Fragment ff = getChildFragmentManager().findFragmentByTag("CONTROLLER");
        if (ff == null) {
            mapActionSelector actions = new mapActionSelector();
            actions.setListener(this);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, actions, "CONTROLLER").commit();
        } else {
            ((mapActionSelector) ff).setListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.controler_selector, container, false);
        return v;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        //mLister = (controlerSelectorListener) activity;
    }

    @Override
    public void onDetach () {super.onDetach(); mLister = null;}


    public void OnActionClick(String action)
    {
        Log.d(TAG, action);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (action.equals(mapActionSelector.RECORRIDO_CLICK)) {
            colectivoList list = new colectivoList();
            list.setAll(true);
            list.setListener(this);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame, list, "CELECTIVO").commit();
            action = COLECTIVOS_ID;
        } else if (action.equals(mapActionSelector.PARADAS_CLICK)) {
            mapViewer list = new mapViewer();
            list.setListener(this);
            Bundle datos = new Bundle();
            datos.putString(mapViewer.ACTION_KEY,mapViewer.PARADAS_ACTION);
            list.setArguments(datos);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame, list,"MAPA").commit();
            ((MainTabActivity)getActivity()).setScrollView(false);
        } else {
            makeToast("No se realizo ninguna consulta");
        }
    }

    public void HomeActionBarClick() {
        FragmentManager fm = getChildFragmentManager();
        while (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }

    }



    public void makeToast(String s) {
        Context context = getActivity().getApplicationContext();
        CharSequence text = s;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void OnColectivoClick(JSONObject o){
        try {
            Integer idColectivo = o.getInt("idColectivo");

            mapViewer list = new mapViewer();
            Bundle datos = new Bundle();
            datos.putString(mapViewer.ACTION_KEY,mapViewer.RECORRIDO_ACTION);
            datos.putInt("idColectivo",idColectivo);
            list.setArguments(datos);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame, list,"MAPA").commit();
            action = COLECTIVOS_ID;

            ((MainTabActivity)getActivity()).setScrollView(false);
                //mLister.allSelect(o);
                /*Intent i = new Intent(getActivity(), paradasinfo.class);
                stopsGroup stops [] = new stopsGroup[]{};
                stopsGroup r[] = stopsGroup.addItem(stops,new stopsGroup(idCalle,idInter,colectivo,0));
                i.putExtra("Stops",stopsGroup.stopsToString(r));
                startActivity(i);*/


        } catch (Exception e) {e.printStackTrace();}
    }

    public void OnPointSelected(Double lat,Double lng, Integer radius) {
        geoList list = new geoList();
        Bundle datos = new Bundle();
        datos.putString(geoList.ACTION_KEY,geoList.FIXED_ACTION);
        datos.putInt(geoList.RADIO_KEY,radius);
        datos.putDouble(geoList.LAT_KEY,lat);
        datos.putDouble(geoList.LNG_KEY,lng);
        list.setArguments(datos);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.frame, list).commit();
        action = COLECTIVOS_ID;

        ((MainTabActivity)getActivity()).setScrollView(false);
    }


    public interface controlerSelectorListener {
        public void allSelect(JSONObject o);
    }
}


