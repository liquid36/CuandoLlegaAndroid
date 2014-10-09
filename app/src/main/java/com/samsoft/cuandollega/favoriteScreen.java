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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.spec.ECField;


public class favoriteScreen extends ActionBarActivity {
    private LinearLayout listItems;
    private DataBase db;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle datos = getIntent().getExtras();
        listItems = (LinearLayout) findViewById(R.id.listItems);
        db =  new DataBase(getApplicationContext());

        ShowFavList();


    }

    public void ShowFavList()
    {
        listItems.removeAllViews();
        JSONArray l = db.getFavoritos();
        Log.d("FAVORITES", "Length de favorites: " + l.length());
        for(int i = 0;i < l.length();i++) {
            try {
                JSONObject o = l.getJSONObject(i);
                View v = inflater.inflate(R.layout.rowsimple, null);
                TextView t = (TextView) v.findViewById(R.id.label);
                t.setText(o.getString("name"));
                v.setTag(o.getInt("id"));
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Integer id = (Integer) view.getTag();
                        Intent i = new Intent(favoriteScreen.this, paradasinfo.class);
                        i.putExtra("calle",0);
                        i.putExtra("interseccion",0);
                        i.putExtra("colectivos","");
                        i.putExtra("favorito", id);
                        i.putExtra("accion","favorite");
                        startActivity(i);
                    }
                });
                listItems.addView(v);
            } catch (Exception e) {e.printStackTrace();}

        }

        View v = inflater.inflate(R.layout.entryrow, null);
        ImageButton btn = (ImageButton) v.findViewById(R.id.btn);
        final EditText texto = (EditText) v.findViewById(R.id.texto);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t = texto.getText().toString();
                db.addFavorito(t);
                ShowFavList();
            }
        });
        listItems.addView(v);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favorite_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
