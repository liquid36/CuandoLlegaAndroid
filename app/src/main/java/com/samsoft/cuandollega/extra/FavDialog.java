package com.samsoft.cuandollega.extra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private ImageView img;
    private Context cc;
    private LayoutInflater inflater;
    final ArrayList checkList = new ArrayList();
    LinearLayout v;
    public FavDialog(Context c, DataBase d, String l,Integer p,ImageView ii)
    {
        super(c);
        cc = c;
        img = ii;
        linea = l;
        parada = p;
        db = d;
        ar = db.getFavoritos();

        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View f = (LinearLayout) inflater.inflate(R.layout.translayout,null);
        v = (LinearLayout) f.findViewById(R.id.listItems);
        this.setView(f);

        try {
            for (int i = 0; i < ar.length(); i++) {
                Integer id = (Integer) ar.getJSONObject(i).getInt("id") ;
                String item = ar.getJSONObject(i).getString("name");
                Boolean cbool = db.isFavCheck(id,linea,parada);
                addCheckRow(id,item,cbool);
            }

        } catch (Exception e) {}
        addEditRow();

        this.setTitle("Etiquetas");

        setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                db.deleteFavorito(linea, parada);
                for (int i = 0; i < checkList.size(); i++) {
                    CheckBox cb = (CheckBox) checkList.get(i);
                    Integer id = (Integer) cb.getTag();
                    if (cb.isChecked()) db.insertFavList(id, linea, parada);
                }
                if (db.chekcFavorito(linea, parada))
                    img.setImageDrawable(cc.getResources().getDrawable(R.drawable.ic_action_important));
                else
                    img.setImageDrawable(cc.getResources().getDrawable(R.drawable.ic_action_not_important));
                img.invalidate();
            }
        });

        setNegativeButton("Cancelar",null);
    }

    public void addCheckRow(Integer id , String name, Boolean b)
    {
        LinearLayout cview = (LinearLayout) inflater.inflate(R.layout.checkrow,null);
        TextView txt = (TextView) cview.findViewById(R.id.txtrow);
        CheckBox cb = (CheckBox) cview.findViewById(R.id.cbrow);
        txt.setText(name);
        cb.setChecked(b);
        cb.setTag(id);
        checkList.add(cb);
        v.addView(cview);
    }

    public void addEditRow()
    {
        final LinearLayout eview = (LinearLayout) inflater.inflate(R.layout.entryrow,null);
        ImageButton btn = (ImageButton) eview.findViewById(R.id.btn);
        final EditText txt = (EditText) eview.findViewById(R.id.texto);
        v.addView(eview);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = txt.getText().toString();
                v.removeView(eview);
                Integer id = db.addFavorito(newName);
                addCheckRow(id,newName,true);
                addEditRow();
            }
        });
    }

    public void Show()
    {
        AlertDialog a = this.create();
        a.show();
    }

}
