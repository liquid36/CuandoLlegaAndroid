package com.samsoft.cuandollega.objects;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.samsoft.cuandollega.R;
import com.samsoft.cuandollega.classes.paradaInfo;

import java.util.List;

/**
 * Created by sam on 13/10/16.
 */

public class arrivalsAdapter extends ArrayAdapter<paradaInfo>{
    private Context context = null;
    private arrivalsAdapterListener events;
    private List<paradaInfo> values;

    public arrivalsAdapter(Context context, List<paradaInfo> values, arrivalsAdapterListener events) {
        super(context, R.layout.markrow , values);
        this.context = context;
        this.values = values;
        this.events = events;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.waitingrow, parent, false);

        TextView bus = (TextView) rowView.findViewById(R.id.txtBus);
        TextView dest = (TextView) rowView.findViewById(R.id.txtDest);
        TextView lugar = (TextView) rowView.findViewById(R.id.txtLugar);
        ProgressBar bar = (ProgressBar) rowView.findViewById(R.id.waitingbar);
        ImageView img = (ImageView) rowView.findViewById(R.id.actionIcon);

        LinearLayout arrivosPlace = (LinearLayout) rowView.findViewById(R.id.arrivosPlace);

        if (events != null) {
             img.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     events.OnStarClick(position);
                 }
             });
        }

        paradaInfo info = values.get(position);
        try {
            bus.setText(info.bus_name);
            dest.setText(info.destino);
            lugar.setText(info.getLugar());

            if (info.arrivos == null) {
                bar.setVisibility(View.VISIBLE);
                img.setVisibility(View.GONE);
            } else {
                bar.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);

                if (info.isFavorite) {
                    img.setImageDrawable(context.getResources().getDrawable(R.drawable.starfull));
                } else {
                    img.setImageDrawable(context.getResources().getDrawable(R.drawable.star_empty));
                }

                arrivosPlace.removeAllViews();
                for(String t:info.arrivos) {
                    TextView linea = new TextView(context);
                    linea.setText(t);
                    linea.setTextColor(Color.BLACK);
                    arrivosPlace.addView(linea);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowView;
    }


    public interface  arrivalsAdapterListener {
        public void OnStarClick(Integer position);
    }

}
