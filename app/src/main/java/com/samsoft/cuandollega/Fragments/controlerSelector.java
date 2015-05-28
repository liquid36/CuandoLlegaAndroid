package com.samsoft.cuandollega.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.objects.settingRep;
import com.samsoft.cuandollega.objects.stopsGroup;
import com.samsoft.cuandollega.paradasinfo;

import org.json.JSONObject;

/**
 * Created by sam on 21/05/15.
 */
public class controlerSelector extends Fragment implements  actionSelect.actionSelectListener,
                                                            calleList.calleListListener,
                                                            colectivoList.colectivoListListener{
    private static final String TAG = "controlerSelector";
    private static final String MENU_ID = "MENU";
    private static final String CALLE_ID = "STREET";
    private static final String COLECTIVOS_ID = "BUSES";
    private String action;
    private Integer idCalle,idInter;
    private String colectivo;

    public controlerSelector()
    {
        action = MENU_ID;
        idCalle = 0;
        idInter = 0;
        colectivo = "";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        actionSelect actions = new actionSelect();
        actions.setListener(this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.frame, actions).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.controler_selector, container, false);
        return v;
    }


    public void OnActionClick(String action)
    {
        Log.d(TAG,action);
        if (action.equals(actionSelect.CALLE_CLICK)) {
            calleList list = new calleList();
            list.setListener(this);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame, list).commit();
            action = CALLE_ID;
        } else if (action.equals(actionSelect.COLECTIVO_CLICK)) {
            colectivoList list = new colectivoList();
            list.setListener(this);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame, list).commit();
            action = COLECTIVOS_ID;
        } else if (action.equals(actionSelect.RECIENTE_CLICK)) {
            settingRep s = new settingRep(getActivity().getApplicationContext());
            if (!s.getString("Reciente").equals(""))  {
                String sreciente = s.getString("Reciente");
                Intent i = new Intent(getActivity(), paradasinfo.class);
                i.putExtra("Stops",sreciente);
                startActivity(i);
            } else {
                makeToast("No se realizo ninguna consulta");
            }
        }

    }

    public void makeToast(String s) {
        Context context = getActivity().getApplicationContext();
        CharSequence text = s;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void OnCalleClick(JSONObject o)
    {
        try {
            idCalle = o.getInt("idCalle");
            colectivo = o.getString("colectivo");
            idInter = o.getInt("idInter");
            if (idInter == 0) {
                calleList list = new calleList();
                list.setCalle(idCalle);
                list.setColectivo(colectivo);
                list.setListener(this);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.frame, list).commit();
            } else {
                if (colectivo.isEmpty()) {
                    colectivoList list = new colectivoList();
                    list.setListener(this);
                    list.setCalles(idCalle,idInter);
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.frame, list).commit();
                } else {
                    Log.d(TAG,idCalle + " " + idInter + " " + colectivo);
                    Intent i = new Intent(getActivity(), paradasinfo.class);
                    stopsGroup stops [] = new stopsGroup[]{};
                    stopsGroup r[] = stopsGroup.addItem(stops,new stopsGroup(idCalle,idInter,colectivo,0));
                    i.putExtra("Stops",stopsGroup.stopsToString(r));
                    startActivity(i);
                }
            }
        }catch (Exception e) {e.printStackTrace();}
    }

    public void OnColectivoClick(JSONObject o){
        try {
            idCalle = o.getInt("idCalle");
            colectivo = o.getString("colectivo");
            idInter = o.getInt("idInter");
            if (idCalle == 0) {
                calleList list = new calleList();
                list.setListener(this);
                list.setColectivo(colectivo);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.frame, list).commit();
            } else {
                Intent i = new Intent(getActivity(), paradasinfo.class);
                stopsGroup stops [] = new stopsGroup[]{};
                stopsGroup r[] = stopsGroup.addItem(stops,new stopsGroup(idCalle,idInter,colectivo,0));
                i.putExtra("Stops",stopsGroup.stopsToString(r));
                startActivity(i);
            }

        } catch (Exception e) {e.printStackTrace();}
    }

}

