package com.samsoft.cuandollega.extra;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.objects.stopsGroup;
import com.samsoft.cuandollega.paradasinfo;

import org.json.JSONArray;

/**
 * Created by sam on 20/11/15.
 */
public class colectivoDialog {
    private AlertDialog alert;
    public colectivoDialog(final Context c,DataBase db,ContentValues values) {
        final Integer idCalle = values.getAsInteger("idCalle");
        final Integer idInter = values.getAsInteger("idInter");
        String name1 = db.getCalleName(idCalle);
        String name2 = db.getCalleName(idInter);

        final JSONArray arr = db.busInStop(idCalle,idInter);
        final CharSequence[] items = new CharSequence[arr.length() + 1];
        items[0] = " - TODOS - ";
        try {
            for (int i = 0; i < arr.length(); i++)
                items[i + 1] = arr.getJSONObject(i).getString("name");
        } catch (Exception e) {e.printStackTrace();}

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(name1 + " y " + name2);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent i = new Intent(c, paradasinfo.class);
                String colectivo = "";
                if (item > 0) {
                    try {
                        colectivo = arr.getJSONObject(item - 1).getString("linea");
                    } catch (Exception e) {e.printStackTrace();}
                }
                stopsGroup r[] = stopsGroup.addItem(new stopsGroup[]{},new stopsGroup(idCalle,idInter,colectivo,0));
                i.putExtra("Stops",stopsGroup.stopsToString(r));
                c.startActivity(i);

            }
        });
        alert = builder.create();
    }

    public void show()
    {
        alert.show();
    }


}
