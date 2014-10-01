package com.samsoft.cuandollega.extra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by sam on 30/09/14.
 */

public class SMSDialog extends  AlertDialog.Builder {
    public SMSDialog(Context c, final String Mensaje)
    {
        super(c);
        this.setTitle("Enviar SMS");
        this.setMessage("Quieres enviar un Mensaje?");

        setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SmsManager smsManager = SmsManager.getDefault();
                Log.d("SEND SMS", Mensaje);
                smsManager.sendTextMessage("22522", null, Mensaje, null, null);
                dialog.dismiss();
            }
        });
        setNegativeButton("Cancelar",null);
    }

    public void AskSend()
    {
        AlertDialog a = this.create();
        a.show();
    }



}
