package com.samsoft.cuandollega.Fragments;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.samsoft.cuandollega.R;

import org.json.JSONObject;

/**
 * Created by sam on 7/03/16.
 */
public class CuantoTengo extends Fragment {
    public static final String TAG = "CuantoTengo";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.webview_fragment, container, false);
        final WebView web = (WebView) v.findViewById(R.id.webView);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setPluginState(WebSettings.PluginState.ON);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setAllowContentAccess(true);
        web.getSettings().setAllowFileAccessFromFileURLs(true);
        web.getSettings().setAllowUniversalAccessFromFileURLs(true);
        web.addJavascriptInterface(new WebViewJavaScriptInterface(getActivity().getApplicationContext()), "app");
        web.loadUrl("file:///android_asset/www/index.html");
        web.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                web.loadUrl("javascript:ObtenerOperaciones(4241020100127748,33069425)");
                Log.d(TAG,"Web View" + url);
            }
        });
        return v;
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
        public void reponse(String message){
            Log.d(TAG,message);
            try {
                JSONObject o = new JSONObject(message);
                Integer estado = o.getInt("estado");
                Log.d(TAG,"Estado de la peticion" + estado.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Toast.makeText(context, message, (lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
        }
    }


}
