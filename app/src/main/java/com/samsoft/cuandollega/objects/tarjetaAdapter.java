package com.samsoft.cuandollega.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.samsoft.cuandollega.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by sam on 15/10/16.
 */

public class tarjetaAdapter extends ArrayAdapter<JSONObject> {
    public interface  tarjetaAdapterListener {
        public void OnItemClick(Integer position);
        public void OnItemLongClick(Integer position);
    }

    private final Context context;
    private List<JSONObject> values;
    private tarjetaAdapterListener events;

    public tarjetaAdapter(Context context, List<JSONObject> values, tarjetaAdapterListener events)
    {
        super(context, R.layout.itemlist_cuantotengo_tarjeta , values);
        this.context = context;
        this.values = values;
        this.events = events;
    }
    public void setData(List<JSONObject> list)
    {
        values = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.itemlist_cuantotengo_tarjeta, parent, false);

        TextView txt_dni = (TextView) rowView.findViewById(R.id.tarjeta_dni);
        TextView txt_numero = (TextView) rowView.findViewById(R.id.tarjeta_numero);

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
        try {
            String numero = values.get(position).getString("tarjeta");
            String dni = values.get(position).getString("dni");
            txt_dni.setText(dni);
            txt_numero.setText(numero);
        } catch (Exception e)  {
            e.printStackTrace();
        }

        return rowView;
    }
}
