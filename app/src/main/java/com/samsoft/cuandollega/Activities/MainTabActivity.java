package com.samsoft.cuandollega.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.samsoft.cuandollega.Fragments.calleList;
import com.samsoft.cuandollega.Fragments.controlerSelector;
import com.samsoft.cuandollega.Fragments.favoriteList;
import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.objects.MainTabAdapter;
import com.samsoft.cuandollega.objects.stopsGroup;
import com.samsoft.cuandollega.paradasinfo;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by sam on 18/05/15.
 */
public class MainTabActivity extends ActionBarActivity implements ActionBar.TabListener,
                                                                  favoriteList.favoriteListListener,
                                                                  controlerSelector.controlerSelectorListener {
    private ViewPager viewPager;
    private MainTabAdapter mAdapter;
    private ActionBar actionBar;
    private stopsGroup stops [];
    // Tab titles
    private String[] tabs = { "Busqueda", "Marcadores"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_view_main);

        Bundle datos = getIntent().getExtras();
        if (datos != null && datos.containsKey("Stops")) stops = stopsGroup.stringtoStops(datos.getString("Stops"));
        else stops = new stopsGroup[]{};

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        mAdapter = new MainTabAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}

            @Override
            public void onPageScrollStateChanged(int arg0) {}
        });
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

        } else if (id == android.R.id.home) {
            if (viewPager.getCurrentItem() == 0) {
                onRemoveAllBackStack(getSupportFragmentManager());
                actionBar.setDisplayHomeAsUpEnabled(false);
            }

        }
        return super.onOptionsItemSelected(item);
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
        if (viewPager.getCurrentItem() == 0) {
            FragmentManager fm = getSupportFragmentManager();
            if (onBackPressed(fm)) {
                return;
            }
        }
        super.onBackPressed();
    }

    private boolean onBackPressed(FragmentManager fm) {
        if (fm != null) {
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
                return true;
            }
            List<Fragment> fragList = fm.getFragments();
            if (fragList != null && fragList.size() > 0) {
                for (Fragment frag : fragList) {
                    if (frag == null) {
                        continue;
                    }
                    Log.d("FRAGMENT",frag.getClass().toString());
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
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (tab.getPosition() != 0) actionBar.setDisplayHomeAsUpEnabled(false);
        else actionBar.setDisplayHomeAsUpEnabled(true);
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
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

    public void allSelect(JSONObject o)
    {
        try {
            Integer idCalle = o.getInt("idCalle");
            Integer idInter = o.getInt("idInter");
            String colectivo = o.getString("colectivo");
            if (colectivo.equals(" - TODOS - ")) colectivo = "";

            Intent i = new Intent(this, paradasinfo.class);
            stopsGroup r[] = stopsGroup.addItem(stops,new stopsGroup(idCalle,idInter,colectivo,0));
            i.putExtra("Stops",stopsGroup.stopsToString(r));
            startActivity(i);
        } catch (Exception e){e.printStackTrace();}
    }
}
