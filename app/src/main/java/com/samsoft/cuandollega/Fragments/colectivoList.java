package com.samsoft.cuandollega.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.objects.calleAdapter;
import com.samsoft.cuandollega.objects.colectivoAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam on 18/05/15.
 */
public class colectivoList extends Fragment {
    private DataBase db;
    private colectivoAdapter madapter;
    private colectivoListListener mListener;
    private Integer idCalle;
    private Integer idInterseccion;
    private Boolean cl;

    public colectivoList() {
        this.cl = true;
        idCalle = 0;
        idInterseccion = 0;
    }

    public void setAll(Boolean cl) {
        this.cl = !cl;
    }

    public void setCalles(Integer idCalle,Integer idInterseccion)
    {
        this.idCalle = idCalle;
        this.idInterseccion = idInterseccion;
    }

    public void setListener(colectivoListListener listener) {mListener = listener;}

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //db = new DataBase(getActivity().getApplicationContext());
        View v = inflater.inflate(R.layout.list_view, container, false);
        ListView lw = (ListView) v.findViewById(R.id.listView);
        madapter = new colectivoAdapter(getActivity().getApplicationContext(),new ArrayList<JSONObject>(),events);
        madapter.setAll(this.cl);
        recalcularAdapter(idCalle,idInterseccion);
        lw.setAdapter(madapter);
        return v;
    }

    public void recalcularAdapter(Integer idCalle,Integer idInterseccion)
    {
        db = new DataBase(getActivity().getApplicationContext());
        this.idCalle = idCalle; this.idInterseccion = idInterseccion;
        JSONArray arr = new JSONArray();
        JSONObject o = new JSONObject();

        if (idCalle != 0 && idInterseccion != 0) arr = db.busInStop(idCalle,idInterseccion);
        else arr = db.getAllBuses(cl);

        madapter.clear();
        if (idCalle != 0) {
            try {
                o.put("linea", " - TODOS - ");
                o.put("name", " - TODOS - ");
                o.put("id", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            madapter.add(o);
        }
        for(int i = 0; i < arr.length();i++)
            try {madapter.add(arr.getJSONObject(i));} catch (Exception e) {e.printStackTrace();}
        db.Close();
    }

    public void refreshScreen()
    {
        recalcularAdapter(idCalle,idInterseccion);
        madapter.notifyDataSetChanged();
    }


    private colectivoAdapter.colectivoAdapterListener events = new colectivoAdapter.colectivoAdapterListener() {
        @Override
        public void OnItemClick(Integer position) {
            if (mListener != null) {
                JSONObject o = new JSONObject();
                try {
                    String c = madapter.getItem(position).getString("linea");
                    Integer i = madapter.getItem(position).getInt("id");
                    o.put("idCalle",idCalle);
                    o.put("idInter",idInterseccion);
                    o.put("colectivo",c);
                    o.put("idColectivo",i);
                }catch (Exception e) {e.printStackTrace();}
                mListener.OnColectivoClick(o);
            }
        }

        @Override
        public void OnItemLongClick(Integer position) {

        }
    };


    public interface colectivoListListener {
        public void OnColectivoClick(JSONObject o);
    }
}
