package com.samsoft.cuandollega.objects;

import android.content.Context;
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
public class geoAdapter extends ArrayAdapter<JSONObject> {
    private final Context context;
    private geoAdapterListener events;
    private List<JSONObject> values;
    private DataBase db;

    public geoAdapter(Context context, List<JSONObject> values,DataBase db,geoAdapterListener events) {
        super(context, R.layout.markrow , values);
        this.context = context;
        this.values = values;
        this.events = events;
        this.db = db;
    }

    public void setData(List<JSONObject> list)
    {
        values = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.georow, parent, false);
        try {
            JSONObject o = values.get(position);
            String calle1 = o.getString("name1");
            String calle2 = o.getString("name2");
            String colectivos = db.colectivosEnEsquina(o.getInt("idCalle"), o.getInt("idInter"));

            TextView txtCalles = (TextView) rowView.findViewById(R.id.txtCalles);
            TextView txtDist = (TextView) rowView.findViewById(R.id.txtDist);
            TextView txtColectivos = (TextView) rowView.findViewById(R.id.txtColectivos);

            txtCalles.setText(calle1 + " y " + calle2);
            txtDist.setText("a " + o.getInt("distancia") + "mts");
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
        return rowView;
    }

    public interface  geoAdapterListener {
        public void OnItemClick(Integer position);
        public void OnItemLongClick(Integer position);
    }
}
