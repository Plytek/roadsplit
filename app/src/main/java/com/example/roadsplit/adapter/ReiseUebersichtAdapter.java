package com.example.roadsplit.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.activities.MainScreenReisenActivity;
import com.example.roadsplit.activities.testing.PaymentDummyActivity;
import com.example.roadsplit.helperclasses.AppSettings;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.AusgabenTyp;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseReponse;
import com.example.roadsplit.requests.AusgabenRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ReiseUebersichtAdapter extends PagerAdapter {

    private Reise reise;
    private List<Reisender> reisende;
    private List<Stop> stops;
    private ArrayList<HashMap<String,String>> fullstops = new ArrayList<HashMap<String,String>>();
    private BigDecimal nutzerGesamtAusgabe;
    private BigDecimal reiseGesamtAusgabe;
    private ReiseReponse reiseReponse;
    private List<String> reisendeNames;
    private List<String> ausgabeDetails;
    Context mContext;
    MainScreenReisenActivity mainScreenReisenActivity;
    List<View> views;

    public ReiseUebersichtAdapter(Context mContext, MainScreenReisenActivity mainScreenReisenActivity, List<View> views, ReiseReponse reiseReponse) {
        this.mContext = mContext;
        this.views = views;
        this.mainScreenReisenActivity = mainScreenReisenActivity;
        this.reise = reiseReponse.getReise();
        this.stops = reiseReponse.getReise().getStops();
        this.reisende = reiseReponse.getReisendeList();
        this.nutzerGesamtAusgabe = reiseReponse.getGesamtAusgabe();
        this.reiseReponse = reiseReponse;
        this.ausgabeDetails = new ArrayList<>();
        ausgabeDetails.addAll(reiseReponse.getAusgabenRecord());
        if(nutzerGesamtAusgabe == null) nutzerGesamtAusgabe = new BigDecimal(0);
        reiseGesamtAusgabe = new BigDecimal(0);
        for(Stop stop : stops) {
            try {
                reiseGesamtAusgabe = reiseGesamtAusgabe.add(stop.getGesamtausgaben());
            } catch (Exception e) {
                stop.setGesamtausgaben(new BigDecimal(0));
            }
        }
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View layoutScreen;
        switch(position)
        {
            case 0:
                layoutScreen = inflater.inflate(R.layout.ausgabenpage,null);
                fetchPaymentInfo(reise, layoutScreen);
                setUpAusgaben(layoutScreen);
                break;
            case 1:
                layoutScreen = inflater.inflate(R.layout.zwischenstopp,null);
                setUpZwischenStops(layoutScreen);
                break;
            case 2:
                layoutScreen = inflater.inflate(R.layout.packlistepage,null);
                break;
            case 3:
                layoutScreen = inflater.inflate(R.layout.ausgabendetailpage,null);
                setUpAusgabeDetailPage(layoutScreen);
                break;
            default:
                layoutScreen = inflater.inflate(R.layout.dokumentepage,null);
                break;
        }

        container.addView(layoutScreen);

        return layoutScreen;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View)object);

    }

    public void setUpZwischenStops(View layoutScreen){
        Button addStopButton = layoutScreen.findViewById(R.id.plusButton2);
        Button removeStopButton = layoutScreen.findViewById(R.id.minusButton2);
        Button addBudgetButton = layoutScreen.findViewById(R.id.plusButton3);

        ListView stopListView = layoutScreen.findViewById(R.id.stopNamenListView);

        for(Stop stop : stops)
        {
            HashMap<String, String> fullstop = new HashMap<>();
            //stopNames.add(stop.getName());
            //TODO: budget immer setzen
            fullstop.put("stop", stop.getName());
/*            if(stop.getBudget() == null) budgetStrings.add("0");
            else budgetStrings.add(stop.getBudget().toString());*/
            if(stop.getBudget() == null) fullstop.put("budget","0");
            else fullstop.put("budget", stop.getBudget().toString());
            /*if(stop.getGesamtausgaben() == null)gesamtAusgabenString.add("0");
            else gesamtAusgabenString.add(stop.getGesamtausgaben().toString());*/
            if(stop.getGesamtausgaben() == null)fullstop.put("gesamt", "0");
            else fullstop.put("gesamt", stop.getGesamtausgaben().toString());
            fullstops.add(fullstop);
        }

        StopAdapter stopAdapter = new StopAdapter(stops, mContext);
        stopListView.setAdapter(stopAdapter);

        addStopButton.setOnClickListener(view -> {
            String stopName = ((EditText)layoutScreen.findViewById(R.id.pinTextView2)).getText().toString();
            String budget = ((EditText)layoutScreen.findViewById(R.id.pinTextView3)).getText().toString();
            if(stopName.isEmpty()) return;

            Stop stop = new Stop();
            stop.setName(stopName);
            if(budget.isEmpty()) stop.setBudget(new BigDecimal(0));
            else stop.setBudget(new BigDecimal(budget));
            stop.setGesamtausgaben(new BigDecimal(0));

            stops.add(stop);
            reise.setStops(stops);
            stopAdapter.notifyDataSetChanged();

        });

        removeStopButton.setOnClickListener(view -> {
            SparseBooleanArray checked = stopListView.getCheckedItemPositions();
            int removedcounter = 0;
            for (int i = 0; i < stopListView.getCount(); i++)
                if (checked.get(i)) {
                    stops.remove(i-removedcounter);
                    removedcounter++;
                }
            reise.setStops(stops);
            stopAdapter.notifyDataSetChanged();
        });

        addBudgetButton.setOnClickListener(view -> {
            String budget = ((EditText)layoutScreen.findViewById(R.id.pinTextView3)).getText().toString();
            //Nur "*d.dd und *d,dd*"
            String regex = "^\\d+([.,]\\d{2})?$";
            if(budget.isEmpty()) return;
            if(!budget.matches(regex))
            {
                //TODO: Fehlerbehandlung UI
                return;
            }
            budget = budget.replaceAll(",", ".");
            SparseBooleanArray checked = stopListView.getCheckedItemPositions();
            for (int i = 0; i < stopListView.getCount(); i++)
                if (checked.get(i)) {
                    stops.get(i).setBudget(new BigDecimal(budget));
                    //budgetStrings.set(i, budget);
                }
            reise.setStops(stops);
            stopAdapter.notifyDataSetChanged();
        });
    }

    public void setUpAusgaben(View layoutscreen){
        Button ausgabeSpeichernButton = layoutscreen.findViewById(R.id.ausgabeSpeichernButton);
        layoutscreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.INVISIBLE);
        AppSettings.buttonPressDownEffect(ausgabeSpeichernButton);

        TextView nutzergView = layoutscreen.findViewById(R.id.textViewPayAmount);
        nutzergView.setText(nutzerGesamtAusgabe.toString() + "€");

        TextView gesamtView = layoutscreen.findViewById(R.id.textViewPayAmountTeam);
        gesamtView.setText(reiseGesamtAusgabe.toString() + "€");

        setUpPaymentTextViews(layoutscreen);


        reisendeNames = new ArrayList<>();
        for(Reisender reisender : reisende) reisendeNames.add(reisender.getNickname());

        ListView schuldnerListView = layoutscreen.findViewById(R.id.schuldnerListView);
        schuldnerListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<String> schuldnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_multiple_choice, reisendeNames);
        schuldnerListView.setAdapter(schuldnerAdapter);

        Spinner typen = layoutscreen.findViewById(R.id.planets_spinner4);
        List<String> names = new ArrayList<>();
        for (AusgabenTyp c : AusgabenTyp.values()) {
            names.add(c.name());
        }
        ArrayAdapter<String> typAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, names);
        typAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typen.setAdapter(typAdapter);

        Spinner stopNameSpinner = layoutscreen.findViewById(R.id.planets_spinner3);
        List<String> stopNames = new ArrayList<>();
        for(Stop stop : stops) stopNames.add(stop.getName());
        ArrayAdapter<String> stopAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, stopNames);
        stopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stopNameSpinner.setAdapter(stopAdapter);

        ausgabeSpeichernButton.setOnClickListener(view -> {
            layoutscreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.VISIBLE);
            EditText betragView = layoutscreen.findViewById(R.id.ausgabenTextView);
            String sbetrag = betragView.getText().toString();
            if(sbetrag.isEmpty()) return;
            BigDecimal betrag = new BigDecimal(sbetrag);
            addBetrag(reise, stopNameSpinner, betrag, schuldnerListView, layoutscreen);
            betragView.setText("");
            layoutscreen.findViewById(R.id.ausgabenProgressBar).setVisibility(View.INVISIBLE);
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
            if(!reisender.getId().equals(MainActivity.currentUser.getId())){
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
                ausgabe.setZahler(MainActivity.currentUser.getId());
                ausgabe.setSchuldner(reisende.get(i).getId());
                ausgabe.setAusgabenTyp(AusgabenTyp.typForPosition(kategorieSpinner.getSelectedItemPosition()));
                if(stop.getAusgaben() == null) stop.setAusgaben(new ArrayList<>());
                stop.getAusgaben().add(ausgabe);
                if(stop.getGesamtausgaben() == null) stop.setGesamtausgaben(new BigDecimal(0));
                stop.setGesamtausgaben(stop.getGesamtausgaben().add(betragN));
            }
        reise.getStops().set(stopSpinner.getSelectedItemPosition(), stop);
        updateReise(reise, layoutScreen);
        Log.d("ausgaben", stop.getAusgaben().toString());
    }

    public void updateReise(Reise reise, View layoutScreen)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/updatereise";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        if(reise == null) return;
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(reise));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        //Erstellt in einem neuen Thread eine Http Anfrage an den Webservice, ruft bei Erfolg onReponse() auf, bei Misserfolg onFailure()
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseReponse reiseReponse = new Gson().fromJson(response.body().string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                    Log.d("ausgaben", reiseReponse.getReise().toString());
                    fetchPaymentInfo(reiseReponse.getReise(), layoutScreen);
                }
            }

        });
    }

    public void fetchPaymentInfo(Reise reiseR, View layoutScreen)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/ausgaben";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        AusgabenRequest ausgabenRequest = new AusgabenRequest();
        if(reiseR == null) return;

        ausgabenRequest.setReise(reiseR);
        ausgabenRequest.setReisender(MainActivity.currentUser);

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(ausgabenRequest));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        //Log.d("zusammenfassung", new Gson().toJson(ausgabenRequest));

        //Erstellt in einem neuen Thread eine Http Anfrage an den Webservice, ruft bei Erfolg onReponse() auf, bei Misserfolg onFailure()
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseReponse payResponse = new Gson().fromJson(Objects.requireNonNull(response.body()).string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                    MainActivity.currentUser = payResponse.getReisender();
                    reise = payResponse.getReise();
                    stops = payResponse.getReise().getStops();
                    reisende = payResponse.getReisendeList();
                    nutzerGesamtAusgabe = payResponse.getGesamtAusgabe();
                    reiseReponse = payResponse;
                    reisendeNames = new ArrayList<>();
                    for(Reisender reisender : reisende) reisendeNames.add(reisender.getNickname());
                    if(nutzerGesamtAusgabe == null) nutzerGesamtAusgabe = new BigDecimal(0);
                    reiseGesamtAusgabe = new BigDecimal(0);
                    for(Stop stop : stops) {
                        try {
                            reiseGesamtAusgabe = reiseGesamtAusgabe.add(stop.getGesamtausgaben());
                        } catch (Exception e) {
                            stop.setGesamtausgaben(new BigDecimal(0));
                        }
                    }
                    mainScreenReisenActivity.runOnUiThread(() -> setUpPaymentTextViews(layoutScreen));
                    Looper.prepare();
                    Toast.makeText(mContext, "success", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    public void setUpAusgabeDetailPage(View layoutScreen)
    {
        Spinner filterSpinner = layoutScreen.findViewById(R.id.ausgabeFilterSpinner);
        ListView detailAusgaben = layoutScreen.findViewById(R.id.ausgabeDetailListView);

        reisendeNames = new ArrayList<>();
        for(Reisender reisender : reisende) reisendeNames.add(reisender.getNickname());

        ArrayAdapter<String> reisendeAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, reisendeNames);
        reisendeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(reisendeAdapter);


        ArrayAdapter<String> detailAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, ausgabeDetails);
            detailAusgaben.setAdapter(detailAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ausgabeDetails.clear();
                for(String detail : reiseReponse.getAusgabenRecord())
                {
                    if(detail.contains(reisende.get(i).getNickname())) ausgabeDetails.add(detail);
                }
                detailAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void setUpPackliste(View layoutScreen){

    }
}
