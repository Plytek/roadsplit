package com.example.roadsplit.helperclasses;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.adapter.DashboardAusgabenAdapter;
import com.example.roadsplit.adapter.ReiseUebersichtAdapter;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.AusgabenSumme;
import com.example.roadsplit.model.AusgabenTyp;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.ReiseTeilnehmer;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseResponse;
import com.example.roadsplit.requests.AusgabenRequest;
import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AusgabenAdapterHelper{

        private Context mContext;
        private View layoutScreen;
        private AusgabenActivity ausgabenActivity;
        private List<String> reisendeNames;
        private ReiseUebersichtAdapter reiseUebersichtAdapter;
        private Reisender reisender;
        private Reise reise;

        private SharedPreferences prefs;

    public AusgabenAdapterHelper(Context context, View layoutScreen, Reisender reisender, Reise reise, AusgabenActivity ausgabenActivity, ReiseUebersichtAdapter reiseUebersichtAdapter) {

        this.layoutScreen = layoutScreen;
        this.mContext = context;
        this.ausgabenActivity = ausgabenActivity;
        this.reiseUebersichtAdapter = reiseUebersichtAdapter;
        this.reisender = reisender;
        this.reise = reise;

        prefs = context.getSharedPreferences("reisender", MODE_PRIVATE);

    }

    public void setUpDashboard(){
        RecyclerView details = layoutScreen.findViewById(R.id.ausgabenDetailsRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        details.setLayoutManager(layoutManager);
        List<Ausgabe> ausgaben = new ArrayList<>();
        for(Stop stop : reise.getStops())
            ausgaben.addAll(stop.getAusgaben());
        DashboardAusgabenAdapter dashboardAusgabenAdapter = new DashboardAusgabenAdapter(mContext, ausgaben);
        details.setAdapter(dashboardAusgabenAdapter);
    }

    public void setUpAusgaben(){
        Button ausgabeSpeichernButton = layoutScreen.findViewById(R.id.ausgabeSpeichernButton);
        layoutScreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.INVISIBLE);
        ButtonEffect.buttonPressDownEffect(ausgabeSpeichernButton);

        TextView nutzergView = layoutScreen.findViewById(R.id.textViewPayAmount);

        List<AusgabenSumme> budgetReisende = reise.getGesamtBudgetProReisender();
        AusgabenSumme currentUserAusgabenSumme = null;
        for(AusgabenSumme ausgabenSumme : budgetReisende)
        {
            if(ausgabenSumme.getReisenderId() == reisender.getId())
            {
                currentUserAusgabenSumme = ausgabenSumme;
                break;
            }
        }
        if (currentUserAusgabenSumme == null) currentUserAusgabenSumme = new AusgabenSumme(BigDecimal.ZERO);
        nutzergView.setText(currentUserAusgabenSumme.getBetrag().toString() + "€");

        TextView gesamtView = layoutScreen.findViewById(R.id.textViewPayAmountTeam);
        if(reise.getGesamtAusgabe() == null) gesamtView.setText("0€");
        else gesamtView.setText(reise.getGesamtAusgabe() + "€");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setUpPaymentTextViews(layoutScreen);
        }


        reisendeNames = new ArrayList<>();
        for(ReiseTeilnehmer reiseTeilnehmer : reise.getReiseTeilnehmer()) reisendeNames.add(reiseTeilnehmer.getReisenderName());

        ListView schuldnerListView = layoutScreen.findViewById(R.id.schuldnerListView);
        schuldnerListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<String> schuldnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_multiple_choice, reisendeNames);
        schuldnerListView.setAdapter(schuldnerAdapter);

        Spinner typen = layoutScreen.findViewById(R.id.planets_spinner4);
        List<String> names = new ArrayList<>();
        for (AusgabenTyp c : AusgabenTyp.values()) {
            names.add(c.name());
        }
        ArrayAdapter<String> typAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, names);
        typAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typen.setAdapter(typAdapter);

        Spinner stopNameSpinner = layoutScreen.findViewById(R.id.planets_spinner3);
        List<String> stopNames = new ArrayList<>();
        for(Stop stop : reise.getStops()) stopNames.add(stop.getName());
        ArrayAdapter<String> stopAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, stopNames);
        stopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stopNameSpinner.setAdapter(stopAdapter);

        ausgabeSpeichernButton.setOnClickListener(view -> {


            layoutScreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.VISIBLE);
            EditText betragView = layoutScreen.findViewById(R.id.ausgabenTextView);
            String sbetrag = betragView.getText().toString();
            if(sbetrag.isEmpty()) return;
            BigDecimal betrag = new BigDecimal(sbetrag);
            addBetrag(reise, stopNameSpinner, betrag, schuldnerListView, layoutScreen);
            betragView.setText("");
            layoutScreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.INVISIBLE);
            //reiseUebersichtAdapter.getZwischenstopAdapterHelper().setUpZwischenStops();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpPaymentTextViews(View layoutScreen){
        TextView balanceView = layoutScreen.findViewById(R.id.textViewPayAmountAusgleich);
        TextView balanceTextView = layoutScreen.findViewById(R.id.textView11);

        AusgabenSumme reisenderGesamt = reise.getGesamtAusgabeProReisender().stream()
                .filter(d -> d.getReisenderId() == reisender.getId())
                .findFirst().orElse(new AusgabenSumme());
        if (reisenderGesamt.getBetrag() == null) reisenderGesamt.setBetrag(BigDecimal.ZERO);

        TextView gesamtView = layoutScreen.findViewById(R.id.textViewPayAmount);
        gesamtView.setText(reisenderGesamt.getBetrag().toString() + "€");

        TextView gesamtGruppeView = layoutScreen.findViewById(R.id.textViewPayAmountTeam);


        if (reise.getGesamtAusgabe() == null) gesamtGruppeView.setText("0€");
        else gesamtGruppeView.setText(reise.getGesamtAusgabe().toString() + "€");


