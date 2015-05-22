package com.samsoft.cuandollega.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;

/**
 * Created by sam on 21/05/15.
 */
public class actionSelect extends Fragment {
    public static final String CALLE_CLICK = "CALLE_CLICK";
    public static final String COLECTIVO_CLICK = "COLECTIVO_CLICK";
    public static final String RECIENTE_CLICK = "RECIENTE_CLICK";
    public static final String CLOSE_CLICK = "CLOSE_CLICK";
    public actionSelectListener mListener;

    public actionSelect() {

    }

    public void setListener(actionSelectListener listener) { mListener = listener;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.action_selector, container, false);
        LinearLayout btn_calle = (LinearLayout) v.findViewById(R.id.btn_calle);
        LinearLayout btn_colectivo = (LinearLayout) v.findViewById(R.id.btn_colectivo);
        LinearLayout btn_reciente = (LinearLayout) v.findViewById(R.id.btn_reciente);
        LinearLayout btn_close = (LinearLayout) v.findViewById(R.id.btn_close);
        btn_calle.setTag(CALLE_CLICK);
        btn_close.setTag(CLOSE_CLICK);
        btn_colectivo.setTag(COLECTIVO_CLICK);
        btn_reciente.setTag(RECIENTE_CLICK);
        btn_calle.setOnClickListener(click);
        btn_colectivo.setOnClickListener(click);
        btn_reciente.setOnClickListener(click);
        btn_close.setOnClickListener(click);
        return v;
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mListener != null) mListener.OnActionClick((String) view.getTag());
        }
    };

    public interface actionSelectListener {
        void OnActionClick(String action);
    }

}
