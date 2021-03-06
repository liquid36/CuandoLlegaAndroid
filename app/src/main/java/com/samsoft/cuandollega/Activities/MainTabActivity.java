package com.samsoft.cuandollega.Activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Choreographer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.samsoft.cuandollega.BuildConfig;
import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.Fragments.controlerSelector;
import com.samsoft.cuandollega.Fragments.favoriteList;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.Views.CustomTabView;
import com.samsoft.cuandollega.extra.DialogAccion;
import com.samsoft.cuandollega.extra.updateDB;
import com.samsoft.cuandollega.objects.MainTabAdapter;
import com.samsoft.cuandollega.objects.settingRep;
import com.samsoft.cuandollega.objects.stopsGroup;
import com.samsoft.cuandollega.paradasinfo;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam on 18/05/15.
 */
public class MainTabActivity extends ActionBarActivity implements ActionBar.TabListener,
                                                                  favoriteList.favoriteListListener,
                                                                  controlerSelector.controlerSelectorListener {
    private CustomTabView viewPager;
    private MainTabAdapter mAdapter;
    private ActionBar actionBar;
    private stopsGroup stops [];
    private settingRep settings;
    private ProgressDialog mProgressDialog;
    private  ProgressDialog progresDialog;
    private String[] tabs = { "Busqueda", "Marcadores","Mapa"};
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

    public DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_view_main);
        settings = new settingRep(getApplicationContext());
        Bundle datos = getIntent().getExtras();
        if (datos != null && datos.containsKey("Stops")) stops = stopsGroup.stringtoStops(datos.getString("Stops"));
        else stops = new stopsGroup[]{};

        db = new DataBase(getApplicationContext());

        int versionCode = BuildConfig.VERSION_CODE ;
        int lastCode = settings.getInteger("Version");
        boolean db_update = settings.getBoolean("db-update");

        if (versionCode != lastCode || !db_update) {
            settings.putBoolean("db-update",false);
            CopiarBaseDatos(true);
            settings.putInteger("Version",versionCode);
        }

        // Initilization
        viewPager = (CustomTabView) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        mAdapter = new MainTabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        if (savedInstanceState != null && savedInstanceState.containsKey("scrollState")) {
            scrollState = savedInstanceState.getBoolean("scrollState");
            setScrollView(scrollState);
        } else{
            scrollState = true;
            setScrollView(true);
        }

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                if (position == 2) {
                    Fragment mapCtl = searchFragment(mAdapter.getItem(position));
                    if (mapCtl != null) {
                        Fragment fmap = mapCtl.getChildFragmentManager().findFragmentByTag("MAPA");
                        if (fmap != null && fmap.isVisible()) setScrollView(false);
                    }
                } else {
                    setScrollView(true);
                }
                Fragment Ctl = searchFragment(mAdapter.getItem(position));
                if (Ctl.getChildFragmentManager().getBackStackEntryCount() == 0) {
                    actionBar.setDisplayHomeAsUpEnabled(false);
                } else {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("scrollState", scrollState);
        super.onSaveInstanceState(savedInstanceState);
    }

    public Fragment searchFragment(Fragment f)
    {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragList = fm.getFragments();
        if (fragList != null && fragList.size() > 0) {
            for (Fragment frag : fragList) {
                if (frag.getClass().getName().equals(f.getClass().getName())) {
                    return frag;
                }

            }
        }
        return null;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Boolean first = settings.getBoolean("FirstLoad");
        if (!first && true == false) {
            settings.putBoolean("FirstLoad",true);
            /*new DialogAccion(MainTabActivity.this,"Cuando Llega Movil",
                    "Bienvenido a Cuando Llega Movil!\n\n" +
                            "Busqueda por Calle\n" +
                            "Busqueda por Colectivo\n" +
                            "Marcadores de paradas\n"+
                            "Paradas Cercanas\n"+
                            "Multiples consultas"
                    ,"Aceptar" ,"" , null).Show();*/
        }
        Boolean fallo = settings.getBoolean("FALLOTRES");
        if (!fallo) {
            settings.putBoolean("FALLOTRES",true);
            new DialogAccion(MainTabActivity.this, "Cuando Llega Movil",
                    "Volvieron los recorridos. Que felicidad!!!!\n"
                    + " Muchas gracias por la paciencia."
                    , "Aceptar", "", null).Show();
        }
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
            Intent i = new Intent(this, About.class);
            startActivity(i);
        } else if (id == android.R.id.home) {
            if (viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == 2) {
                onRemoveAllBackStack(getSupportFragmentManager());
                actionBar.setDisplayHomeAsUpEnabled(false);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void CopiarBaseDatos(boolean fromRaw)
    {
        try {
            progresDialog = ProgressDialog.show(this, "Cargando base de datos", "Por favor espere...", true);
            updateDB run = new updateDB(getApplicationContext(), db);
            run.firstLoad(progresDialog);
            settings.putBoolean("db-update",true);
        } catch (Exception e) {
            Log.d("MainTabAtivity","Load problems");
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

    @Override
    public void onBackPressed() {
        setScrollView(true);
        if (viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == 2) {
            FragmentManager fm = getSupportFragmentManager();
            if (onBackPressed(fm)) {
                if (viewPager.getCurrentItem() == 2) {
                    Fragment mapCtl = searchFragment(mAdapter.getItem(viewPager.getCurrentItem()));

                    if (mapCtl != null) {
                        Fragment fmap = mapCtl.getChildFragmentManager().findFragmentByTag("MAPA");
                        if (fmap != null && fmap.isVisible()) {
                            setScrollView(false);
                        }
                    }
                }
                return;

            }
        }

        super.onBackPressed();
    }

    private boolean onBackPressed(FragmentManager fm) {
        if (fm != null) {
            if (fm.getBackStackEntryCount() == 1) actionBar.setDisplayHomeAsUpEnabled(false);
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStackImmediate();
                //fm.popBackStack();
                return true;
            }
            List<Fragment> fragList = fm.getFragments();
            if (fragList != null && fragList.size() > 0) {
                for (Fragment frag : fragList) {
                    if (frag == null) {
                        continue;
                    }
                    if (frag.isVisible()) {
                        if (onBackPressed(frag.getChildFragmentManager())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean onRemoveAllBackStack(FragmentManager fm) {
        if (fm != null) {
            Integer i = fm.getBackStackEntryCount();
            for (int j = 0; j < i;j++) {
                fm.popBackStack();
            }
            List<Fragment> fragList = fm.getFragments();
            if (fragList != null && fragList.size() > 0) {
                for (Fragment frag : fragList) {
                    if (frag == null) {
                        continue;
                    }
                    if (frag.isVisible()) {
                        if (onRemoveAllBackStack(frag.getChildFragmentManager())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.d("MainTabActivity","Ocrurre onTabReselected");
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (tab.getPosition() == 1) actionBar.setDisplayHomeAsUpEnabled(false);
        else actionBar.setDisplayHomeAsUpEnabled(true);
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.d("MainTabActivity","Ocrurre onTabUnselected");
    }


    public void onFavoriteClick(JSONObject id) {
        try {
            Intent i = new Intent(this, paradasinfo.class);
            //stopsGroup stops [] = new stopsGroup[]{};
            stopsGroup r[] = stopsGroup.addItem(stops,new stopsGroup(0,0,"",id.getInt("id")));
            i.putExtra("Stops",stopsGroup.stopsToString(r));
            startActivity(i);
        } catch (Exception e) {e.printStackTrace();}
    }

    public void allSelect(Integer idCalle, Integer idInter,String colectivo)
    {
        if (colectivo.equals(" - TODOS - ")) colectivo = "";
        Intent i = new Intent(this, paradasinfo.class);
        stopsGroup r[] = stopsGroup.addItem(stops,new stopsGroup(idCalle,idInter,colectivo,0));
        i.putExtra("Stops",stopsGroup.stopsToString(r));
        startActivity(i);
    }

    public void allSelect(JSONObject o)
    {
        try {
            Integer idCalle = o.getInt("idCalle");
            Integer idInter = o.getInt("idInter");
            String colectivo = o.getString("colectivo");
            allSelect(idCalle,idInter,colectivo);
        } catch (Exception e){e.printStackTrace();}
    }

    private Boolean scrollState = true;
    public void setScrollView(Boolean can)
    {
       Log.d("MainTabACtivity", "setScrollView = " + can);
       viewPager.setPagingEnabled(can);
       scrollState = can;
    }

    // COPIA ARCHIVOS ******************************************************************************
    /*private class UpdateDB extends AsyncTask<String, Integer, Boolean> {
        private Context contex;
        private DataBase db;
        UpdateDB(Context c, DataBase db) {
            this.db = db; contex = c;
        }

        public boolean LoadFromFile() {
            try {
                File dbfile = contex.getDatabasePath("test.db");
                InputStream in   = contex.getResources().openRawResource(R.raw.test);
                OutputStream out = new FileOutputStream(dbfile.getAbsolutePath(),false);
                updateDB.CopyFile(in, out);

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
            return LoadFromFile();
        }

        protected void onProgressUpdate(Integer... progress) {
            return;
        }

        protected void onPostExecute(Boolean result) {
            progresDialog.dismiss();
            return;
        }
    }*/
}


/*

* BaseFragment con controlador de stack y open de base de datos
*
*
*
*
*/