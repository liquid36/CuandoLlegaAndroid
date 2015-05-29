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
import android.location.Criteria;
import android.location.LocationManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.samsoft.cuandollega.Activities.MainTabActivity;
import com.samsoft.cuandollega.extra.DialogAccion;
import com.samsoft.cuandollega.objects.locationListener;
import com.samsoft.cuandollega.objects.settingRep;
import com.samsoft.cuandollega.objects.stopsGroup;
import com.samsoft.cuandollega.Activities.favoriteScreen;

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
    settingRep settings;


    /** Called when the activity is about to become visible. */
    @Override
    protected void onResume() {
        super.onResume();
        crearView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        settings = new settingRep(getApplicationContext());
        db = new DataBase(getApplicationContext());

        crearView();

        //db.getClosePoint("-32.947392","-60.711163",500);

        int versionCode = BuildConfig.VERSION_CODE ;
        int lastCode = settings.getInteger("Version");
        Boolean first = settings.getBoolean("FirstLoad");

        if (versionCode != lastCode  && first ) {
            CopiarBaseDatos(true);
            settings.putInteger("Version",versionCode);
            /*new DialogAccion(CLMain.this,"Cuando Llega Pro",
                    "Nuevas novedades!\n\n" +
                    "Ahora puedes agregar nuevas paradas a tu consulta. Presiona en el signo mas para hacerlo.\n"
                    + "Actualizamos las paradas de algunos colectivos."
                    ,"Aceptar","" , null).Show();*/
        } else if (!first) {
            CopiarBaseDatos(true);
            settings.putInteger("Version",versionCode);
            settings.putBoolean("FirstLoad",true);
            new DialogAccion(CLMain.this,"Cuando Llega Movil",
                    "Bienvenido a Cuando Llega Pro!\n\n" +
                    "Busqueda por Calle\n" +
                    "Busqueda por Colectivo\n" +
                    "Marcadores de paradas\n"+
                    "Multiples consultas"
                    ,"Aceptar" ,"" , null).Show();

        }

        /* else {
            if (isOnline()) {
                String url2 = "https://raw.githubusercontent.com/liquid36/CLDownload/master/db.md5";
                new DownloadFileAsync(getApplicationContext(), true).execute(url2);
            }
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mnuVotar) {
            rateClick(null);
        } else if (id == R.id.mnuShare) {
            sharedClick(null);
        } else if (id == R.id.mnuAbout) {
            //startActivity(new Intent(this, MainTabActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void crearView()
    {
        setContentView(R.layout.activity_clmain);
        listItems = (LinearLayout) findViewById(R.id.listItems);
        JSONArray favoritos = db.getFavoritos();
        for (int i = 0; i < favoritos.length();i++) {
            View v = inflater.inflate(R.layout.markrow, null);
            TextView t = (TextView) v.findViewById(R.id.txtName);
            LinearLayout l = (LinearLayout) v.findViewById(R.id.btnLayout);
            try {
                final JSONObject o = favoritos.getJSONObject(i);
                final Integer id = o.getInt("id");
                t.setText(o.getString("name"));
                l.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(CLMain.this, paradasinfo.class);
                        stopsGroup r[] = new stopsGroup[1];
                        r[0]=new stopsGroup(0,0,"",id);
                        i.putExtra("Stops",stopsGroup.stopsToString(r));
                        startActivity(i);
                    }
                });


                listItems.addView(v,i);
                Log.d("CLMAIN","ADDING FAVORITOS");
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    /*public int getLastVersion()
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
    }*/

    /*public boolean getStat()
    {

        return settings.getBoolean("Loaded", false);
    }

    public void saveStat(boolean b)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("Loaded", b);
        editor.commit();
    }*/

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void calleClick(View v)
    {
        Intent i = new Intent(CLMain.this, calleSearch.class);
        i.putExtra("calle",0);
        i.putExtra("colectivos","");
        i.putExtra("accion","street");
        i.putExtra("Stops","");
        startActivity(i);
    }

    public void busClick(View v)
    {
        Intent i = new Intent(CLMain.this, colectivoSearch.class);
        i.putExtra("calle",0);
        i.putExtra("interseccion",0);
        i.putExtra("accion","bus");
        i.putExtra("Stops","");
        startActivity(i);
    }

    public void geoClick(View v)
    {
        Intent i = new Intent(CLMain.this, geoActivity.class);
        i.putExtra("calle",0);
        i.putExtra("colectivos","");
        i.putExtra("accion","street");
        i.putExtra("Stops","");
        startActivity(i);
    }

    public void refreshClick(View v)
    {
        String url = "https://rawgit.com/liquid36/CLDownload/master/test.db";
        String url2 = "https://raw.githubusercontent.com/liquid36/CLDownload/master/db.md5";

        new DownloadFileAsync(getApplicationContext(),false).execute(url, url2);
    }

    public void favClick(View v)
    {
        Intent i = new Intent(CLMain.this, favoriteScreen.class);
        i.putExtra("Stops","");
        i.putExtra("NoAdd",false);
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

        if (!settings.getString("Reciente", "").equals(""))  {
            String sreciente = settings.getString("Reciente", "");
            //stopsGroup[] stops = stopsGroup.stringtoStops(sreciente);

        // get item count - equal to number of tabs
            Intent i = new Intent(CLMain.this, paradasinfo.class);
            i.putExtra("Stops",sreciente);
            startActivity(i);

        } else {
            makeToast("No se realizo ninguna consulta");
        }
    }

    public void sharedClick(View v)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Cuando Llega Movil - https://play.google.com/store/apps/details?id=com.samsoft.cuandollega" );
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Cuando Llega Movil"));
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void makeToast(String s) {
        Context context = getApplicationContext();
        CharSequence text = s;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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
                db = new DataBase(getApplicationContext());
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
            //saveStat(true);
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
