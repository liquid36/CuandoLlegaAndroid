package com.samsoft.cuandollega.objects;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by sam on 06/04/15.
 */
public class locationListener implements LocationListener {
    private settingRep settings;
    private Context c;
    public locationListener(Context _c,settingRep s)
    {
        c = _c;
        settings = s;
    }

    @Override
    public void onLocationChanged(Location l) {
        settings.putString("lat",((Double) l.getLatitude()).toString());
        settings.putString("lng",((Double) l.getLongitude()).toString());
        settings.putString("precision",((Float) l.getAccuracy()).toString());
        Log.d("geoActivity", l.getLatitude() + "  " + l.getLongitude() + " " + l.getProvider() + " " + l.getAccuracy());
    }

    @Override
    public void onProviderDisabled(String provider) {
        // called when the GPS provider is turned off (user turning off the GPS on the phone)
    }

    @Override
    public void onProviderEnabled(String provider) {
        // called when the GPS provider is turned on (user turning on the GPS on the phone)
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
