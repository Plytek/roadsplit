package com.example.roadsplit.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.R;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.AusgabenTyp;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.model.finanzen.Schulden;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

public class DashboardAusgabenAdapter extends RecyclerView.Adapter<DashboardAusgabenAdapter.RecentsViewHolder> {

    private Context context;
    private String type;
    private AusgabenReport ausgabenReport;
    private TextView summe;

    private SharedPreferences reportPref;

    public DashboardAusgabenAdapter(Context context, AusgabenReport ausgabenReport, View layoutScreen, String type) {
        this.context = context;
        this.ausgabenReport = ausgabenReport;
        this.type = type;
        summe = layoutScreen.findViewById(R.id.textViewDashBetrag);
        this.reportPref = context.getSharedPreferences("report", MODE_PRIVATE);

        this.reportPref.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if(key.equals("report"))
                    {

                    }
            }
        });
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
        switch (type) {
            case "privatAusgabe": {
                Ausgabe ausgabe = ausgabenReport.getAusgabenFuerSelbst().get(position);
                if(ausgabe.getNotiz() == null) holder.ausgabename.setText("Keine Notiz!");
                else holder.ausgabename.setText(ausgabe.getNotiz());
                holder.beschreibung.setText("");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
                    String dateString = formatter.format(ausgabe.getErstellDatum());
                    holder.date.setText(dateString);
                } catch (Exception e) {
                    holder.date.setText("");
                }
                setupAusgabenImage(holder, ausgabe.getAusgabenTyp());
                summe.setText(ausgabenReport.getPrivateAusgaben() + "€");
                holder.ausgabe.setText(ausgabe.getBetrag() + "€");
                break;
            }
            case "gruppenAusgabe": {
                Ausgabe ausgabe = ausgabenReport.getAusgabenFuerGruppe().get(position);
                if(ausgabe.getNotiz() == null) holder.ausgabename.setText("Keine Notiz!");
                else holder.ausgabename.setText(ausgabe.getNotiz());
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
                    String dateString = formatter.format(ausgabe.getErstellDatum());
                    holder.date.setText(dateString);
                } catch (Exception e) {
                    holder.date.setText("");
                }
                setupAusgabenImage(holder, ausgabe.getAusgabenTyp());
                if(ausgabe.getZahler() == ausgabe.getSchuldner()) holder.beschreibung.setText("Für dich selbst!");
                else holder.beschreibung.setText("Von: " + ausgabe.getZahlerName() + " An: " + ausgabe.getSchuldnerName());
                holder.ausgabe.setText(ausgabe.getBetrag() + "€");
                summe.setText(ausgabenReport.getPersoenlicheGruppenausgaben() + "€");
                break;
            }
            case "schulden":
                Schulden schulden = ausgabenReport.getSchuldenReport().get(position);
                BigDecimal betrag = schulden.getSchulden();
                if (betrag.compareTo(BigDecimal.ZERO) < 0){
                    holder.ausgabename.setText("Du schuldest");
                    holder.beschreibung.setText(schulden.getMitreisenderName());
                    holder.ausgabe.setText(betrag.abs() + "€");
                }
                else
                {
                    holder.ausgabename.setText("Du bekommst von");
                    holder.beschreibung.setText(schulden.getMitreisenderName());
                    holder.ausgabe.setText(betrag + "€");
                }
                holder.date.setText("");
                summe.setText(ausgabenReport.getGruppenGesamtausgabe() + "€");
                break;
        }

    }

    private void setupAusgabenImage(RecentsViewHolder holder, AusgabenTyp ausgabenTyp)
    {
        switch (ausgabenTyp){
            case Restaurants:
                holder.kategorie.setImageResource(R.drawable.spaghettismall);
                break;
            case Transport:
                holder.kategorie.setImageResource(R.drawable.trainsmall);
                break;
            case Tanken:
                holder.kategorie.setImageResource(R.drawable.fuelpumpsmall);
                break;
            case Unterkunft:
                holder.kategorie.setImageResource(R.drawable.bedsmall);
                break;
            case Shopping:
                holder.kategorie.setImageResource(R.drawable.shoppingsmall);
                break;
            case Aktivitaeten:
                holder.kategorie.setImageResource(R.drawable.clappersmall);
                break;
            case Gebuehren:
                holder.kategorie.setImageResource(R.drawable.creditcardsmall);
                break;
            default:
                holder.kategorie.setImageResource(R.drawable.moneywingssmall);
                break;
        }
    }

    @Override
    public int getItemCount() {
        switch (type)
        {
        case "privatAusgabe": {
            try {
                return ausgabenReport.getAusgabenFuerSelbst().size();
            } catch (Exception e) {
                return 0;
            }
        }
        case "gruppenAusgabe": {
            return ausgabenReport.getAusgabenFuerGruppe().size();
        }
        case "schulden": {
            return ausgabenReport.getSchuldenReport().size();
        }
        default: return ausgabenReport.getAusgabenFuerSelbst().size();
        }
    }

    public static final class RecentsViewHolder extends RecyclerView.ViewHolder{

        TextView ausgabename;
        TextView beschreibung;
        TextView ausgabe;
        TextView date;
        ImageView kategorie;

        public RecentsViewHolder(@NonNull View itemView) {
            super(itemView);

            ausgabename = itemView.findViewById(R.id.textViewDashName);
            beschreibung = itemView.findViewById(R.id.textViewDashDesc);
            ausgabe = itemView.findViewById(R.id.textViewDashPrice);
            date = itemView.findViewById(R.id.textViewDashSchuldner);
            kategorie = itemView.findViewById(R.id.ausgabenTypImageView);

        }
    }
}

