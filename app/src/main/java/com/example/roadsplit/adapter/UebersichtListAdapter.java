package com.example.roadsplit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.model.CurrentUserData;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseResponse;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class UebersichtListAdapter extends ArrayAdapter<Bitmap> implements Observer {

    private List<Bitmap> reisenWithImages;
    private List<ReiseResponse> reiseRepons;
    private Context context;

    @Override
    public void update(Observable observable, Object o) {
        if(o instanceof CurrentUserData){
            //reiseReponses = ((CurrentUserData) o).getCurrentReiseReponsesAsList();
        }
    }

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView date;
        TextView ausgaben;
        TextView anzahlReisende;
        TextView budget;
        ImageView image;
    }

    public UebersichtListAdapter(List<ReiseResponse> reiseRepons, Context context, List<Bitmap> data) {
        super(context, R.layout.reiseuebersichtlist, data);
        MainActivity.currentUserData.addObserver(this);
        this.reisenWithImages = data;
        this.reiseRepons = MainActivity.currentUserData.getCurrentReiseReponsesAsList();
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Bitmap image = getItem(position);
        ReiseResponse reiseResponse = null;
        try {
            reiseResponse = reiseRepons.get(position);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return convertView;
        }
        Reise reise = reiseResponse.getReise();
        // Check if an existing view is being reused, otherwise inflate the view
        UebersichtListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new UebersichtListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.reiseuebersichtlist, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.uebersichtTextViewName);
            viewHolder.date = (TextView) convertView.findViewById(R.id.uebersichtTextViewDatum);
            viewHolder.anzahlReisende = (TextView) convertView.findViewById(R.id.uebersichtTextViewReisende);
            viewHolder.ausgaben = (TextView) convertView.findViewById(R.id.uebersichtTextViewAusgaben);
            viewHolder.budget = (TextView) convertView.findViewById(R.id.uebersichtTextViewBudget);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.locationImageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (UebersichtListAdapter.ViewHolder) convertView.getTag();
        }
        BigDecimal gesamtBudget = new BigDecimal(0);
        for(Stop stop : reise.getStops())
        {
            if(stop.getBudget() != null)
                gesamtBudget = gesamtBudget.add(stop.getBudget());
        }


        viewHolder.name.setText(reise.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormat.format(new Date(reise.getCreateDate()));
        viewHolder.date.setText(dateString);
        viewHolder.anzahlReisende.setText("Reise-Teilnehmer: " + reiseResponse.getReisendeList().size());
        viewHolder.ausgaben.setText("Ausgaben: " + reiseResponse.getGesamtAusgabe() + "€");
        viewHolder.budget.setText("Budget: " + gesamtBudget + "€");

        //Log.d("bigdecimal", )
        if(gesamtBudget != null &&
                !gesamtBudget.equals(new BigDecimal(0))
            && gesamtBudget.compareTo(reiseResponse.getGesamtAusgabe()) < 0){
            viewHolder.ausgaben.setTextColor(context.getResources().getColor(R.color.rcreme));
        }

        if(reisenWithImages.get(position) != null) {
            viewHolder.image.setImageBitmap(reisenWithImages.get(position));
        }
        // Return the completed view to render on screen
        return convertView;
    }

}
