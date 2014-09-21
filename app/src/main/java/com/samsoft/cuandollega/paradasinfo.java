package com.samsoft.cuandollega;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;


public class paradasinfo extends ActionBarActivity {
    private int idCalle;
    private int idInter;
    private String Bus;
    private LinearLayout listItems;
    private DataBase db;
    private LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle datos = getIntent().getExtras();

        listItems = (LinearLayout) findViewById(R.id.listItems);
        db =  new DataBase(getApplicationContext());

        idCalle = datos.getInt("calle");
        idInter = datos.getInt("interseccion");
        Bus = datos.getString("colectivos");

        ShowParadas();

    }

    public void ShowParadas()
    {
        JSONArray a = db.getStops(Bus,idCalle,idInter);
        for(int i = 0;i < a.length();i++) {
            try {
                JSONObject o = a.getJSONObject(i);
                View v = inflater.inflate(R.layout.waitingrow, null);
                TextView bus  = (TextView) v.findViewById(R.id.txtBus);
                TextView dest = (TextView) v.findViewById(R.id.txtDest);
                bus.setText(o.getString("name"));
                dest.setText(o.getString("desc"));
                listItems.addView(v);
                AskTime ask = new AskTime(v,getApplicationContext());
                ask.execute(o);
            } catch (Exception e) {e.printStackTrace();}
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.paradasinfo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private class AskTime extends AsyncTask<JSONObject, Integer, Boolean> {
        private View v;
        private String datos;
        private Context contex;
        public AskTime(View vv,Context c)
        {
            contex = c;
            v = vv;
        }

        private String InputStreamToString(InputStream in)
        {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch (Exception e){return "";}
        }

        @Override
        protected Boolean doInBackground(JSONObject... stops) {
            String url = "http://www.etr.gov.ar/ajax/getSmsResponse.php";
            InputStream content = null;
            JSONObject stop = stops[0];
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("parada",stop.getString("parada")));
                nameValuePairs.add(new BasicNameValuePair("linea", stop.getString("name")));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                content = response.getEntity().getContent();
                datos = InputStreamToString(content);
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
                String [] lineas = datos.substring(11).split("-");
                for(int i = 0; i < lineas.length; i++) {
                    TextView t = new TextView(contex);
                    t.setText(lineas[i]);
                    t.setTextColor(Color.WHITE);
                    //t.setTextSize(30);
                    LinearLayout list = (LinearLayout) v;
                    ProgressBar bar = (ProgressBar) list.findViewById(R.id.waitingbar);
                    bar.setVisibility(View.GONE);
                    list.addView(t);
                }
            }
            return;
        }
    }

}
