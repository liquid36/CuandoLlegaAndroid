package com.samsoft.cuandollega;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;


public class colectivoSearch extends ActionBarActivity {
    private LinearLayout listItems;
    private DataBase db;
    private LayoutInflater inflater;
    private int idCalle;
    private int idInter;
    private String accion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle datos = getIntent().getExtras();

        listItems = (LinearLayout) findViewById(R.id.listItems);
        db =  new DataBase(getApplicationContext());

        idCalle =  datos.getInt("calle");
        idInter = datos.getInt("interseccion");
        accion = datos.getString("accion");

        ShowItems();
    }


    public void ShowItems() {
        listItems.removeAllViews();
        JSONArray a;
        if (idCalle == 0) a = db.getAllBuses();
        else a = db.busInStop(idCalle,idInter);
        Log.d("colectivoSearch", "Tamaño = " + a.length());
        AddSimpleRows(a);
    }

    public void AddSimpleRows(JSONArray a)
    {
        for(int i = 0;i < a.length();i++)  {
            try {
                JSONObject o = a.getJSONObject(i);
                View v = inflater.inflate(R.layout.rowsimple, null);
                TextView t = (TextView) v.findViewById(R.id.label);
                t.setText(o.getString("name"));
                t.setTag(o);
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONObject o = (JSONObject) view.getTag();
                        if (accion == "bus") { // elegir Calles ahora
                            try {
                                Intent i = new Intent(colectivoSearch.this, calleSearch.class);
                                i.putExtra("calle",0);
                                i.putExtra("colectivos",o.getString("name"));
                                i.putExtra("accion",accion);
                                startActivity(i);
                            } catch (Exception e) {e.printStackTrace();}
                        } else { // Elegir paradas

                        }

                    }
                });

                listItems.addView(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.colectivo_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
