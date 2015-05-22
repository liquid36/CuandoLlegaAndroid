package com.samsoft.cuandollega.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.objects.calleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam on 17/05/15.
 */
public class calleList extends Fragment {
    private DataBase db;
    private calleAdapter madapter;
    private calleListListener mListener;
    private String mColectivo;
    private String mName;
    private Integer mCalle;


    public calleList() {
        mColectivo = "";
        mName = "";
        mCalle = 0;

    }


    public void setColectivo(String c) {mColectivo = c;}
    public void setCalle(Integer id) {mCalle = id;}

    public void setListener(calleListListener listener){mListener = listener;}

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        db = new DataBase(getActivity().getApplicationContext());
        View v = inflater.inflate(R.layout.list_view, container, false);
        ListView lw = (ListView) v.findViewById(R.id.listView);
        madapter = new calleAdapter(getActivity().getApplicationContext(),new ArrayList<JSONObject>(),events);
        recalcularAdapter(mCalle,mColectivo,mName);
        lw.setAdapter(madapter);

        if (mCalle != 0) {
            String txtcalle = db.getCalleName(mCalle);
            LinearLayout msgCalle = (LinearLayout) v.findViewById(R.id.msgCalle);
            TextView t = (TextView) msgCalle.findViewById(R.id.labCalle);
            t.setText(txtcalle + " Y ...");
            msgCalle.setVisibility(View.VISIBLE);
        }


        return v;
    }

    public void recalcularAdapter(Integer idCalle,String colectivo,String name)
    {
        mCalle = idCalle;
        mColectivo = colectivo;
        mName = name;
        JSONArray arr;
        if (idCalle == 0) arr = db.getCalles(colectivo,name);
        else arr = db.Intersecciones(idCalle,colectivo,name);
        madapter.clear();
        for(int i = 0; i < arr.length();i++)
            try {madapter.add(arr.getJSONObject(i));} catch (Exception e) {e.printStackTrace();}

    }

    public void refreshScreen()
    {
        recalcularAdapter(mCalle,mColectivo,mName);
        madapter.notifyDataSetChanged();
    }


    private calleAdapter.calleAdapterListener events = new calleAdapter.calleAdapterListener() {
        @Override
        public void OnItemClick(Integer position) {
            if (mListener != null) {
                mListener.OnCalleClick(madapter.getItem(position));
            }
        }

        @Override
        public void OnItemLongClick(Integer position) {

        }
    };


    public interface calleListListener {
        public void OnCalleClick(JSONObject o);
    }
}
