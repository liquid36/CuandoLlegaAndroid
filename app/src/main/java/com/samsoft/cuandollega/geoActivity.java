package com.samsoft.cuandollega;

import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class geoActivity extends ActionBarActivity {
    private Integer d = 100;
    private TextView txtDist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list);
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
    }

    public void minusClick()
    {
        if (d > 100) {
            d -= 100;
            txtDist.setText(d + "mts");
        }
    }

    public void plusClick()
    {
        d += 100;
        txtDist.setText(d + "mts");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_geo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
