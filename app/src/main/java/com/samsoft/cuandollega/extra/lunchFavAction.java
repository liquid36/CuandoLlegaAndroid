package com.samsoft.cuandollega.extra;

import android.content.Context;
import android.content.Intent;

import com.samsoft.cuandollega.favoriteScreen;

/**
 * Created by sam on 19/10/14.
 */
public class lunchFavAction implements Action {

    private Context contet;
    public lunchFavAction(Context c)
    {
        contet = c;
    }

    @Override
    public void Run()
    {
        Intent i = new Intent(contet, favoriteScreen.class);
        contet.startActivity(i);
    }
}
