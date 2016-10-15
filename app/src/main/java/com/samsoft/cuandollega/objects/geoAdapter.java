package com.samsoft.cuandollega.objects;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.samsoft.cuandollega.DataBase;
import com.samsoft.cuandollega.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by sam on 29/05/15.
 */
public class geoAdapter extends ArrayAdapter<ContentValues> {
    private final Context context;
    private geoAdapterListener events;
    private List<ContentValues> values;
    private DataBase db;

    public geoAdapter(Context context, List<ContentValues> values,DataBase db,geoAdapterListener events) {
        super(context, R.layout.markrow , values);
        this.context = context;
        this.values = values;
        this.events = events;
        this.db = db;
    }

    public void setData(List<ContentValues> list)
    {
        values = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        db = new DataBase(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.georow, parent, false);
        try {
            ContentValues o = values.get(position);
            String calle1 = o.getAsString("name1");
            String calle2 = o.getAsString("name2");
            String colectivos = db.colectivosEnEsquina(o.getAsInteger("idCalle"), o.getAsInteger("idInter"));

            TextView txtCalles = (TextView) rowView.findViewById(R.id.txtCalles);
            TextView txtDist = (TextView) rowView.findViewById(R.id.txtDist);
            TextView txtColectivos = (TextView) rowView.findViewById(R.id.txtColectivos);

            txtCalles.setText(calle1 + " y " + calle2);

            Double distancia = Math.acos(o.getAsDouble("distancia")) *  6371.0  * 1000.0;
            txtDist.setText("a " + Math.round(distancia) + "mts");
            txtColectivos.setText(colectivos);

            if (events != null) {
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        events.OnItemClick(position);
                    }
                });
                rowView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        events.OnItemLongClick(position);
                        return false;
                    }
                });
            }

        } catch (Exception e ) {e.printStackTrace();}
        db.Close();
        return rowView;
    }

    public interface  geoAdapterListener {
        public void OnItemClick(Integer position);
        public void OnItemLongClick(Integer position);
    }
}
