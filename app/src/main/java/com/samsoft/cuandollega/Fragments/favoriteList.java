package com.samsoft.cuandollega.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.samsoft.cuandollega.Activities.MainTabActivity;
import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.extra.InputDialog;
import com.samsoft.cuandollega.objects.MainTabAdapter;
import com.samsoft.cuandollega.objects.favoriteAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam on 16/05/15.
 */

public class favoriteList extends Fragment {
    private DataBase db;
    private favoriteListListener mListener;
    private favoriteAdapter madapter;
    private Boolean showAdd;
    public static final String NOT_SHOW_ADD = "NOT_SHOW_ADD";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public favoriteList() {
        showAdd = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("favoriteList","onCreate occurs en favoriteList");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        showAdd = getArguments() == null || !getArguments().containsKey(NOT_SHOW_ADD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view, container, false);
        ListView lw = (ListView) v.findViewById(R.id.listView);
        madapter = new favoriteAdapter(getActivity().getApplicationContext(),new ArrayList<JSONObject>(),events);
        recalcularAdapter();
        lw.setAdapter(madapter);
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favorite_list_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu)
    {
        if (showAdd) {
            menu.findItem(R.id.act_add).setVisible(true);
        } else menu.findItem(R.id.act_add).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.act_add:
                InputDialog dialog = new InputDialog(getActivity(), "Marcadores","Introduzca el nombre del marcador",
                        new InputDialog.inputDialogListener() {
                            @Override
                            public void onAcceptClick(String txt) {
                                if (txt.isEmpty()) {
                                    Toast.makeText(getActivity(),"Campo vacío" , Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                db = new DataBase(getActivity().getApplicationContext());
                                db.addFavorito(txt);
                                db.Close();
                                recalcularAdapter();
                            }

                            @Override
                            public void onCancelClick() {

                            }
                        });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void recalcularAdapter()
    {
        db = new DataBase(getActivity().getApplicationContext());
        if (db != null) {
            JSONArray arr = db.getFavoritos();
            madapter.clear();
            for (int i = 0; i < arr.length(); i++) {
                try {
                    madapter.add(arr.getJSONObject(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        db.Close();

    }

    public void refreshScreen()
    {
        recalcularAdapter();
        madapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //db = new DataBase(activity.getApplicationContext());
        //db = ((MainTabActivity) activity).db;
        try {
            mListener = (favoriteListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private favoriteAdapter.favoriteAdapterListener events = new favoriteAdapter.favoriteAdapterListener() {
        @Override
        public void OnItemClick(Integer position) {
            Log.d("favoriteList","Is working");
            if (null != mListener) {
                mListener.onFavoriteClick((JSONObject) madapter.getItem(position));
            }
        }

        @Override
        public void OnItemLongClick(final Integer position) {
            Log.d("favoriteList","Probando");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Favoritos");
            builder.setMessage("¿Desea borrar de la lista el favorito?");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    JSONObject o = (JSONObject) madapter.getItem(position);
                    try {
                        db = new DataBase(getActivity().getApplicationContext());
                        db.removeFavorito(o.getInt("id"));
                        db.Close();
                        recalcularAdapter();
                        madapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        }
    };
    public interface favoriteListListener {
        public void onFavoriteClick(JSONObject id);
    }

}

