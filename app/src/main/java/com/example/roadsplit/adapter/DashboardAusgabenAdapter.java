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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.R;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.AusgabenSumme;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseResponse;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DashboardAusgabenAdapter extends RecyclerView.Adapter<DashboardAusgabenAdapter.RecentsViewHolder> {

    private Context context;
    private List<Ausgabe> ausgaben;
    public DashboardAusgabenAdapter(Context context, List<Ausgabe> ausgaben) {
        this.context = context;
        this.ausgaben = ausgaben;
    }

    @NonNull
    @Override
    public RecentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ausgabenuebersichtlist, parent, false);
        // here we create a recyclerview row item layout file
        return new RecentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentsViewHolder holder, int position) {



        Result result = getAusgabeAndSumme(position);
        String zahler = result.getAusgabe().getZahlername();
        String schuldner = result.getAusgabenSumme().getReisenderName();

        holder.ausgabename.setText(result.getAusgabe().getAusgabenTyp().toString());
        holder.beschreibung.setText("Von: " + zahler + " An: " + schuldner);
        holder.ausgabe.setText(result.getAusgabenSumme().getBetrag() + "€");

    }

    @Override
    public int getItemCount() {
        int counter = 0;
        for(Ausgabe ausgabe : ausgaben)
        {
            counter = counter + ausgabe.getAusgabenSumme().size();
        }
        return counter;
    }

    public class Result {
        private Ausgabe ausgabe;
        private AusgabenSumme ausgabenSumme;

        public Result(Ausgabe ausgabe, AusgabenSumme ausgabenSumme) {
            this.ausgabe = ausgabe;
            this.ausgabenSumme = ausgabenSumme;
        }

        public Ausgabe getAusgabe() {
            return ausgabe;
        }

        public AusgabenSumme getAusgabenSumme() {
            return ausgabenSumme;
        }
    }

    public Result getAusgabeAndSumme(int counter) {
        int currentCounter = 0;
        for(Ausgabe ausgabe : ausgaben) {
            for(AusgabenSumme ausgabenSumme : ausgabe.getAusgabenSumme()) {
                currentCounter++;
                if(currentCounter == counter) {
                    return new Result(ausgabe, ausgabenSumme);
                }
            }
        }
        return null; // or throw an exception if the counter is out of bounds
    }

    public static final class RecentsViewHolder extends RecyclerView.ViewHolder{

        TextView ausgabename;
        TextView beschreibung;
        TextView ausgabe;

        public RecentsViewHolder(@NonNull View itemView) {
            super(itemView);

            ausgabename = itemView.findViewById(R.id.textViewDashName);
            beschreibung = itemView.findViewById(R.id.textViewDashDesc);
            ausgabe = itemView.findViewById(R.id.textViewDashPrice);

        }
    }
}

