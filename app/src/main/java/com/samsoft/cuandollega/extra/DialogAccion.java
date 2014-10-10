package com.samsoft.cuandollega.extra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by sam on 30/09/14.
 */

public class DialogAccion extends  AlertDialog.Builder {
    public DialogAccion(Context c, String t, String m, String btok, String btcancel, final Action ac )
    {
        super(c);
        this.setTitle(t);
        if (!m.isEmpty()) this.setMessage(m);

        setPositiveButton(btok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ac.Run();
                dialog.dismiss();
            }
        });
        setNegativeButton(btcancel,null);
    }

    public void Show()
    {
        AlertDialog a = this.create();
        a.show();
    }

}
