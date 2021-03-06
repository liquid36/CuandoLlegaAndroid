package com.samsoft.cuandollega.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samsoft.cuandollega.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by sam on 17/05/15.
 */
public class calleAdapter extends ArrayAdapter<JSONObject> {
    private final Context context;
    private calleAdapterListener events;
    private List<JSONObject> values;

    public calleAdapter(Context context, List<JSONObject> values,calleAdapterListener events) {
        super(context, R.layout.markrow , values);
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
        View rowView = inflater.inflate(R.layout.rowsimple, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
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
            textView.setText(values.get(position).getString("desc"));
        } catch (Exception e) {
            e.printStackTrace();
            textView.setText("Error en el nombre");
        }
        return rowView;
    }

    public interface  calleAdapterListener {
        public void OnItemClick(Integer position);
        public void OnItemLongClick(Integer position);
    }
}
