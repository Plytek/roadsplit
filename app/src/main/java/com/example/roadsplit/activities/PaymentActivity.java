package com.example.roadsplit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.helperclasses.AusgabenAdapterHelper;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.AusgabenTyp;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.reponses.ReiseResponse;
import com.example.roadsplit.requests.AusgabenRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PaymentActivity extends AppCompatActivity {

    private TextView notizView;
    private TextView betragView;
    private Spinner kategorieSpinner;
    private ListView schulderView;
    private Spinner zwischenstopSpinner;
    private Button ausgabeSpeichernButton;
    private CheckBox privateCheckBox;
    private ProgressBar progressBar;

    ArrayAdapter<String> schuldnerAdapter;
    private SharedPreferences reportPref;
    private SharedPreferences reisenderPref;
    private SharedPreferences reisePref;
    private AusgabenReport ausgabenReport;
    private Reise reise;
    private Reisender reisender;

    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ausgabeerstellen);

        this.notizView = findViewById(R.id.ausgabenNotizEditText);
        this.betragView = findViewById(R.id.betragEditText);
        this.kategorieSpinner = findViewById(R.id.kategorieSpinner);
        this.schulderView = findViewById(R.id.schuldnerListView);
        this.zwischenstopSpinner = findViewById(R.id.planets_spinnerZW);
        this.ausgabeSpeichernButton = findViewById(R.id.ausgabeSpeichernButton);
        this.privateCheckBox = findViewById(R.id.privatCheckBox);
        this.progressBar = findViewById(R.id.ausgabeProgressBar);
        progressBar.setVisibility(View.GONE);

        this.reportPref = getSharedPreferences("report", MODE_PRIVATE);
        this.reisenderPref = getSharedPreferences("reisender", MODE_PRIVATE);
        this.reisePref = getSharedPreferences("reise", MODE_PRIVATE);
        this.ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);
        this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
        this.reise = ausgabenReport.getReise();

        setupListView();
        setupSpinners();

        ausgabeSpeichernButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BigDecimal betrag = new BigDecimal(betragView.getText().toString());
                    progressBar.setVisibility(View.VISIBLE);
                    addBetrag(betrag, schulderView);
                } catch (NullPointerException e) {
                    Log.d("ausgabeError", "Vermutlich kein betrag angegeben wah");
                }
            }
        });

        privateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    schulderView.setEnabled(false);
                    schulderView.setBackgroundColor(Color.LTGRAY);
                    schulderView.clearChoices();
                    check = true;
                    schuldnerAdapter.notifyDataSetChanged();
                }
                else {

                    schulderView.setEnabled(true);
                    check = false;
                    schulderView.setBackgroundColor(Color.WHITE);
                }
            }
        });


    }

    private void setupListView()
    {
        List<String> reisendeNames = new ArrayList<>();
        for(Reisender reisender : ausgabenReport.getReisende()) reisendeNames.add(reisender.getNickname());

        schulderView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        schuldnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, reisendeNames);
        schulderView.setAdapter(schuldnerAdapter);
    }

    private void setupSpinners(){
        List<String> names = new ArrayList<>();
        for (AusgabenTyp c : AusgabenTyp.values()) {
            names.add(c.name());
        }
        ArrayAdapter<String> typAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names);
        typAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kategorieSpinner.setAdapter(typAdapter);

        List<String> stopNames = new ArrayList<>();
        for(Stop stop : ausgabenReport.getReise().getStops()) stopNames.add(stop.getName());
        ArrayAdapter<String> stopAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stopNames);
        stopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zwischenstopSpinner.setAdapter(stopAdapter);
    }


    public void addBetrag(BigDecimal betrag, ListView schuldner)
    {
        Stop stop = reise.getStops().get(zwischenstopSpinner.getSelectedItemPosition());
        SparseBooleanArray checked = schuldner.getCheckedItemPositions();
        if(privateCheckBox.isChecked()){
            Ausgabe ausgabe = new Ausgabe();
            ausgabe.setBetrag(betrag);
            ausgabe.setAnzahlReisende(1);
            ausgabe.setPaidForHimself(privateCheckBox.isChecked());
            ausgabe.setZahler(reisender.getId());
            ausgabe.setNotiz(notizView.getText().toString());
            ausgabe.setZahlerName(reisender.getNickname());
            ausgabe.setSchuldner(reisender.getId());
            ausgabe.setSchuldnerName(reisender.getNickname());
            ausgabe.setAusgabenTyp(AusgabenTyp.typForPosition(kategorieSpinner.getSelectedItemPosition()));
            ausgabe.setErstellDatum(System.currentTimeMillis());
            if(stop.getAusgaben() == null) stop.setAusgaben(new ArrayList<>());
            stop.getAusgaben().add(ausgabe);
        }
        else {
            for (int i = 0; i < schuldner.getCount(); i++)
                if (checked.get(i)) {
                    BigDecimal betragN = betrag.divide(new BigDecimal(checked.size()), 2, RoundingMode.HALF_DOWN);
                    Ausgabe ausgabe = new Ausgabe();
                    ausgabe.setBetrag(betragN);
                    ausgabe.setAnzahlReisende(checked.size());
                    ausgabe.setPaidForHimself(privateCheckBox.isChecked());
                    ausgabe.setZahler(reisender.getId());
                    ausgabe.setNotiz(notizView.getText().toString());
                    ausgabe.setZahlerName(reisender.getNickname());
                    if(privateCheckBox.isChecked())
                    {
                        ausgabe.setSchuldner(reisender.getId());
                        ausgabe.setSchuldnerName(reisender.getNickname());
                    }
                    else
                    {
                        ausgabe.setSchuldner(ausgabenReport.getReisende().get(i).getId());
                        ausgabe.setSchuldnerName(ausgabenReport.getReisende().get(i).getNickname());
                    }
                    ausgabe.setAusgabenTyp(AusgabenTyp.typForPosition(kategorieSpinner.getSelectedItemPosition()));
                    ausgabe.setErstellDatum(System.currentTimeMillis());
                    if(stop.getAusgaben() == null) stop.setAusgaben(new ArrayList<>());
                    stop.getAusgaben().add(ausgabe);
                    if(stop.getGesamtausgaben() == null) stop.setGesamtausgaben(new BigDecimal(0));
                    stop.setGesamtausgaben(stop.getGesamtausgaben().add(betragN));
                }
        }

        reise.getStops().set(zwischenstopSpinner.getSelectedItemPosition(), stop);

        AusgabenRequest ausgabenRequest = new AusgabenRequest(reisender, reise);
        EndpointConnector.updateAusgaben(ausgabenRequest, updateAusgabenCallback());
    }

    private Callback updateAusgabenCallback(){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                ausgabenReport = gson.fromJson(response.body().string(), AusgabenReport.class);
                if(response.isSuccessful()) {
                    SharedPreferences.Editor editor = reportPref.edit();
                    editor.putString("report", gson.toJson(ausgabenReport));
                    editor.apply();

                    editor = reisePref.edit();
                    editor.putString("reise", gson.toJson(ausgabenReport.getReise()));
                    editor.apply();


                    runOnUiThread(() ->
                    {
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(PaymentActivity.this, AusgabenActivity.class);
                        intent.putExtra("reise", new Gson().toJson(ausgabenReport.getReise()));
                        startActivity(intent);
                        finish();
                    });
                }
            }
        };
    }
}
