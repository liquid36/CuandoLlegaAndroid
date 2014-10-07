package com.samsoft.cuandollega;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class CLMain extends ActionBarActivity {
    private  ProgressDialog progresDialog;
    private LinearLayout listItems;
    private DataBase db;
    private LayoutInflater inflater;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clmain);
        listItems = (LinearLayout) findViewById(R.id.aaa);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = new DataBase(getApplicationContext());

        if (!getStat()) {
            CopiarBaseDatos(true);
        } else {
            if (isOnline()) {
                String url2 = "https://raw.githubusercontent.com/liquid36/CLDownload/master/db.md5";
                new DownloadFileAsync(getApplicationContext(), true).execute(url2);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
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

    public void refreshClick(View v)
    {
        String url = "https://rawgit.com/liquid36/CLDownload/master/test.db";
        String url2 = "https://raw.githubusercontent.com/liquid36/CLDownload/master/db.md5";

        new DownloadFileAsync(getApplicationContext(),false).execute(url,url2);
    }

    public void favClick(View v)
    {
        Intent i = new Intent(CLMain.this, paradasinfo.class);
        i.putExtra("calle",0);
        i.putExtra("interseccion",0);
        i.putExtra("colectivos","");
        i.putExtra("accion","favorite");
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

    public void CopiarBaseDatos(boolean fromRaw)
    {
        progresDialog = ProgressDialog.show(this, "Cargando base de datos", "Por favor espere...", true);
        UpdateDB run = new UpdateDB(getApplicationContext(),fromRaw);
        run.execute();
        View v = findViewById(R.id.msgLay);
        v.setVisibility(View.GONE);
    }

    public void ShowUpdateMessage()
    {
        View v = findViewById(R.id.msgLay);
        ExpandAnimation.expand(v,110,1500);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    // COPIA ARCHIVOS ******************************************************************************
    private class UpdateDB extends AsyncTask<String, Integer, Boolean> {
        private Boolean rawFile;
        private  Context contex;
        UpdateDB(Context c, Boolean b) {
            rawFile = b; contex = c;
        }

        public boolean LoadFromFile(Boolean rawFile) {
            try {
                File dbfile = contex.getDatabasePath("test.db");
                File md5 = contex.getDatabasePath("test.md5");

                if (rawFile) {
                    InputStream in   = contex.getResources().openRawResource(R.raw.test);
                    OutputStream out = new FileOutputStream(dbfile.getAbsolutePath(),false);
                    ExpandAnimation.CopyFile(in,out);
                    in = contex.getResources().openRawResource(R.raw.db);
                    out = new FileOutputStream(md5.getAbsolutePath(),false);
                    ExpandAnimation.CopyFile(in,out);
                }

                db.AttachDB(getDatabasePath("test.db").getAbsolutePath());
                db.Close();
                getDatabasePath("test.db").delete();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        protected Boolean doInBackground(String... urls) {
            return LoadFromFile(rawFile);

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

    // BAJA ARCHIVOS DE INTERNET *******************************************************************
    class DownloadFileAsync extends AsyncTask<String, String, String> {
        private Context contex;
        private boolean hideDialog;
        DownloadFileAsync(Context c,boolean hD) {
            contex = c;
            hideDialog = hD;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!hideDialog) showDialog(DIALOG_DOWNLOAD_PROGRESS);

        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                for(int i = 0; i < aurl.length; i++) {
                    String name = aurl[i].substring(aurl[i].lastIndexOf("/") + 1);
                    if (hideDialog) name = "temp.md5";

                    URL url = new URL(aurl[i]);
                    URLConnection conexion = url.openConnection();
                    if (!name.equals("test.db")) conexion.setDoOutput(true);
                    if (conexion != null) Log.d("CUADNOLLEGA","Funciono");
                    conexion.connect();


                    int lenghtOfFile = conexion.getContentLength();
                    Log.d("ANDRO_ASYNC", "Lenght of file: " + name + "  " + lenghtOfFile);

                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(contex.getDatabasePath(name));

                    byte data[] = new byte[1024];
                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
            } catch (Exception e) {e.printStackTrace();}
            return null;

        }
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            if (!hideDialog) mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            if (!hideDialog) {
                dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
                removeDialog(DIALOG_DOWNLOAD_PROGRESS);
                mProgressDialog.dismiss();
                mProgressDialog.cancel();
                CopiarBaseDatos(false);
            } else {
                try {
                    BufferedReader a = new BufferedReader(new InputStreamReader(new FileInputStream(contex.getDatabasePath("temp.md5"))));
                    BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(contex.getDatabasePath("db.md5"))));
                    String sa = a.readLine();
                    String sb = b.readLine();
                    Log.d("CUANDO LLEGA","MD5 actual " + sb );
                    Log.d("CUANDO LLEGA","MD5 Nuevo " + sa );
                    if (!sa.equals(sb)) {
                        Log.d("CUANDO LLEGA","Atencion cambio la base de datos");
                        ShowUpdateMessage();
                    }
                }catch (Exception e) {e.printStackTrace();ShowUpdateMessage();}
            }
        }
    }

}
