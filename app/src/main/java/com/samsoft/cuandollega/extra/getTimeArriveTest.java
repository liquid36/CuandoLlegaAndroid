package com.samsoft.cuandollega.extra;

import android.nfc.Tag;
import android.util.Log;

import com.samsoft.cuandollega.ExpandAnimation;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sam on 12/10/16.
 */

public class getTimeArriveTest {

    private Integer linea;
    private Integer parada;

    private JSONArray array;

    private static final String TAG = "getTimeArriveTest";
    public getTimeArriveTest(Integer lin, Integer par)
    {
        linea = lin;
        parada = par;
    }

    // Example : [{"LineaBandera":"Parada","latitud":"-32.920894","longitud":"-60.739138","arribo":""},{"LineaBandera":"115 UNICO Adaptado","bandera":"UNICO","latitud":"-32.91683","longitud":"-60.72803","arribo":"6 min","interno":"131","horaGPS":"12\/10\/2016 19:56:43"},{"LineaBandera":"115 AEROPUERTO Adaptado","bandera":"AEROPUERTO","latitud":"-32.93264","longitud":"-60.71521","arribo":"9 min","interno":"186","horaGPS":"12\/10\/2016 19:58:04"},{"LineaBandera":"115 UNICO","bandera":"UNICO","latitud":"-32.92816","longitud":"-60.72743","arribo":"11 min","interno":"178","horaGPS":"12\/10\/2016 19:58:08"},{"LineaBandera":"115 AEROPUERTO Adaptado","bandera":"AEROPUERTO","latitud":"-32.95463","longitud":"-60.63723","arribo":"44 min","interno":"200","horaGPS":"12\/10\/2016 19:57:56"}]);
    public JSONArray getInfo() {return array;}

    public ArrayList<String> parserResult(String datos)
    {
        String start_tag = "JSONcoordenadas = eval(";
        String end_tag = ");";
        ArrayList<String> result = new ArrayList<String>();

        int index = datos.indexOf(start_tag);
        if (index > 0) {
            index += start_tag.length();
            datos = datos.substring(index);
            Log.d(TAG,datos);

            int index_b = datos.indexOf(end_tag);
            if (index_b > 0) {
                datos = datos.substring(0,index_b);
                Log.d(TAG,datos);
                try {
                    array = new JSONArray(datos);

                    for (int i = 0; i < array.length(); i ++) {
                        JSONObject o = array.getJSONObject(i);
                        String linea = o.getString("LineaBandera");
                        if (linea.equals("Parada")) continue;

                        String resultado = "";
                        resultado += o.getString("LineaBandera") + " (" + o.getString("interno") + ") :  " + o.getString("arribo");

                        result.add(resultado);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else {
            result.add(datos);
        }

        return result;
    }


    public ArrayList<String> run()
    {
        try {
            String url = "http://www.etr.gov.ar/ajax/cuandollega/getSmsResponseEfisat.php";
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
            String result = InputStreamToString(content);
            Log.d("getTimeArrive",result);
            return parserResult(result);
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
