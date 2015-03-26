package com.samsoft.cuandollega;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.samsoft.cuandollega.extra.Action;
import com.samsoft.cuandollega.extra.DialogAccion;
import com.samsoft.cuandollega.extra.FavDialog;
import com.samsoft.cuandollega.extra.SMSAction;
import com.samsoft.cuandollega.extra.getTimeArrive;
import com.samsoft.cuandollega.extra.lunchFavAction;
import com.samsoft.cuandollega.objects.stopsGroup;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class paradasinfo extends ActionBarActivity {
    private int idCalle;
    private int idInter;
    private String Bus;
    private LinearLayout listItems;
    private DataBase db;
    private LayoutInflater inflater;
    private boolean online;
    private String accion;
    private Integer idFav;

    private stopsGroup stops[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle datos = getIntent().getExtras();

        listItems = (LinearLayout) findViewById(R.id.listItems);
        db =  new DataBase(getApplicationContext());
        stops = stopsGroup.stringtoStops(datos.getString("Stops"));

        online = isOnline();
        if (!online) {
            View v = findViewById(R.id.msgLay);
            ExpandAnimation.expand(v,100,1400);
        }
        ShowParadas();
        saveStat();
    }

    public void saveStat()
    {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("CuandoLLega", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Reciente",stopsGroup.stopsToString(stops));
        editor.commit();
    }

    public void ShowParadas()
    {
        for(int j = 0;j<stops.length;j++) {
            idCalle = stops[j].idCalle;
            idInter = stops[j].idInter;
            Bus = stops[j].Bus;
            idFav = stops[j].idFav;

            JSONArray a;
            if (idFav != 0) a = db.getStopsFromFavorite(idFav);
            else {
                a = db.getStops(Bus, idCalle, idInter);
                db.addFrequencia(idCalle);
                db.addFrequencia(idInter);
            }

            for (int i = 0; i < a.length(); i++) {
                try {
                    JSONObject o = a.getJSONObject(i);
                    View v = inflater.inflate(R.layout.waitingrow, null);
                    TextView bus = (TextView) v.findViewById(R.id.txtBus);
                    TextView dest = (TextView) v.findViewById(R.id.txtDest);
                    bus.setText(o.getString("name"));
                    dest.setText(o.getString("desc"));

                    if (idFav != 0) {
                        idCalle = o.getInt("idCalle");
                        idInter = o.getInt("idInter");
                    }
                    String txtcalle = db.getCalleName(idCalle);
                    String txtinter = db.getCalleName(idInter);
                    TextView lugar = (TextView) v.findViewById(R.id.txtLugar);
                    lugar.setVisibility(View.VISIBLE);
                    lugar.setText(txtcalle + " Y " + txtinter);

                    if (online) {
                        AskTime ask = new AskTime(v, getApplicationContext());
                        ask.execute(o);
                    } else {
                        v = putOflineStop(v,o);
                    }
                    listItems.addView(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public View putOflineStop(View v , JSONObject o)
    {
        try {
            ProgressBar bar = (ProgressBar) v.findViewById(R.id.waitingbar);
            ImageView img = (ImageView) v.findViewById(R.id.actionIcon);
            bar.setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);
            img.setImageDrawable(getResources().getDrawable(R.drawable.ic_sms));
            String mensaje = "TUP " + o.getString("parada") + " " + o.getString("name");
            img.setTag(mensaje);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String m = (String) view.getTag();
                    Action ac = new SMSAction(m);
                    new DialogAccion(paradasinfo.this, "Enviar SMS", "Quieres enviar un SMS?", "Enviar", "Cancelar", ac).Show();
                }
            });
        } catch (Exception e) { e.printStackTrace();}
        return v;
    }

    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
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
            listItems.removeAllViews();
            ShowParadas();
        } else if (id == R.id.act_add) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.buttondialog);
            dialog.setTitle("Agregar parada desde...");

            // set the custom dialog components - text, image and button
            Button bt1 = (Button) dialog.findViewById(R.id.bt1);
            Button bt2 = (Button) dialog.findViewById(R.id.bt2);
            Button bt3 = (Button) dialog.findViewById(R.id.bt3);
            bt1.setOnClickListener(new View.OnClickListener() { // Por calle
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent i = new Intent(paradasinfo.this, calleSearch.class);
                    i.putExtra("calle",0);
                    i.putExtra("colectivos","");
                    i.putExtra("accion","street");
                    i.putExtra("Stops",stopsGroup.stopsToString(stops));
                    startActivity(i);
                }
            });

            bt2.setOnClickListener(new View.OnClickListener() { // Por Colectivo
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent i = new Intent(paradasinfo.this, colectivoSearch.class);
                    i.putExtra("calle",0);
                    i.putExtra("interseccion",0);
                    i.putExtra("accion","bus");
                    i.putExtra("Stops",stopsGroup.stopsToString(stops));
                    startActivity(i);
                }
            });

            bt3.setOnClickListener(new View.OnClickListener() { // Por Marcador
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent i = new Intent(paradasinfo.this, favoriteScreen.class);
                    i.putExtra("Stops",stopsGroup.stopsToString(stops));
                    i.putExtra("NoAdd",true);
                    startActivity(i);
                }
            });

            dialog.show();




        } else if (id == android.R.id.home) {
            //super.onBackPressed();
            Intent intent = new Intent(paradasinfo.this, CLMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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


    private class AskTime extends AsyncTask<JSONObject, Integer, Boolean> {
        private View v;
        private ArrayList<String> datos;
        private Context contex;
        private String linea;
        private Integer parada;
        public AskTime(View vv,Context c)
        {
            contex = c;
            v = vv;
        }

        @Override
        protected Boolean doInBackground(JSONObject... stops) {
            JSONObject stop = stops[0];
            try {
                parada = stop.getInt("parada");
                linea = stop.getString("name");
                datos = new getTimeArrive(linea,parada).run();
                if (datos.isEmpty()) return  false;
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            return;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                LinearLayout list = (LinearLayout) v;
                for (int i = 0; i < datos.size(); i++) {
                    TextView t = new TextView(contex);
                    t.setText(datos.get(i));
                    t.setTextColor(Color.BLACK);
                    list.addView(t);
                }

                ProgressBar bar = (ProgressBar) list.findViewById(R.id.waitingbar);
                ImageView img = (ImageView) list.findViewById(R.id.actionIcon);
                bar.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);

                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (db.getFavoritos().length() > 0) {
                            ImageView img = (ImageView) view;
                            FavDialog d = new FavDialog(paradasinfo.this, db, linea, parada,img);
                            d.Show();
                        }else {
                            Action ac =  new lunchFavAction(paradasinfo.this);
                            DialogAccion da = new DialogAccion(paradasinfo.this,"No hay etiquetas","Quieres crear una nueva?","Crear","Cancelar",ac);
                            da.Show();
                        }
                    }
                });

                boolean fav = db.chekcFavorito(linea,parada);
                if (fav) img.setImageDrawable(getResources().getDrawable(R.drawable.starfull));
                else img.setImageDrawable(getResources().getDrawable(R.drawable.star_empty));
            }
            return;
        }
    }

}
