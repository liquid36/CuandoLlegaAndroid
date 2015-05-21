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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam on 18/05/15.
 */
public class colectivoList extends Fragment {
    private DataBase db;
    private calleAdapter madapter;
    private colectivoListListener mListener;
    private Integer idCalle;
    private Integer idInterseccion;

    public colectivoList() {
        idCalle = 0;
        idInterseccion = 0;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        db = new DataBase(activity.getApplicationContext());
        try {
            mListener = (colectivoListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view, container, false);
        ListView lw = (ListView) v.findViewById(R.id.listView);
        madapter = new calleAdapter(getActivity().getApplicationContext(),new ArrayList<JSONObject>(),events);
        recalcularAdapter(idCalle,idInterseccion);
        lw.setAdapter(madapter);
        return v;
    }

    public void recalcularAdapter(Integer idCalle,Integer idInterseccion)
    {
        this.idCalle = idCalle; this.idInterseccion = idInterseccion;
        JSONArray arr = new JSONArray();
        JSONObject o = new JSONObject();
        try {
            o.put("name", " - TODOS - ");
            o.put("id", 0);
            arr.put(o);
        } catch (Exception e) { e.printStackTrace();}

        if (idCalle != 0 && idInterseccion != 0) arr = db.busInStop(idCalle,idInterseccion);
        else arr = db.getAllBuses();

        madapter.clear();
        madapter.add(o);
        for(int i = 0; i < arr.length();i++)
            try {madapter.add(arr.getJSONObject(i));} catch (Exception e) {e.printStackTrace();}

    }

    public void refreshScreen()
    {
        recalcularAdapter(idCalle,idInterseccion);
        madapter.notifyDataSetChanged();
    }


    private calleAdapter.calleAdapterListener events = new calleAdapter.calleAdapterListener() {
        @Override
        public void OnItemClick(Integer position) {
            if (mListener != null) {
                mListener.OnClick(madapter.getItem(position));
            }
        }

        @Override
        public void OnItemLongClick(Integer position) {

        }
    };


    public interface colectivoListListener {
        public void OnClick(JSONObject o);
    }
}