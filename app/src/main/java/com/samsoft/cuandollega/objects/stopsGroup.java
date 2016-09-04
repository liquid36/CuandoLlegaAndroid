package com.samsoft.cuandollega.objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sam on 29/01/15.
 */
public class stopsGroup {
    public Integer idCalle;
    public Integer idInter;
    public String Bus;
    public Integer idFav;


    public stopsGroup()
    {
        idCalle = 0;
        idInter = 0;
        Bus = "";
        idFav = 0;
    }

    public stopsGroup(Integer calle, Integer inter,String colectivo,Integer favoritos)
    {
        idCalle = calle;
        idInter = inter;
        Bus = colectivo;
        idFav = favoritos;
    }

    public static String stopsToString(stopsGroup[] s)
    {
        JSONArray a = new JSONArray();
        try {

            for(int i = 0;i < s.length;i++)
            {
                String jo = "{ \"idCalle\":" + s[i].idCalle + " , \"idInter\": " + s[i].idInter ;
                jo +=  " , \"Bus\": \"" + s[i].Bus + "\", \"idFav\":" + s[i].idFav + "}"  ;
                JSONObject o = new JSONObject(jo);
                a.put(o);

            }
        } catch (Exception e) {e.printStackTrace();}
        return a.toString();
    }

    public static stopsGroup[] stringtoStops(String s)
    {
        try {
            JSONArray a = new JSONArray(s);
            stopsGroup [] r = new stopsGroup[a.length()];
            for(int i = 0;i < a.length();i++) {
                JSONObject o = a.getJSONObject(i);
                stopsGroup t = new stopsGroup(o.getInt("idCalle"),o.getInt("idInter"),o.getString("Bus"),o.getInt("idFav"));
                r[i] = t;
            }
            return r;
        } catch (Exception e) {
            Log.d("stopsGroup",s);
            e.printStackTrace();
            return new stopsGroup[0];
        }

    }

    public static stopsGroup[] addItem(stopsGroup[] ls, stopsGroup item)
    {
        stopsGroup r[] = new stopsGroup[ls.length + 1];
        for (int l = 0; l < ls.length;l++) r[l] = ls[l];
        r[ls.length] = item;
        return r;
    }
}
