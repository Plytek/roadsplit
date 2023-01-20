package com.example.roadsplit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.activities.ReiseUebersichtActivity;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseReponse;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UebersichtListAdapter extends ArrayAdapter<Bitmap> {

    private List<Bitmap> reisenWithImages;
    private List<ReiseReponse> reiseReponses;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView date;
        TextView ausgaben;
        TextView anzahlReisende;
        TextView budget;
        ImageView image;
    }

    public UebersichtListAdapter(List<ReiseReponse> reiseReponses, Context context, List<Bitmap> data) {
        super(context, R.layout.reiseuebersichtlist, data);
        this.reisenWithImages = data;
        this.reiseReponses = reiseReponses;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Bitmap image = getItem(position);
        ReiseReponse reiseReponse = reiseReponses.get(position);
        Reise reise = reiseReponse.getReise();
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
        viewHolder.anzahlReisende.setText("Reise-Teilnehmer: " + reiseReponse.getReisendeList().size());
        viewHolder.ausgaben.setText("Ausgaben: " + reiseReponse.getGesamtAusgabe() + "€");
        viewHolder.budget.setText("Budget: " + gesamtBudget + "€");

        //Log.d("bigdecimal", )
        if(gesamtBudget != null &&
                !gesamtBudget.equals(new BigDecimal(0))
            && gesamtBudget.compareTo(reiseReponse.getGesamtAusgabe()) < 0){
            viewHolder.ausgaben.setTextColor(Color.RED);
        }

        if(reisenWithImages.get(position) != null) {
            viewHolder.image.setImageBitmap(reisenWithImages.get(position));
        }
        // Return the completed view to render on screen
        return convertView;
    }

}
