package com.example.roadsplit.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roadsplit.R;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseResponse;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UebersichtListAdapter extends ArrayAdapter<Bitmap>{

    private List<Bitmap> reisenWithImages;
    private Reisender reisender;
    private SharedPreferences prefs;
    private Context context;


    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView date;
        TextView ausgaben;
        TextView anzahlReisende;
        TextView budget;
        ImageView image;
    }

    public UebersichtListAdapter(Context context, List<Bitmap> data) {
        super(context, R.layout.reiseuebersichtlist, data);
        this.reisenWithImages = data;
        this.context = context;
        prefs = context.getSharedPreferences("reisender", MODE_PRIVATE);
        reisender = new Gson().fromJson(prefs.getString("reisender", "fehler"), Reisender.class);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Bitmap image = getItem(position);
        Reise reise;
        try {
             reise = reisender.getReisen().get(position);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return convertView;
        }
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

        viewHolder.name.setText(reise.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormat.format(new Date(reise.getCreateDate()));
        viewHolder.date.setText(dateString);
        //TODO: Reiseteilnehmer
        //viewHolder.anzahlReisende.setText("Reise-Teilnehmer: " + reiseResponse.getReisendeList().size());

        BigDecimal gesamtBudget = reise.getGesamtBudget();
        viewHolder.ausgaben.setText("Ausgaben: " + reise.getGesamtAusgabe() + "€");
        viewHolder.budget.setText("Budget: " + gesamtBudget + "€");


        if(gesamtBudget != null &&
                !gesamtBudget.equals(new BigDecimal(0))
            && gesamtBudget.compareTo(reise.getGesamtAusgabe()) < 0){
            viewHolder.ausgaben.setTextColor(context.getResources().getColor(R.color.rcreme));
        }

        if(reisenWithImages.get(position) != null) {
            viewHolder.image.setImageBitmap(reisenWithImages.get(position));
        }
        // Return the completed view to render on screen
        return convertView;
    }

}
