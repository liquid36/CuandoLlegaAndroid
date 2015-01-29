package com.samsoft.cuandollega;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samsoft.cuandollega.objects.stopsGroup;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        JSONArray a,b = new JSONArray();
        if (idCalle == 0) a = db.getAllBuses();
        else a = db.busInStop(idCalle,idInter);
        if (idCalle != 0) {
            try {
                JSONObject o = new JSONObject();
                o.put("name", " - TODOS - ");
                o.put("id", 0);
                b.put(o);
                Log.d("colectivoSearch", "Pongo todos ----------------------");
                for(int i = 0;i < a.length();i++)  {
                    b.put(a.get(i));
                }
            } catch (Exception e) {}
        } else b = a;

        Log.d("colectivoSearch", "Tamaño = " + b.length());
        AddSimpleRows(b);
    }

    public void AddSimpleRows(JSONArray a)
    {
        for(int i = 0;i < a.length();i++)  {
            try {
                JSONObject o = a.getJSONObject(i);
                View v = inflater.inflate(R.layout.rowsimple, null);
                TextView t = (TextView) v.findViewById(R.id.label);
                t.setText(o.getString("name"));
                v.setTag(o);
                //if (i%2 == 0) v.setBackgroundResource(R.drawable.whitefondo);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TextView vv = (TextView) view;
                        TextView tt = (TextView) view.findViewById(R.id.label);
                        JSONObject o = (JSONObject) view.getTag();
                        if (accion.equals("bus")) { // elegir Calles ahora
                            try {
                                Intent i = new Intent(colectivoSearch.this, calleSearch.class);
                                i.putExtra("calle",0);
                                i.putExtra("colectivos",o.getString("name"));
                                i.putExtra("accion",accion);
                                startActivity(i);
                            } catch (Exception e) {e.printStackTrace();}
                        } else { // Elegir paradas
                            Intent i = new Intent(colectivoSearch.this, paradasinfo.class);
                            String colec = "";
                            if (!tt.getText().equals(" - TODOS - ")) colec = tt.getText().toString();

                            stopsGroup r[] = new stopsGroup[1];
                            r[0] = new stopsGroup(idCalle,idInter,colec,0);
                            i.putExtra("Stops",stopsGroup.stopsToString(r));
                            startActivity(i);
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
        getMenuInflater().inflate(R.menu.colectivo_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            /*Intent intent = new Intent(colectivoSearch.this, CLMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
        }
        return super.onOptionsItemSelected(item);
    }





}
