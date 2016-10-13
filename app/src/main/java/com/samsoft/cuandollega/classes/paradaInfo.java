package com.samsoft.cuandollega.classes;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by sam on 12/10/16.
 */

public class paradaInfo {

    public Integer calle = 0;
    public Integer inter = 0;
    public Integer parada = 0;

    public String bus_name = "";
    public String destino = "";

    public String calle_name = "";
    public String inter_name = "";

    public Boolean isFavorite = false;

    public JSONArray resultado = null;
    public ArrayList<String> arrivos = null;

    public String getLugar() {
        return calle_name + " Y " + inter_name + " (" + parada + ")";
    }

}
