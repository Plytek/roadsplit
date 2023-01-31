package com.example.roadsplit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.model.Stop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class StopAdapter extends ArrayAdapter<Stop> {

    private List<Stop> dataSet;
    Context mContext;


    // View lookup cache
    private static class ViewHolder {
        TextView stops;
        TextView ausgaben;
        TextView budget;
    }

    public StopAdapter(List<Stop> data, Context context) {
        super(context, R.layout.row_layout, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Stop stop = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_layout, parent, false);
            viewHolder.stops = (TextView) convertView.findViewById(R.id.text_view_stops);
            viewHolder.ausgaben = (TextView) convertView.findViewById(R.id.text_view_ausgaben);
            viewHolder.budget = (TextView) convertView.findViewById(R.id.text_view_budget);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(stop.getGesamtausgaben() == null) stop.setGesamtausgaben(new BigDecimal(0));
        if(stop.getBudget()== null) stop.setBudget(new BigDecimal(0));

        viewHolder.stops.setText(stop.getName());
        viewHolder.ausgaben.setText(stop.getGesamtausgaben().toString());
        viewHolder.budget.setText(stop.getBudget().toString());

        // Return the completed view to render on screen
        return convertView;
    }
}
