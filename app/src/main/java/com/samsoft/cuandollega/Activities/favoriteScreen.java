package com.samsoft.cuandollega.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.samsoft.cuandollega.Fragments.favoriteList;
import com.samsoft.cuandollega.R;

import org.json.JSONObject;

/**
 * Created by sam on 16/05/15.
 */
public class favoriteScreen extends ActionBarActivity implements  favoriteList.favoriteListListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_screen_activity);


    }

    public void onFavoriteClick(JSONObject id) {
        // NOTHING TO DO
    }




}
