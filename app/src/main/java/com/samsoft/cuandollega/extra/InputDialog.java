package com.samsoft.cuandollega.extra;

/**
 * Created by sam on 16/05/15.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samsoft.cuandollega.R;

public class InputDialog {
    private EditText txt;
    private AlertDialog.Builder alert;
    public InputDialog (Context c,String title,String desc, final inputDialogListener listener){
        alert = new AlertDialog.Builder(c);
        final LinearLayout view =  (LinearLayout) ((LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.input_text,null);
        txt = (EditText) view.findViewById(R.id.input);

        txt.setHint(desc);
        alert.setTitle(title);
        alert.setView(view);

        alert.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                listener.onAcceptClick(txt.getText().toString());
            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                listener.onCancelClick();
            }
        });
    }

    public void show() {
        alert.show();
    }

    public interface inputDialogListener {
        void onAcceptClick(String txt);
        void onCancelClick();
    }

}

