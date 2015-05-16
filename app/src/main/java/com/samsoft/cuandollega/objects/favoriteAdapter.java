package com.samsoft.cuandollega.objects;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.samsoft.cuandollega.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by sam on 16/05/15.
 */

public class favoriteAdapter extends ArrayAdapter<JSONObject> {
    private final Context context;
    private List<JSONObject> values;

    public favoriteAdapter(Context context, List<JSONObject> values) {
        super(context, R.layout.markrow , values);
        this.context = context;
        this.values = values;
    }

    public void setData(List<JSONObject> list)
    {
        values = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.markrow, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.txtName);

        try {
            textView.setText(values.get(position).getString("name"));
        } catch (Exception e) {
            e.printStackTrace();
            textView.setText("Error en el nombre");
        }
        return rowView;
    }
}
