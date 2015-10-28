package com.samsoft.cuandollega.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;

/**
 * Created by sam on 21/05/15.
 */
public class mapActionSelector extends Fragment {
    public static final String RECORRIDO_CLICK = "RECORRIDOS_CLICK";
    public actionSelectListener mListener;

    public mapActionSelector() {

    }

    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        Log.d("mapActionSelector","CREADO");
    }

    public void setListener(actionSelectListener listener) { mListener = listener;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_action_selector, container, false);
        LinearLayout btn_recorrido = (LinearLayout) v.findViewById(R.id.btn_recorridos);
        btn_recorrido.setTag(RECORRIDO_CLICK);
        btn_recorrido.setOnClickListener(click);

        return v;
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("actionSelect", "Enviando Accion " + (String) view.getTag() + "  " + mListener);
            if (mListener != null) mListener.OnActionClick((String) view.getTag());
        }
    };

    public interface actionSelectListener {
        void OnActionClick(String action);
    }

}

