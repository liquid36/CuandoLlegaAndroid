package com.samsoft.cuandollega.Activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsoft.cuandollega.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArrivalsActivityFragment extends Fragment {

    public ArrivalsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_arrivals, container, false);
    }
}
