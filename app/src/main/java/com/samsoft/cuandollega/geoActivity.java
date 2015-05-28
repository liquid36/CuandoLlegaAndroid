package com.samsoft.cuandollega;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.samsoft.cuandollega.extra.DialogAccion;
import com.samsoft.cuandollega.objects.settingRep;
import com.samsoft.cuandollega.objects.stopsGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class geoActivity extends ActionBarActivity implements LocationListener {
    private Integer d = 500;
    private TextView txtDist;
    private Double lat=0.0,lng=0.0;
    private float precision;
    private LayoutInflater inflater;
    private LinearLayout listItems;
    private DataBase db;
    private stopsGroup stops [];
    private String SStops;
    private settingRep settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listItems = (LinearLayout) findViewById(R.id.listItems);
        db = new DataBase(getApplicationContext());
        settings = new settingRep(getApplicationContext());
        d = settings.getInteger("radio");
        if (d == 0) {
            d = 300;
            settings.putInteger("radio",d);
        }

        Boolean b = settings.getBoolean("geoMSG");
        if (!b) {
            new DialogAccion(this,"Cuando Llega Movil",
                    "De forma instantanea se visualiza la ultima ubicacion conocida." +
                    " Cuando la distancia deja de parpadear se logró actualizar su ubicacion y las nuevas paradas se muestran. "
                    ,"Aceptar" ,"" , null).Show();
            settings.putBoolean("geoMSG",true);
        }


        LinearLayout l = (LinearLayout) findViewById(R.id.msgDistancia);
        l.setVisibility(View.VISIBLE);
        txtDist = (TextView) findViewById(R.id.labDistancia);
        ImageView minus = (ImageView) findViewById(R.id.btnMinus);
        ImageView plus = (ImageView) findViewById(R.id.btnPlus);
        txtDist.setText(d + "mts");
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusClick();
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusClick();
            }
        });

        Bundle datos = getIntent().getExtras();
        stops = stopsGroup.stringtoStops(datos.getString("Stops"));
        SStops = datos.getString("Stops");
        pedirUbicacion();
    }

    public Location getLastLocation()
    {
        float bestAccuracy = Float.MAX_VALUE;
        long minTime = Long.MIN_VALUE,bestTime = Long.MIN_VALUE;
        Location bestResult = null;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> matchingProviders = lm.getAllProviders();
        for (String provider: matchingProviders) {
            Location location = lm.getLastKnownLocation(provider);
            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();
                if (time > bestTime ){
                    bestResult = location;
                    bestTime = time;
                }
            }
        }
        return  bestResult;
    }

    public void pedirUbicacion()
    {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            showGPSDisabledAlertToUser();
            return;
        }

        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);


        lm.requestSingleUpdate(criteria, this, null);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        txtDist.startAnimation(anim);

        listItems.removeAllViews();

        Location l = getLastLocation();
        if (l == null) {
            ProgressBar bar = new ProgressBar(getApplicationContext());
            bar.setIndeterminate(true);
            bar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.myprogressbar));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 20, 0, 0);
            listItems.addView(bar, layoutParams);
            Log.d("geoActivity", "Pidiendo ubicacion");
        } else {
            lat = l.getLatitude();
            lng = l.getLongitude();
            precision =   l.getAccuracy();
            rellenarListView();
        }
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Ningun metodo de localizacion esta activado. ¿Desea activar alguno?")
                .setCancelable(false)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void rellenarListView()
    {
        if (lat == 0.0 && lng == 0.0) return;
        listItems.removeAllViews();
        JSONArray arr = db.getClosePoint(lat.toString(),lng.toString(),d + Math.round(precision));
        for(int i = 0;i < arr.length();i++) {
            try {
                final JSONObject o = arr.getJSONObject(i);
                String calle1 = o.getString("name1");
                String calle2 = o.getString("name2");
                String colectivos = db.colectivosEnEsquina(o.getInt("idCalle"),o.getInt("idInter"));
                if (!colectivos.isEmpty()) {
                    View v = inflater.inflate(R.layout.georow, null);
                    TextView txtCalles = (TextView) v.findViewById(R.id.txtCalles);
                    TextView txtDist = (TextView) v.findViewById(R.id.txtDist);
                    TextView txtColectivos = (TextView) v.findViewById(R.id.txtColectivos);

                    txtCalles.setText(calle1 + " y " + calle2);
                    txtDist.setText("a " + o.getInt("distancia") + "mts");
                    //String label = "";
                    //for (int j = 0; j < colectivos.length; j++) label += colectivos[j] + " ";
                    txtColectivos.setText(colectivos);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent i = new Intent(geoActivity.this, colectivoSearch.class);
                                i.putExtra("calle", o.getInt("idCalle"));
                                i.putExtra("interseccion", o.getInt("idInter"));
                                i.putExtra("accion","street");
                                i.putExtra("Stops", SStops);
                                startActivity(i);
                            }catch (Exception e){e.printStackTrace();}
                        }
                    });


                    listItems.addView(v);
                }
            } catch (Exception e) {e.printStackTrace();}

        }
    }

    public void minusClick() {
        if (d > 100) {
            d -= 100;
            txtDist.setText(d + "mts");
            settings.putInteger("radio",d);
            rellenarListView();
        }
    }

    public void plusClick() {
        d += 100;
        txtDist.setText(d + "mts");
        settings.putInteger("radio",d);
        rellenarListView();
    }


    public void makeToast(String s) {
        Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_geo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.act_refresh) {
            pedirUbicacion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLocationChanged(Location l) {
        lat = l.getLatitude();
        lng = l.getLongitude();
        precision =   l.getAccuracy();
        txtDist.clearAnimation();
        rellenarListView();
        makeToast("Ubicacion establecida");
        Log.d("geoActivity",l.getLatitude() + "  " + l.getLongitude() + " " + l.getProvider() + " " + l.getAccuracy());
        //l.getLatitude()
        //l.getLongitude();

    }

    @Override
    public void onProviderDisabled(String provider) {
        // called when the GPS provider is turned off (user turning off the GPS on the phone)
    }

    @Override
    public void onProviderEnabled(String provider) {
        // called when the GPS provider is turned on (user turning on the GPS on the phone)
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}