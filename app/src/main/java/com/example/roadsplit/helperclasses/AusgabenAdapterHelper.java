package com.example.roadsplit.helperclasses;

import android.content.Context;
import android.graphics.Color;
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

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.AusgabenTyp;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseReponse;
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

public class AusgabenAdapterHelper implements Observer {

        private Context mContext;
        private View layoutScreen;
        private AusgabenActivity ausgabenActivity;
        private ReiseReponse reiseReponse;
        private BigDecimal reiseGesamtAusgabe;
        private List<String> reisendeNames;

    public AusgabenAdapterHelper(Context context, View layoutScreen, ReiseReponse reiseReponse, AusgabenActivity ausgabenActivity) {
        MainActivity.currentUserData.addObserver(this);
        this.layoutScreen = layoutScreen;
        this.reiseReponse = MainActivity.currentUserData.getCurrentReiseResponse();
        this.mContext = context;
        this.ausgabenActivity = ausgabenActivity;

        reiseGesamtAusgabe = new BigDecimal(0);
        for(Stop stop : reiseReponse.getReise().getStops()) {
            try {
                reiseGesamtAusgabe = reiseGesamtAusgabe.add(stop.getGesamtausgaben());
            } catch (Exception e) {
                stop.setGesamtausgaben(new BigDecimal(0));
            }
        }
    }

    public void setUpAusgaben(){
        Button ausgabeSpeichernButton = layoutScreen.findViewById(R.id.ausgabeSpeichernButton);
        layoutScreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.INVISIBLE);
        ButtonEffect.buttonPressDownEffect(ausgabeSpeichernButton);

        TextView nutzergView = layoutScreen.findViewById(R.id.textViewPayAmount);
        nutzergView.setText(reiseReponse.getGesamtAusgabe().toString() + "€");

        TextView gesamtView = layoutScreen.findViewById(R.id.textViewPayAmountTeam);
        gesamtView.setText(reiseGesamtAusgabe + "€");

        setUpPaymentTextViews(layoutScreen);


