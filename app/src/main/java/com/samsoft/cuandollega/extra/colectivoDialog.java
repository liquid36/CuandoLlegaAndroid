package com.samsoft.cuandollega.extra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.samsoft.cuandollega.DataBase;

/**
 * Created by sam on 20/11/15.
 */
public class colectivoDialog {
    private AlertDialog alert;
    public colectivoDialog(Context c,DataBase db) {

        final CharSequence[] items = {
                "Rajesh", "Mahesh", "Vijayakumar"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Make your selection");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection

            }
        });
        alert = builder.create();
    }

    public void show()
    {
        alert.show();
    }


}
