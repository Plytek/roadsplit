package com.example.roadsplit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.R;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseReponse;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UebersichtRecAdapter extends RecyclerView.Adapter<UebersichtRecAdapter.RecentsViewHolder> {

    Context context;
    List<ReiseReponse> recentsDataList;

    public UebersichtRecAdapter(Context context, List<ReiseReponse> recentsDataList) {
        this.context = context;
        this.recentsDataList = recentsDataList;
    }

    @NonNull
    @Override
    public RecentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.reiseuebersichtlist, parent, false);

        // here we create a recyclerview row item layout file
        return new RecentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentsViewHolder holder, int position) {

        ReiseReponse reiseReponse = recentsDataList.get(position);
        Reise reise = reiseReponse.getReise();

        holder.name.setText(reise.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormat.format(new Date(reise.getCreateDate()));
        holder.date.setText(dateString);
        if (reiseReponse.getGesamtAusgabe() != null)
            holder.ausgaben.setText(reiseReponse.getGesamtAusgabe().toString());
        holder.anzahlReisende.setText(reiseReponse.getReisendeList().size());
        BigDecimal gesamtBudget = new BigDecimal(0);
        for(Stop stop : reise.getStops())
        {
            if(stop.getBudget() != null)
                gesamtBudget = gesamtBudget.add(stop.getBudget());
        }
        holder.budget.setText(gesamtBudget.toString());
        //holder.image.setImageResource(recentsDataList.get(position).getImageUrl(arsch));

        if(gesamtBudget != null &&
                !gesamtBudget.equals(new BigDecimal(0))
                && gesamtBudget.compareTo(reiseReponse.getGesamtAusgabe()) < 0){
            holder.ausgaben.setTextColor(context.getResources().getColor(R.color.red));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Intent i=new Intent(context, DetailsActivity.class);
             //   context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recentsDataList.size();
    }

    public static final class RecentsViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView date;
        TextView ausgaben;
        TextView anzahlReisende;
        TextView budget;
        ImageView image;

        public RecentsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.uebersichtTextViewName);
            date = itemView.findViewById(R.id.uebersichtTextViewDatum);
            ausgaben = itemView.findViewById(R.id.uebersichtTextViewAusgaben);
            anzahlReisende = itemView.findViewById(R.id.uebersichtTextViewReisende);
            budget = itemView.findViewById(R.id.uebersichtTextViewBudget);
            image = itemView.findViewById(R.id.locationImageView);

        }
    }
}