        reisendeNames = new ArrayList<>();
        for(Reisender reisender : reiseReponse.getReisendeList()) reisendeNames.add(reisender.getNickname());

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
        for(Stop stop : reiseReponse.getReise().getStops()) stopNames.add(stop.getName());
        ArrayAdapter<String> stopAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, stopNames);
        stopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stopNameSpinner.setAdapter(stopAdapter);

        ausgabeSpeichernButton.setOnClickListener(view -> {
            layoutScreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.VISIBLE);
            EditText betragView = layoutScreen.findViewById(R.id.ausgabenTextView);
            String sbetrag = betragView.getText().toString();
            if(sbetrag.isEmpty()) return;
            BigDecimal betrag = new BigDecimal(sbetrag);
            addBetrag(reiseReponse.getReise(), stopNameSpinner, betrag, schuldnerListView, layoutScreen);
            betragView.setText("");
            layoutScreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.INVISIBLE);
        });
    }

    private void setUpPaymentTextViews(View layoutScreen){
        TextView balanceView = layoutScreen.findViewById(R.id.textViewPayAmountAusgleich);
        TextView balanceTextView = layoutScreen.findViewById(R.id.textView11);

        TextView gesamtView = layoutScreen.findViewById(R.id.textViewPayAmount);
        gesamtView.setText(reiseReponse.getGesamtAusgabe().toString() + "€");

        TextView gesamtGruppeView = layoutScreen.findViewById(R.id.textViewPayAmountTeam);

        reiseGesamtAusgabe = new BigDecimal(0);
        for(Stop stop : reiseReponse.getReise().getStops()) {
            try {
                reiseGesamtAusgabe = reiseGesamtAusgabe.add(stop.getGesamtausgaben());
            } catch (Exception e) {
                stop.setGesamtausgaben(new BigDecimal(0));
            }
        }
        gesamtGruppeView.setText(reiseGesamtAusgabe.toString() + "€");

        BigDecimal gesamtBalance = new BigDecimal(0);
        int counter = 0;
        for(Reisender reisender : reiseReponse.getSchuldner()){
            if(!reisender.getId().equals(MainActivity.currentUserData.getCurrentUser().getId())){
                BigDecimal betrag = reiseReponse.getBetraege().get(counter);
                gesamtBalance = gesamtBalance.add(betrag);
            }
            counter++;
        }
        if(gesamtBalance.compareTo(new BigDecimal(0)) < 0){
            balanceView.setTextColor(Color.RED);
            balanceTextView.setText("Du schuldest");
        }
        else {
            balanceView.setTextColor(Color.GREEN);
            balanceTextView.setText("Du bekommst");
        }
        balanceView.setText(gesamtBalance.toString() + "€");
    }

    public void addBetrag(Reise reise, Spinner stopSpinner, BigDecimal betrag, ListView schuldner, View layoutScreen)
    {
        Stop stop = reise.getStops().get(stopSpinner.getSelectedItemPosition());
        Spinner kategorieSpinner = layoutScreen.findViewById(R.id.planets_spinner4);
        SparseBooleanArray checked = schuldner.getCheckedItemPositions();
        for (int i = 0; i < schuldner.getCount(); i++)
            if (checked.get(i)) {
                BigDecimal betragN = betrag.divide(new BigDecimal(checked.size()), 2, RoundingMode.HALF_DOWN);
                Ausgabe ausgabe = new Ausgabe();
                ausgabe.setBetrag(betragN);
                ausgabe.setAnzahlReisende(checked.size());
                ausgabe.setZahler(MainActivity.currentUserData.getCurrentUser().getId());
                ausgabe.setSchuldner(reiseReponse.getReisendeList().get(i).getId());
                ausgabe.setAusgabenTyp(AusgabenTyp.typForPosition(kategorieSpinner.getSelectedItemPosition()));
                if(stop.getAusgaben() == null) stop.setAusgaben(new ArrayList<>());
                stop.getAusgaben().add(ausgabe);
                if(stop.getGesamtausgaben() == null) stop.setGesamtausgaben(new BigDecimal(0));
                stop.setGesamtausgaben(stop.getGesamtausgaben().add(betragN));
            }
        reise.getStops().set(stopSpinner.getSelectedItemPosition(), stop);
        EndpointConnector.updateReise(reise, updateReiseCallback());
        Log.d("ausgaben", stop.getAusgaben().toString());
    }

    private Callback updateReiseCallback(){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseReponse reiseReponse = new Gson().fromJson(response.body().string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                    Log.d("ausgaben", reiseReponse.getReise().toString());
                    EndpointConnector.fetchPaymentInfo(reiseReponse.getReise(), fetchPaymentCallback());
                }
            }
        };
    }

    private Callback fetchPaymentCallback(){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseReponse payResponse = new Gson().fromJson(Objects.requireNonNull(response.body()).string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                    MainActivity.currentUserData.setCurrentUser(payResponse.getReisender());
                    MainActivity.currentUserData.setCurrentReiseResponse(payResponse);
                    MainActivity.currentUserData.setCurrentReise(payResponse.getReise());
                    MainActivity.currentUserData.notifyObservers();
                    reiseReponse = payResponse;
                    reisendeNames = new ArrayList<>();
                    for(Reisender reisender : reiseReponse.getReisendeList()) reisendeNames.add(reisender.getNickname());
                    reiseGesamtAusgabe = new BigDecimal(0);
                    for(Stop stop : reiseReponse.getReise().getStops()) {
                        try {
                            reiseGesamtAusgabe = reiseGesamtAusgabe.add(stop.getGesamtausgaben());
                        } catch (Exception e) {
                            stop.setGesamtausgaben(new BigDecimal(0));
                        }
                    }
                    ausgabenActivity.runOnUiThread(() -> setUpPaymentTextViews(layoutScreen));
                    Looper.prepare();
                    Toast.makeText(mContext, "success", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
