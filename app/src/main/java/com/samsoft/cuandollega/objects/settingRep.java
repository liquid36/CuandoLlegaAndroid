package com.samsoft.cuandollega.objects;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sam on 03/02/15.
 */
public class settingRep {
    public Context contex;
    public SharedPreferences repo;
    public settingRep(Context c)
    {
        contex = c;
        repo = contex.getSharedPreferences("CuandoLLega", Context.MODE_PRIVATE);
    }

    public String getString(String s)
    {
        try {
            return repo.getString(s, "");
        } catch (Exception e) {e.printStackTrace();return "";}
    }

    public Integer getInteger(String s)
    {
        try {
            return repo.getInt(s, 0);
        } catch (Exception e) {e.printStackTrace();return 0;}
    }

    public Boolean getBoolean(String s)
    {
        try {
            return repo.getBoolean(s, false);
        } catch (Exception e) {e.printStackTrace();return false;}
    }

    public void putInteger(String k,Integer v) {
        SharedPreferences.Editor editor = repo.edit();
        editor.putInt(k, v);
        editor.commit();
    }

    public void putString(String k,String v) {
        SharedPreferences.Editor editor = repo.edit();
        editor.putString(k, v);
        editor.commit();
    }

    public void putBoolean(String k,Boolean v) {
        SharedPreferences.Editor editor = repo.edit();
        editor.putBoolean(k, v);
        editor.commit();
    }

}