/*        if(gesamtBalance.compareTo(new BigDecimal(0)) < 0){
            balanceView.setTextColor(Color.RED);
            balanceTextView.setText("Du schuldest");
        }
        else {
            balanceView.setTextColor(Color.GREEN);
            balanceTextView.setText("Du bekommst");
        }
        balanceView.setText(gesamtBalance.toString() + "€");*/
    }

    public void addBetrag(Reise reise, Spinner stopSpinner, BigDecimal betrag, ListView schuldner, View layoutScreen)
    {
        AusgabenRequest ausgabenRequest = new AusgabenRequest();

        Stop stop = reise.getStops().get(stopSpinner.getSelectedItemPosition());
        Spinner kategorieSpinner = layoutScreen.findViewById(R.id.planets_spinner4);
        SparseBooleanArray checked = schuldner.getCheckedItemPositions();

        Ausgabe ausgabe = new Ausgabe();
        ausgabe.setZahler(reisender.getId());
        ausgabe.setZahlername(reisender.getNickname());
        ausgabe.setAusgabenSumme(new ArrayList<>());

        for (int i = 0; i < schuldner.getCount(); i++)
            if (checked.get(i)) {
                BigDecimal betragN = betrag.divide(new BigDecimal(checked.size()), 2, RoundingMode.HALF_DOWN);
                ausgabe.setAusgabenTyp(AusgabenTyp.typForPosition(kategorieSpinner.getSelectedItemPosition()));
                AusgabenSumme ausgabenSumme = new AusgabenSumme(reise.getReiseTeilnehmer().get(i).getId(), reise.getReiseTeilnehmer().get(i).getReisenderName(), betragN);
                ausgabe.getAusgabenSumme().add(ausgabenSumme);
            }

        ausgabenRequest.setReise(reise);
        ausgabenRequest.setStop(stop);
        ausgabenRequest.setAusgabe(ausgabe);

        EndpointConnector.saveAusgabe(ausgabenRequest, saveAusgabeCallback());
        Log.d("ausgaben", stop.getAusgaben().toString());
    }

    private Callback saveAusgabeCallback(){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("reisender", response.body().string());
                    editor.apply();
                    Looper.prepare();
                    Toast.makeText(mContext, "Ausgabe gespeichert!", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

}
