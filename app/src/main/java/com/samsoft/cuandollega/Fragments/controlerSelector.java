package com.samsoft.cuandollega.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsoft.cuandollega.R;

/**
 * Created by sam on 21/05/15.
 */
public class controlerSelector extends Fragment implements  actionSelect.actionSelectListener{
    private static final String TAG = "controlerSelector";

    public controlerSelector() {}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        actionSelect actions = new actionSelect();
        actions.setListener(this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.frame, actions).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.controler_selector, container, false);
        return v;
    }


    public void OnActionClick(String action) {
        Log.d(TAG,action);
    }
}

