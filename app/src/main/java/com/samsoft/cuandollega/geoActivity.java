package com.samsoft.cuandollega;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class geoActivity extends ActionBarActivity implements LocationListener {
    private Integer d = 100;
    private TextView txtDist;
    private Double lat,lng;
    private float precision;
    private LayoutInflater inflater;
    private LinearLayout listItems;
    private DataBase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listItems = (LinearLayout) findViewById(R.id.listItems);
        db = new DataBase(getApplicationContext());

        LinearLayout l = (LinearLayout) findViewById(R.id.msgDistancia);
        l.setVisibility(View.VISIBLE);
        txtDist = (TextView) findViewById(R.id.labDistancia);
        ImageView minus = (ImageView) findViewById(R.id.btnMinus);
        ImageView plus = (ImageView) findViewById(R.id.btnPlus);
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

        pedirUbicacion();
    }

    public void pedirUbicacion()
    {
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestSingleUpdate(criteria, this, null);
        Log.d("geoActivity", "Pidiendo ubicacion");
    }

    public void rellenarListView()
    {
        listItems.removeAllViews();
        Log.d("geoActivity","" + d + Math.round(precision));
        JSONArray arr = db.getClosePoint(lat.toString(),lng.toString(),d + Math.round(precision));
        for(int i = 0;i < arr.length();i++) {
            try {
                JSONObject o = arr.getJSONObject(i);
                String calle1 = db.getCalleName(o.getInt("idCalle"));
                String calle2 = db.getCalleName(o.getInt("idInter"));
                String [] colectivos = db.colectivosEnEsquina(o.getInt("idCalle"),o.getInt("idInter"));
                if (colectivos.length > 0) {
                    View v = inflater.inflate(R.layout.georow, null);
                    TextView txtCalles = (TextView) v.findViewById(R.id.txtCalles);
                    TextView txtDist = (TextView) v.findViewById(R.id.txtDist);
                    TextView txtColectivos = (TextView) v.findViewById(R.id.txtColectivos);

                    txtCalles.setText(calle1 + " y " + calle2);
                    txtDist.setText("a " + o.getInt("distancia") + "mts");
                    String label = "";
                    for (int j = 0; j < colectivos.length; j++) label += colectivos[j] + " ";
                    txtColectivos.setText(label);

                    listItems.addView(v);
                }
            } catch (Exception e) {e.printStackTrace();}

        }
    }


    public void minusClick() {
        if (d > 100) {
            d -= 100;
            txtDist.setText(d + "mts");
            rellenarListView();
        }
    }

    public void plusClick() {
        d += 100;
        txtDist.setText(d + "mts");
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location l) {
        lat = l.getLatitude();
        lng = l.getLongitude();
        precision =   l.getAccuracy();
        rellenarListView();
        makeToast(l.getLatitude() + "  " + l.getLongitude() + " " + l.getProvider());
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