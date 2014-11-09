package com.samsoft.cuandollega;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
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
import android.widget.Toast;

import com.samsoft.cuandollega.extra.DialogAccion;

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
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clmain);
        listItems = (LinearLayout) findViewById(R.id.aaa);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        settings = getApplicationContext().getSharedPreferences("CuandoLLega", MODE_PRIVATE);
        db = new DataBase(getApplicationContext());

        int versionCode = BuildConfig.VERSION_CODE;
        int lastCode = getLastVersion();
        if (versionCode != lastCode) {
            CopiarBaseDatos(true);
            saveLastVersion(versionCode);
            new DialogAccion(CLMain.this,"Novedades","Bienvenidos a Cuando Llega Pro!\nHemos actualizado las base de datos de calles y colectivos. Incluimos las calles de Funes para la linea 142.\nProximamente nuevas mejoras.","Aceptar","",null).Show();
        }

        /* else {
            if (isOnline()) {
                String url2 = "https://raw.githubusercontent.com/liquid36/CLDownload/master/db.md5";
                new DownloadFileAsync(getApplicationContext(), true).execute(url2);
            }
        }*/
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

    public int getLastVersion()
    {
        try {
            return settings.getInt("Version", 0);
        } catch (Exception e) {e.printStackTrace();return 0;}
    }

    public void saveLastVersion(int b)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("Version", b);
        editor.commit();
    }

    public boolean getStat()
    {

        return settings.getBoolean("Loaded", false);
    }

    public void saveStat(boolean b)
    {
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
        Intent i = new Intent(CLMain.this, favoriteScreen.class);
        startActivity(i);
    }

    public void rateClick(View v)
    {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void recienteClick(View v)
    {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("CuandoLLega", MODE_PRIVATE);
        Log.d("CLMain","Estado para recientes:");
        Log.d("CLMain","Accion: " + settings.getString("Uaccion", ""));
        Log.d("CLMain","Calle: " + settings.getInt("UidCalles",0) );
        Log.d("CLMain","idInter: " + settings.getInt("UidInter",0));
        Log.d("CLMain","Colectivo: " + settings.getString("UColectivos", ""));
        Log.d("CLMain","idFavorito: " + settings.getInt("UidFav", 0));

        if (!settings.getString("Uaccion", "").equals(""))  {
            Intent i = new Intent(CLMain.this, paradasinfo.class);
            i.putExtra("calle",settings.getInt("UidCalles",0));
            i.putExtra("favorito",settings.getInt("UidFav",0));
            i.putExtra("interseccion",settings.getInt("UidInter",0));
            i.putExtra("colectivos",settings.getString("Ucolectivos", ""));
            i.putExtra("accion",settings.getString("Uaccion", ""));
            startActivity(i);
        } else {
            makeToast("No se realizo ninguna consulta");
        }
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void makeToast(String s) {
        Context context = getApplicationContext();
        CharSequence text = s;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return;
    }

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
    }

    public void ShowUpdateMessage()
    {
        View v = findViewById(R.id.msgLay);
        ExpandAnimation.expand(v, 110, 1500);
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
                if (rawFile) {
                    InputStream in   = contex.getResources().openRawResource(R.raw.test);
                    OutputStream out = new FileOutputStream(dbfile.getAbsolutePath(),false);
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
