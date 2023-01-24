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
    private List<Reisender> reisende;
    public DashboardAusgabenAdapter(Context context, List<Ausgabe> ausgaben, List<Reisender> reisende) {
        this.context = context;
        this.ausgaben = ausgaben;
        this.reisende = reisende;
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

        Ausgabe ausgabe = ausgaben.get(position);
        String zahler = null;
        for (Iterator<Reisender> iterator = reisende.iterator(); iterator.hasNext(); ) {
            Reisender reisender = iterator.next();
            if (reisender.getId() == ausgabe.getZahler()) {
                zahler = reisender.getNickname();
                break;
            }
        }
        String schulder = null;
        for (Iterator<Reisender> iterator = reisende.iterator(); iterator.hasNext(); ) {
            Reisender reisender = iterator.next();
            if (reisender.getId() == ausgabe.getSchuldner()) {
                schulder = reisender.getNickname();
                break;
            }
        }
        if(schulder == null || zahler == null) return;

        holder.ausgabename.setText(ausgabe.getAusgabenTyp().toString());
        holder.beschreibung.setText("Von: " + zahler + " An: " + schulder);
        holder.ausgabe.setText(ausgabe.getBetrag() + "€");

    }

    @Override
    public int getItemCount() {
        return ausgaben.size();
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

