package com.samsoft.cuandollega.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.extra.DialogAccion;
import com.samsoft.cuandollega.objects.settingRep;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sam on 7/03/16.
 */
public class CuantoTengo extends Fragment {
    public static final String TAG = "CuantoTengo";
    public static final String TARJETA_KEY = "TARJETA";
    public static final String DOCUMENTO_KEY = "DOCUMENTO";
    public static final String MESSAGE_KEY = "MESSAGE";
    public String message;
    public settingRep settings;
    public EditText txtTarjeta;
    public EditText txtDocumento;
    public TextView lbMsg;


    public void setMessage(String m) {
        message = m;
        lbMsg.setText(m);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MESSAGE_KEY,message);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.webview_fragment, container, false);
        final WebView web = (WebView) v.findViewById(R.id.webView);
        txtTarjeta = (EditText) v.findViewById(R.id.txt_tarjeta);
        txtDocumento = (EditText) v.findViewById(R.id.txt_documento);
        lbMsg = (TextView) v.findViewById(R.id.txtMensaje);

        settings = new settingRep(getActivity().getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(MESSAGE_KEY)) {
            setMessage(savedInstanceState.getString(MESSAGE_KEY));
        }

        if (settings.repo.contains(TARJETA_KEY)) {
            txtTarjeta.setText(settings.getString(TARJETA_KEY));
            txtDocumento.setText(settings.getString(DOCUMENTO_KEY));
        }

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setPluginState(WebSettings.PluginState.ON);
        web.getSettings().setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= 11) web.getSettings().setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= 16) web.getSettings().setAllowFileAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT >= 16) web.getSettings().setAllowUniversalAccessFromFileURLs(true);

        web.addJavascriptInterface(new WebViewJavaScriptInterface(getActivity().getApplicationContext()), "app");
        web.loadUrl("file:///android_asset/www/index.html");

        Button btn = (Button) v.findViewById(R.id.btn_consultar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckBox cbRecordar = (CheckBox) v.findViewById(R.id.cb_recordar);
                String nroTarj = txtTarjeta.getText().toString();
                String nroDNI = txtDocumento.getText().toString();

                if (nroTarj.length() == 17) {
                    if (cbRecordar.isChecked()) {
                        settings.putString(TARJETA_KEY,nroTarj);
                        settings.putString(DOCUMENTO_KEY,nroDNI);
                    }
                    nroTarj = nroTarj.substring(0,16);
                    web.loadUrl("javascript:ObtenerOperaciones(" + nroTarj + "," + nroDNI + ")");
                    setMessage("Obteniendo información...");
                } else {
                    setMessage("Numero de tarjeta inválido");
                }
            }
        });
        mostrarMensaje();
        return v;

    }

    public void mostrarMensaje()
    {
        settingRep settings = new settingRep(getActivity().getApplicationContext());
        Boolean first = settings.getBoolean("cuantoTengo");
        if (!first) {
            settings.putBoolean("cuantoTengo",true);
            new DialogAccion(getActivity(),"Cuando Llega Movil",
                    "Verisón de prueba del Cuanto Tengo.\nIngrese el número de tarjeta y DNI para saber el saldo de su tarjeta."
                    + "\nLos datos son suministrado por la municipalidad, sepa discupar si no están actualizados"
                    ,"Aceptar" ,"" , null).Show();
        }
    }

    public class WebViewJavaScriptInterface{

        private Context context;

        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context){
            this.context = context;
        }

        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        public void reponse(final String message){
            Log.d(TAG,message);


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d(TAG, message);
                            JSONObject o = new JSONObject(message);
                            Integer estado = o.getInt("estado");

                            if (estado == 0) {
                                JSONArray movimientos = o.getJSONArray("movimientos");
                                if (movimientos.length() > 0) {
                                    String saldo = movimientos.getJSONObject(0).getString("saldo");
                                    String fecha = movimientos.getJSONObject(0).getString("fecha");

                                    setMessage("Saldo: " + saldo + "\nUltimo: " + fecha );
                                } else {
                                    setMessage("No hay movimientos en la tarjeta");
                                }
                            } else {
                                setMessage(o.getString("mensaje"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


            //Toast.makeText(context, message, (lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
        }
    }


}

