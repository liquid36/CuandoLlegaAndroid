package com.samsoft.cuandollega.extra;

import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by sam on 10/10/14.
 */
public class SMSAction implements Action {
    private String mensaje;
    public SMSAction(String m)
    {
        mensaje = m;
    }

    public void Run()
    {
        SmsManager smsManager = SmsManager.getDefault();
        Log.d("SEND SMS", mensaje);
        smsManager.sendTextMessage("22522", null, mensaje, null, null);
    }
}
