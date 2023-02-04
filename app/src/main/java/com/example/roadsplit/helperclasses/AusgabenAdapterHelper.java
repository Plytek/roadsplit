package com.example.roadsplit.helperclasses;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Looper;
import android.preference.PreferenceManager;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.activities.PaymentActivity;
import com.example.roadsplit.adapter.DashboardAusgabenAdapter;
import com.example.roadsplit.adapter.ReiseUebersichtAdapter;
import com.example.roadsplit.adapter.UebersichtRecAdapter;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.AusgabenTyp;
import com.example.roadsplit.model.CurrentUserData;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.reponses.ReiseResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
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
        private ReiseResponse reiseResponse;
        private BigDecimal reiseGesamtAusgabe;
        private List<String> reisendeNames;
        private ReiseUebersichtAdapter reiseUebersichtAdapter;

        private SharedPreferences reisenderPref;
        private SharedPreferences reiseResponsePref;
        private SharedPreferences reisePref;
    private SharedPreferences reportPref;

        private Reisender reisender;
        private AusgabenReport ausgabenReport;

        private int standardColor;
        private final int pressedColor = 0xe0f47521;

    public AusgabenAdapterHelper(Context context, View layoutScreen, ReiseResponse reiseResponse, AusgabenActivity ausgabenActivity, ReiseUebersichtAdapter reiseUebersichtAdapter, AusgabenReport ausgabenReport) {

        this.reisenderPref = context.getSharedPreferences("reisender", MODE_PRIVATE);
        this.reiseResponsePref = context.getSharedPreferences("reiseResponses", MODE_PRIVATE);
        this.reisePref = context.getSharedPreferences("reise", MODE_PRIVATE);
        this.reportPref = context.getSharedPreferences("report", MODE_PRIVATE);

        this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
        this.reiseResponse = reiseResponse;
        this.ausgabenReport = ausgabenReport;

        this.layoutScreen = layoutScreen;
        this.mContext = context;
        this.ausgabenActivity = ausgabenActivity;
        this.reiseUebersichtAdapter = reiseUebersichtAdapter;

        this.standardColor = mContext.getResources().getColor(R.color.rdarkgreen);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("reisender")) {
                    AusgabenAdapterHelper.this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
                } else if(key.equals("reiseResponse")) {
                    AusgabenAdapterHelper.this.reiseResponse = new Gson().fromJson(reiseResponsePref.getString("reiseResponses", "fehler"), ReiseResponse.class);
                }
                if(key.equals("report"))
                {
                    AusgabenAdapterHelper.this.ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);
                    setUpDashboard();
                }
                // code to handle change in value for the key
            }
        });

        reiseGesamtAusgabe = new BigDecimal(0);
        for(Stop stop : reiseResponse.getReise().getStops()) {
            try {
                reiseGesamtAusgabe = reiseGesamtAusgabe.add(stop.getGesamtausgaben());
            } catch (Exception e) {
                stop.setGesamtausgaben(new BigDecimal(0));
            }
        }
    }


    public AusgabenAdapterHelper(Context context, View layoutScreen, ReiseResponse reiseResponse, AusgabenActivity ausgabenActivity, ReiseUebersichtAdapter reiseUebersichtAdapter) {

        this.reisenderPref = context.getSharedPreferences("reisender", MODE_PRIVATE);
        this.reiseResponsePref = context.getSharedPreferences("reiseResponses", MODE_PRIVATE);
        this.reisePref = context.getSharedPreferences("reise", MODE_PRIVATE);

        this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
        this.reiseResponse = reiseResponse;

        this.layoutScreen = layoutScreen;
        this.mContext = context;
        this.ausgabenActivity = ausgabenActivity;
        this.reiseUebersichtAdapter = reiseUebersichtAdapter;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("reisender")) {
                    AusgabenAdapterHelper.this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
                } else if(key.equals("reiseResponse")) {
                    AusgabenAdapterHelper.this.reiseResponse = new Gson().fromJson(reiseResponsePref.getString("reiseResponses", "fehler"), ReiseResponse.class);
                }
                // code to handle change in value for the key
            }
        });

        reiseGesamtAusgabe = new BigDecimal(0);
        for(Stop stop : reiseResponse.getReise().getStops()) {
            try {
                reiseGesamtAusgabe = reiseGesamtAusgabe.add(stop.getGesamtausgaben());
            } catch (Exception e) {
                stop.setGesamtausgaben(new BigDecimal(0));
            }
        }
    }

    public void setUpDashboard(){
        RecyclerView details = layoutScreen.findViewById(R.id.ausgabenDetailsRecycler);
        Button privatausgaben = layoutScreen.findViewById(R.id.buttonPrivatAusgaben);
        Button gruppenausgaben = layoutScreen.findViewById(R.id.buttonGruppeAusgaben);
        Button schuldendetails = layoutScreen.findViewById(R.id.buttonAbrechnungsAusgabe);
        FloatingActionButton ausgabeButton = layoutScreen.findViewById(R.id.floating_action_button);

        ButtonEffect.buttonPressDownEffect(ausgabeButton);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        details.setLayoutManager(layoutManager);
        DashboardAusgabenAdapter dashboardAusgabenAdapter = new DashboardAusgabenAdapter(mContext, ausgabenReport, layoutScreen, "privatAusgabe");
        details.setAdapter(dashboardAusgabenAdapter);

        setUpColors(standardColor, pressedColor, standardColor);
        privatausgaben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpColors(standardColor, pressedColor, standardColor);
                details.setLayoutManager(layoutManager);
                DashboardAusgabenAdapter dashboardAusgabenAdapter = new DashboardAusgabenAdapter(mContext, ausgabenReport, layoutScreen, "privatAusgabe");
                details.setAdapter(dashboardAusgabenAdapter);
            }
        });

        gruppenausgaben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpColors(pressedColor, standardColor, standardColor);
                details.setLayoutManager(layoutManager);
                DashboardAusgabenAdapter dashboardAusgabenAdapter = new DashboardAusgabenAdapter(mContext, ausgabenReport, layoutScreen, "gruppenAusgabe");
                details.setAdapter(dashboardAusgabenAdapter);
            }
        });

        schuldendetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpColors(standardColor, standardColor, pressedColor);
                details.setLayoutManager(layoutManager);
                DashboardAusgabenAdapter dashboardAusgabenAdapter = new DashboardAusgabenAdapter(mContext, ausgabenReport, layoutScreen, "schulden");
                details.setAdapter(dashboardAusgabenAdapter);
            }
        });

        ausgabeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PaymentActivity.class);
                Activity activity = (Activity) mContext;
                mContext.startActivity(intent);
                activity.finish();
            }
        });
    }

    private void setUpColors(int gruppeColor, int privatColor, int schuldenColor){
        Button privatausgaben = layoutScreen.findViewById(R.id.buttonPrivatAusgaben);
        Button gruppenausgaben = layoutScreen.findViewById(R.id.buttonGruppeAusgaben);
        Button schuldendetails = layoutScreen.findViewById(R.id.buttonAbrechnungsAusgabe);

        gruppenausgaben.getBackground().setColorFilter(gruppeColor, PorterDuff.Mode.SRC_ATOP);
        privatausgaben.getBackground().setColorFilter(privatColor, PorterDuff.Mode.SRC_ATOP);
        schuldendetails.getBackground().setColorFilter(schuldenColor, PorterDuff.Mode.SRC_ATOP);
    }

    public void setUpAusgaben(){
        Button ausgabeSpeichernButton = layoutScreen.findViewById(R.id.ausgabeSpeichernButton);
        layoutScreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.INVISIBLE);
        ButtonEffect.buttonPressDownEffect(ausgabeSpeichernButton);

        TextView nutzergView = layoutScreen.findViewById(R.id.textViewPayAmount);
        try {
            nutzergView.setText(reiseResponse.getGesamtAusgabe().toString() + "€");
        } catch (Exception e) {
            nutzergView.setText("0€");
        }

        TextView gesamtView = layoutScreen.findViewById(R.id.textViewPayAmountTeam);
        try {
            gesamtView.setText(reiseGesamtAusgabe + "€");
        } catch (Exception e) {
            gesamtView.setText("0€");
        }

        setUpPaymentTextViews(layoutScreen);


        reisendeNames = new ArrayList<>();
        for(Reisender reisender : reiseResponse.getReisendeList()) reisendeNames.add(reisender.getNickname());

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
        for(Stop stop : reiseResponse.getReise().getStops()) stopNames.add(stop.getName());
        ArrayAdapter<String> stopAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, stopNames);
        stopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stopNameSpinner.setAdapter(stopAdapter);

        ausgabeSpeichernButton.setOnClickListener(view -> {
            layoutScreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.VISIBLE);
            EditText betragView = layoutScreen.findViewById(R.id.ausgabenTextView);
            String sbetrag = betragView.getText().toString();
            if(sbetrag.isEmpty()) return;
            BigDecimal betrag = new BigDecimal(sbetrag);
            addBetrag(reiseResponse.getReise(), stopNameSpinner, betrag, schuldnerListView, layoutScreen);
            betragView.setText("");
            layoutScreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.INVISIBLE);
            //reiseUebersichtAdapter.getZwischenstopAdapterHelper().setUpZwischenStops();
        });
    }

    private void setUpPaymentTextViews(View layoutScreen){
        TextView balanceView = layoutScreen.findViewById(R.id.textViewPayAmountAusgleich);
        TextView balanceTextView = layoutScreen.findViewById(R.id.textView11);

        TextView gesamtView = layoutScreen.findViewById(R.id.textViewPayAmount);
        try {
            gesamtView.setText(reiseResponse.getGesamtAusgabe().toString() + "€");
        } catch (Exception e) {
            gesamtView.setText("0€");
        }

        TextView gesamtGruppeView = layoutScreen.findViewById(R.id.textViewPayAmountTeam);

        reiseGesamtAusgabe = new BigDecimal(0);
        for(Stop stop : reiseResponse.getReise().getStops()) {
            try {
                reiseGesamtAusgabe = reiseGesamtAusgabe.add(stop.getGesamtausgaben());
            } catch (Exception e) {
                if (stop.getGesamtausgaben() == null)
                    stop.setGesamtausgaben(new BigDecimal(0));
            }
        }
        gesamtGruppeView.setText(reiseGesamtAusgabe.toString() + "€");

        BigDecimal gesamtBalance = new BigDecimal(0);
        int counter = 0;
        for(Reisender reisender : reiseResponse.getSchuldner()){
            if(!reisender.getId().equals(reisender.getId())){
                BigDecimal betrag = reiseResponse.getBetraege().get(counter);
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
                ausgabe.setZahler(reisender.getId());
                ausgabe.setZahlerName(reisender.getNickname());
                ausgabe.setSchuldner(reiseResponse.getReisendeList().get(i).getId());
                ausgabe.setSchuldnerName(reiseResponse.getReisendeList().get(i).getNickname());
                ausgabe.setAusgabenTyp(AusgabenTyp.typForPosition(kategorieSpinner.getSelectedItemPosition()));
                ausgabe.setErstellDatum(System.currentTimeMillis());
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
                ReiseResponse reiseResponse = new Gson().fromJson(response.body().string(), ReiseResponse.class);
                if(response.isSuccessful()) {
                    Log.d("ausgaben", reiseResponse.getReise().toString());
                    EndpointConnector.fetchPaymentInfo(reiseResponse.getReise(), reisender, fetchPaymentCallback());
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
                Gson gson = new Gson();
                ReiseResponse payResponse = gson.fromJson(Objects.requireNonNull(response.body()).string(), ReiseResponse.class);
                if(response.isSuccessful()) {
                    SharedPreferences.Editor editor = reisenderPref.edit();
                    editor.putString("reisender", gson.toJson(reisender));
                    editor.apply();

                    editor = reiseResponsePref.edit();
                    editor.putString("reiseResponse", gson.toJson(reiseResponse));
                    editor.apply();

                    editor = reisePref.edit();
                    editor.putString("reise", gson.toJson(reiseResponse.getReise()));
                    editor.apply();

                    reiseResponse = payResponse;
                    reisendeNames = new ArrayList<>();
                    for(Reisender reisender : reiseResponse.getReisendeList()) reisendeNames.add(reisender.getNickname());
                    reiseGesamtAusgabe = new BigDecimal(0);
                    for(Stop stop : reiseResponse.getReise().getStops()) {
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

}
