package com.example.roadsplit.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.R;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.model.CurrentUserData;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseResponse;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class UebersichtRecAdapter extends RecyclerView.Adapter<UebersichtRecAdapter.RecentsViewHolder> implements Observer {

    private Context context;
    private List<ReiseResponse> recentsDataList;
    private Map<String, Bitmap> imageMap;
    private RecentsViewHolder viewHolder;

    public UebersichtRecAdapter(Context context, List<ReiseResponse> recentsDataList, Map<String, Bitmap> imageMap) {
        MainActivity.currentUserData.addObserver(this);
        this.context = context;
        this.recentsDataList = recentsDataList;
        this.imageMap = imageMap;
    }

    @NonNull
    @Override
    public RecentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reiseuebersichtlist, parent, false);
        // here we create a recyclerview row item layout file
        this.viewHolder = new RecentsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecentsViewHolder holder, int position) {

        ReiseResponse reiseResponse = recentsDataList.get(position);
        setupUi(holder, reiseResponse);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AusgabenActivity.class);
                String reiseString = new Gson().toJson(reiseResponse.getReise());
                intent.putExtra("reise", reiseString);
                context.startActivity(intent);
            }
        });

    }

    private void setupUi(RecentsViewHolder holder, ReiseResponse reiseResponse)
    {
        Reise reise = reiseResponse.getReise();

        holder.name.setText(reise.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormat.format(new Date(reise.getCreateDate()));
        holder.date.setText(dateString);
        if (reiseResponse.getGesamtAusgabe() != null)
            holder.ausgaben.setText("Ausgaben: " + reiseResponse.getGesamtAusgabe().toString() + "€");
        holder.anzahlReisende.setText("Reisende: " + Integer.toString(reiseResponse.getReisendeList().size()));
        BigDecimal gesamtBudget = new BigDecimal(0);
        for(Stop stop : reise.getStops())
        {
            if(stop.getBudget() != null)
                gesamtBudget = gesamtBudget.add(stop.getBudget());
        }
        holder.budget.setText("Budget: " + gesamtBudget.toString() + "€");
        holder.image.setImageBitmap(imageMap.get(reise.getName()));
        holder.ersteller.setText("Erstellt von: " + reise.getReiseErsteller());

        if(gesamtBudget != null &&
                !gesamtBudget.equals(new BigDecimal(0))
                && gesamtBudget.compareTo(reiseResponse.getGesamtAusgabe()) < 0){
            holder.ausgaben.setTextColor(Color.RED);
        }

    }


    @Override
    public int getItemCount() {
        return recentsDataList.size();
    }

    @Override
    public void update(Observable observable, Object o) {
    }

    public static final class RecentsViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView date;
        TextView ausgaben;
        TextView anzahlReisende;
        TextView budget;
        TextView ersteller;
        ImageView image;

        public RecentsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.uebersichtTextViewName);
            date = itemView.findViewById(R.id.uebersichtTextViewDatum);
            ausgaben = itemView.findViewById(R.id.uebersichtTextViewAusgaben);
            anzahlReisende = itemView.findViewById(R.id.uebersichtTextViewReisende);
            budget = itemView.findViewById(R.id.uebersichtTextViewBudget);
            ersteller = itemView.findViewById(R.id.uebersichtTextViewErsteller);
            image = itemView.findViewById(R.id.locationImageView);

        }
    }


}
