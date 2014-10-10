package com.samsoft.cuandollega.extra;

import android.provider.ContactsContract;
import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.favoriteScreen;

/**
 * Created by sam on 10/10/14.
 */
public class FavAction implements Action {
    private Integer id;
    private DataBase db;
    private favoriteScreen activity;
    public FavAction(Integer i,DataBase d,favoriteScreen a)
    {
        id = i;
        db = d;
        activity = a;
    }

    @Override
    public void Run() {
        db.removeFavorito(id);
        activity.ShowFavList();
    }
}
