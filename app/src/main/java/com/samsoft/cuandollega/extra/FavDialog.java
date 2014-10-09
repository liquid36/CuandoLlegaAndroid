package com.samsoft.cuandollega.extra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import com.samsoft.cuandollega.DataBase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam on 08/10/14.
 */
public class FavDialog extends  AlertDialog.Builder
{
    public Integer parada;
    public String linea;
    private DataBase db;
    private JSONArray ar;
    final ArrayList seletedItems=new ArrayList();
    CharSequence[] items = new CharSequence[] {};
    public FavDialog(Context c, DataBase d, String l,Integer p)
    {
        super(c);
        linea = l;
        parada = p;
        db = d;
        ar = db.getFavoritos();
        items = new CharSequence[ar.length()];
        boolean[] cbool = new boolean[ar.length()];
        try {
            for (int i = 0; i < ar.length(); i++) {
                Integer id = (Integer) ar.getJSONObject(i).getInt("id") ;
                items[i] = ar.getJSONObject(i).getString("name");
                cbool[i] = db.isFavCheck(id,linea,parada);
            }

        } catch (Exception e) {}


        this.setTitle("Etiquetas");
        this.setMultiChoiceItems(items, cbool,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }
        );

        setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                db.deleteFavorito(linea,parada);
                for(int i = 0;i < seletedItems.size();i++)
                    try {
                        JSONObject o = ar.getJSONObject( (Integer) seletedItems.get(i));
                        db.insertFavList(o.getInt("id"),linea,parada);
                    } catch (Exception e) {}
            }
        });
        setNegativeButton("Cancelar",null);
    }

    public void Show()
    {
        AlertDialog a = this.create();
        a.show();
    }
}
