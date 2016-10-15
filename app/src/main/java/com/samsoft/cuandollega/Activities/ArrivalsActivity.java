package com.samsoft.cuandollega.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.Fragments.arrivalsList;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.extra.FavDialog;
import com.samsoft.cuandollega.extra.getTimeArriveTest;
import com.samsoft.cuandollega.objects.stopsGroup;
import com.samsoft.cuandollega.classes.paradaInfo;
import com.samsoft.cuandollega.paradasinfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ArrivalsActivity extends AppCompatActivity {

    private static final String TAG = "ArrivalsActivity";
    private DataBase db;
    private stopsGroup stops[];
    private ArrayList<paradaInfo> paradas = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrivals);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db =  new DataBase(getApplicationContext());
        Bundle datos = getIntent().getExtras();
        stops = stopsGroup.stringtoStops(datos.getString("Stops"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (paradas == null) {
            paradas = new ArrayList<paradaInfo>();
            buscarParadas();
            runBackground();
        }

        Fragment ff =  getSupportFragmentManager().findFragmentByTag("LIST");
        if (ff == null) {
            arrivalsList f = new arrivalsList();
            f.setParadas(paradas);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, f, "LIST").commit();
        } else {
            arrivalsList f = (arrivalsList) ff;
            f.setParadas(paradas);
        }


        saveStat();


    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (db != null) {
            db.Close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.paradasinfo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id;
        id = item.getItemId();
        if (id == R.id.act_refresh && isOnline()) {
            runBackground();
        } else if (id == R.id.act_add) {
            Intent i = new Intent(this,MainTabActivity.class);
            i.putExtra("Stops",stopsGroup.stopsToString(stops));
            startActivity(i);

        } else if (id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void makeToast(String s) {
        Context context = getApplicationContext();
        CharSequence text = s;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return;
    }

    public void saveStat()
    {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("CuandoLLega", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Reciente",stopsGroup.stopsToString(stops));
        editor.commit();
    }

    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void refreshFragment()
    {
        arrivalsList ff = (arrivalsList) getSupportFragmentManager().findFragmentByTag("LIST");
        if (ff != null) {
            ff.refreshScreen();
        }
    }

    public void starClick(final paradaInfo info) {

        FavDialog d = new FavDialog(ArrivalsActivity.this, db, info.bus_name, info.parada, null);
        d.setListener(new FavDialog.favCallback() {
            @Override
            public void okClick() {
                info.isFavorite = db.chekcFavorito(info.bus_name, info.parada);
                refreshFragment();
            }

            @Override
            public void cancelClick() {

            }
        });
        d.show();

    }

    public void runBackground()
    {
        for(paradaInfo p : paradas) {
            p.arrivos = null;
            p.resultado = null;
            refreshFragment();
            new AskTime(p,getApplicationContext()).execute(0);
        }
    }

    public void buscarParadas()
    {
        for(int j = 0 ; j < stops.length ; j++) {
            Integer idCalle = stops[j].idCalle;
            Integer idInter = stops[j].idInter;
            String Bus = stops[j].Bus;
            Integer idFav = stops[j].idFav;

            JSONArray a;
            if (idFav != 0) {
                a = db.getStopsFromFavorite(idFav);
            } else {
                a = db.getStops(Bus, idCalle, idInter);
                db.addFrequencia(idCalle);
                db.addFrequencia(idInter);
            }

            for (int i = 0; i < a.length(); i++) {
                try {
                    JSONObject o = a.getJSONObject(i);
                    paradaInfo info = new paradaInfo();
                    info.bus_name = o.getString("name");
                    info.destino = o.getString("desc");
                    if (idFav != 0) {
                        info.calle = o.getInt("idCalle");
                        info.inter = o.getInt("idInter");
                    } else {
                        info.calle = idCalle;
                        info.inter = idInter;
                    }
                    info.calle_name = db.getCalleName(info.calle);
                    info.inter_name = db.getCalleName(info.inter);
                    info.parada     = o.getInt("parada");
                    info.isFavorite = db.chekcFavorito(info.bus_name,info.parada );

                    paradas.add(info);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class AskTime extends AsyncTask<Integer, Integer, Boolean> {
        private ArrayList<String> datos;
        private Context contex;
        private String linea;
        private Integer parada;
        private paradaInfo info;
        private getTimeArriveTest times;

        public AskTime(paradaInfo _info, Context c)
        {
            contex = c;
            info = _info;
        }

        @Override
        protected Boolean doInBackground(Integer... nada) {
            try {
                parada = info.parada;
                linea =  info.bus_name;

                times =  new getTimeArriveTest(db.getBusId(linea),parada);
                datos = times.run();
                return !datos.isEmpty();

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("paradaInfo","Resultado: " + result);
            if (result) {
                info.arrivos = datos;
                info.resultado = times.getInfo();
            } else {
                info.arrivos = new ArrayList<String>();
                info.resultado = new JSONArray();
            }

            refreshFragment();

        }
    }

}
