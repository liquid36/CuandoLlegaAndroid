package com.samsoft.cuandollega.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.samsoft.cuandollega.Activities.ArrivalsActivity;
import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.classes.paradaInfo;
import com.samsoft.cuandollega.objects.arrivalsAdapter;

import java.util.ArrayList;

/**
 * Created by sam on 12/10/16.
 */

public class arrivalsList extends Fragment {
    private static final String TAG = "arrivalsListFragment";
    private DataBase db;
    private arrivalsAdapter madapter;
    private arrivalsAdapter.arrivalsAdapterListener mListener;
    private ArrayList<paradaInfo> listado;

    public void setParadas(ArrayList<paradaInfo> list) {listado = list;}

    public arrivalsList() {
        db = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = new DataBase(getActivity().getApplicationContext());

        View v = inflater.inflate(R.layout.list_view, container, false);
        ListView lw = (ListView) v.findViewById(R.id.listView);
        madapter = new arrivalsAdapter(getActivity().getApplicationContext(),new ArrayList<paradaInfo>(),events);
        recalcularAdapter();
        lw.setAdapter(madapter);
        setHasOptionsMenu(true);
        return v;
    }

    public void recalcularAdapter()
    {
        if (madapter != null) {
            madapter.clear();
            for (int i = 0; i < listado.size(); i++)
                madapter.add(listado.get(i));
        }
    }

    public void refreshScreen()
    {
        if (madapter != null) {
            recalcularAdapter();
            madapter.notifyDataSetChanged();
        }
    }


    private arrivalsAdapter.arrivalsAdapterListener events = new arrivalsAdapter.arrivalsAdapterListener() {
        @Override
        public void OnStarClick(Integer position) {
            Log.d(TAG,"cooming soon");
            ((ArrivalsActivity)getActivity()).starClick(listado.get(position));
        }
    };
}
