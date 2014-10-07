package com.samsoft.cuandollega;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;


public class calleSearch extends ActionBarActivity {
    private LinearLayout listItems;
    private DataBase db;
    private LayoutInflater inflater;
    private int idCalle;
    private String idColectivo = "";
    private String accion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle datos = getIntent().getExtras();

        listItems = (LinearLayout) findViewById(R.id.listItems);
        db =  new DataBase(getApplicationContext());

        idCalle =  datos.getInt("calle",0);
        idColectivo = datos.getString("colectivos");
        accion = datos.getString("accion");
        Log.d("DEBUG","hola " + datos.containsKey("accion") + "  " + accion);
        ShowCalles("");
    }

    public void ShowCalles(String name) {
        listItems.removeAllViews();
        JSONArray a;
        if (idCalle == 0) a = db.getCalles(idColectivo,name);
        else a = db.Intersecciones(idCalle,idColectivo,name);
        Log.d("CuandoLlega", "Tamaño = " + a.length());
        AddSimpleRows(a);
    }

    public void AddSimpleRows(JSONArray a)
    {
        for(int i = 0;i < a.length();i++)  {
            try {
                JSONObject o = a.getJSONObject(i);
                View v = inflater.inflate(R.layout.rowsimple, null);
                TextView t = (TextView) v.findViewById(R.id.label);
                t.setText(o.getString("desc"));
                v.setTag(o);
                if (i%2 == 0) v.setBackgroundResource(R.drawable.backfondo2);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONObject o = (JSONObject) view.getTag();
                        if (idCalle == 0) { // Tengo que seleccionar otra calle
                            Intent i = new Intent(calleSearch.this, calleSearch.class);
                            try {
                                i.putExtra("calle",o.getInt("id"));
                                i.putExtra("colectivos",idColectivo);
                                i.putExtra("accion",accion);
                                startActivity(i);
                            } catch (Exception e) {e.printStackTrace();}
                        } else if (accion.equalsIgnoreCase("street")) { // Tengo que elegir el colectivo
                            Intent i = new Intent(calleSearch.this, colectivoSearch.class);
                            try {
                                i.putExtra("calle",idCalle);
                                i.putExtra("interseccion",o.getInt("id"));
                                i.putExtra("accion",accion);
                                startActivity(i);
                            } catch (Exception e) {e.printStackTrace();}
                        } else { // Ya elegí colectivo y las calles. Tocan las paradas
                            try {
                                Intent i = new Intent(calleSearch.this, paradasinfo.class);
                                i.putExtra("calle", idCalle);
                                i.putExtra("colectivos", idColectivo);
                                i.putExtra("interseccion", o.getInt("id"));
                                i.putExtra("accion", accion);
                                startActivity(i);
                            } catch (Exception e) {e.printStackTrace();}
                        }
                    }
                });

                listItems.addView(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private MenuItem searchMenuItem;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clmain, menu);
        searchMenuItem = menu.findItem(R.id.act_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                if (android.os.Build.VERSION.SDK_INT >= 14) searchMenuItem.collapseActionView();
                else MenuItemCompat.collapseActionView(searchMenuItem);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ShowCalles(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}
