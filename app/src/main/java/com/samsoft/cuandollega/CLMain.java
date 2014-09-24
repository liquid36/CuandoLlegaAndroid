package com.samsoft.cuandollega;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
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

        if (!getStat()) {
            progresDialog = ProgressDialog.show(this, "Cargando base de datos", "Por favor espere...", true);
            LoadDataBase run = new LoadDataBase(false);
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

    public void refreshClick(View v)
    {
        String url = "https://rawgit.com/liquid36/CLDownload/master/test.db";
        new DownloadFileAsync().execute(url);

        /*ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, "alpha", 1f, .3f);
        fadeOut.setDuration(2000);
        final AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeOut);
        mAnimationSet.start();*/

        //View vv = findViewById(R.id.probando);
        //expand(vv);

    }

    private void expand(View summary) {
        //set Visible
        summary.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        summary.measure(widthSpec, 70);

        ValueAnimator mAnimator = slideAnimator(0, 70, summary);
        mAnimator.setDuration(2000);
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end, final View summary) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = summary.getLayoutParams();
                layoutParams.height = value;
                summary.setLayoutParams(layoutParams);
            }
        });
        return animator;
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

    public void CopiarBaseDatos()
    {
        progresDialog = ProgressDialog.show(this, "Cargando base de datos", "Por favor espere...", true);
        LoadDataBase run = new LoadDataBase(false);
        run.execute();
    }

    public boolean LoadFromFile(Boolean rawFile) {
        try {
            File dbfile = getApplicationContext().getDatabasePath("CuandoLLega.db");
            if (!dbfile.exists()) {
                boolean b = dbfile.getParentFile().mkdirs();
            }

            InputStream in;
            if (rawFile) in = getResources().openRawResource(R.raw.test);
            else in  = new FileInputStream(getDatabasePath("test.db"));
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
    private class LoadDataBase extends AsyncTask<String, Integer, Boolean> {
        private Boolean rawFile;
        LoadDataBase(Boolean b) {
            rawFile = b;
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);

        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(getDatabasePath("test.db"));

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {}
            return null;

        }
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            removeDialog(DIALOG_DOWNLOAD_PROGRESS);
            mProgressDialog.dismiss();
            mProgressDialog.cancel();
            CopiarBaseDatos();

        }
    }

}
