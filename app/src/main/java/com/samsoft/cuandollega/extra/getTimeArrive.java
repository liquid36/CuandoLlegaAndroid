package com.samsoft.cuandollega.extra;

import android.graphics.Color;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samsoft.cuandollega.ExpandAnimation;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by sam on 15/11/14.
 */
public class getTimeArrive {

    private Integer linea;
    private Integer parada;

    public getTimeArrive(Integer lin, Integer par)
    {
        linea = lin;
        parada = par;
    }

    public ArrayList<String> parserResult(String datos)
    {
        ArrayList<String> result = new ArrayList<String>();
        Log.d("getTimeArrive",datos);
        String [] lineas = datos.substring(11).split("-");
        for(int i = 0; i < lineas.length; i++) {
            if (lineas[i].trim().substring(0,1).equals("<")) {
                break;
            }
            if (lineas[i].length() > 6 ) {
                Log.d("sublinea",lineas[i]);
                if (lineas[i].indexOf(":") > 0) {
                    int dospuntos_pos = lineas[i].indexOf(":");
                    int bandera_pos = lineas[i].substring(0,dospuntos_pos).lastIndexOf(' ');
                    String bandera = lineas[i].substring(0,dospuntos_pos).substring(bandera_pos);


                    String steps = lineas[i].substring(lineas[i].indexOf(":") + 1);
                    if (!lineas[i].contains("min")) {
                        result.add(bandera + ":" + steps);
                    } else if (lineas[i].contains("Prox. serv.")) {
                        result.add(bandera + ":" + steps);
                    } else {
                        String[] ll = steps.split("siguiente");
                        for (int j = 0; j < ll.length; j++) {
                            if (ll[j].length() > 0) {
                                Log.d("TIME", ll[j]);
                                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                                Calendar now = Calendar.getInstance();
                                now.add(Calendar.MINUTE, ExpandAnimation.strToInteger(ll[j], 1));
                                result.add(bandera + ":" + ll[j] + " llega " + df.format(now.getTime()) + "Hs");
                            }
                        }
                    }
                } else {
                    result.add(lineas[i]);
                }
            }
        }
        return result;
    }


    public ArrayList<String> run()
    {
        try {
            String url = "http://etr.gov.ar/ajax/cuandollega/getSmsResponseEfisat.php";
            InputStream content = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("parada", parada.toString()));
            nameValuePairs.add(new BasicNameValuePair("linea", linea.toString()));
            nameValuePairs.add(new BasicNameValuePair("accion", "getSmsEfisat"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            content = response.getEntity().getContent();
            return parserResult(InputStreamToString(content));
        } catch (Exception e) {
            e.printStackTrace();
            ArrayList<String> ls = new ArrayList<String>(); ls.add("Lo sentimos. Fallo la conexion");
            return ls;}

    }

    public static String InputStreamToString(InputStream in)
    {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e){return "";}
    }

}
