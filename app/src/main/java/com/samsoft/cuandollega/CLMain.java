package com.samsoft.cuandollega;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.ContactsContract;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class CLMain extends ActionBarActivity {
    private  ProgressDialog progresDialog;
    private LinearLayout listItems;
    private DataBase db;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clmain);
        listItems = (LinearLayout) findViewById(R.id.aaa);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (!getStat()) {
            progresDialog = ProgressDialog.show(this, "Cargando base de datos", "Por favor espere...", true);
            LoadDataBase run = new LoadDataBase();
            run.execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public boolean getStat()
    {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("CuandoLLega", MODE_PRIVATE);
        return settings.getBoolean("Loaded", false);
    }

    public void saveStat(boolean b)
    {
        Log.d("Preferences", "Paso por saveStat = " + b);
        SharedPreferences settings = getApplicationContext().getSharedPreferences("CuandoLLega", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("Loaded", b);
        editor.commit();
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void calleClick(View v)
    {
        Intent i = new Intent(CLMain.this, calleSearch.class);
        i.putExtra("calle",0);
        i.putExtra("colectivos","");
        i.putExtra("accion","street");
        startActivity(i);
    }

    public void busClick(View v)
    {
        Intent i = new Intent(CLMain.this, colectivoSearch.class);
        i.putExtra("calle",0);
        i.putExtra("interseccion",0);
        i.putExtra("accion","bus");
        startActivity(i);
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



    private String streamToString(InputStream i)
    {
        try {
            InputStreamReader is = new InputStreamReader(i);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(is);

            String read = br.readLine();
            while (read != null) {
                sb.append(read);
                read = br.readLine();
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean LoadFromFile() {
        try {
            File dbfile = getApplicationContext().getDatabasePath("CuandoLLega.db");
            if (!dbfile.exists()) {
                boolean b = dbfile.getParentFile().mkdirs();
            }
            InputStream in = getResources().openRawResource(R.raw.test);
            FileOutputStream out = new FileOutputStream(dbfile.getAbsolutePath(),false);
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0)
                    out.write(buff, 0, read);
            } finally {
                in.close();
                out.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    private class LoadDataBase extends AsyncTask<String, Integer, Boolean> {
        protected Boolean doInBackground(String... urls) {
            return LoadFromFile();

        }

        protected void onProgressUpdate(Integer... progress) {
            return;
        }

        protected void onPostExecute(Boolean result) {
            progresDialog.dismiss();
            saveStat(true);
            return;
        }
    }

}
