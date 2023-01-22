package com.example.roadsplit.activities.testing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseReponse;
import com.example.roadsplit.requests.AusgabenRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentDummyActivity extends AppCompatActivity {

    private ArrayAdapter<String> reisendeAdapter;
    private ArrayAdapter<String> stopAdapter;
    private ArrayAdapter<String> itemAdapter;
    private List<Reisender> reisenderList;
    private List<Reise> reiseList;
    private List<String> names;
    private List<String> stops;
    private List<String> items;
    private Reise currentReise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_dummy);

        currentReise = MainActivity.currentUserData.getCurrentUser().getReisen().get(0);

        names = new ArrayList<>();
        stops = new ArrayList<>();
        items = new ArrayList<>();
        reiseList = new ArrayList<>();
        reisenderList = new ArrayList<>();

        ListView reisendeView = findViewById(R.id.reisendeList);
        reisendeView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        reisendeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, names);
        ListView stopView = findViewById(R.id.stopsList);
        stopView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        stopAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, stops);
        ListView itemView = findViewById(R.id.itemsList);
        itemAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        reisendeView.setAdapter(reisendeAdapter);
        stopView.setAdapter(stopAdapter);
        itemView.setAdapter(itemAdapter);

    }


    public void fetchReise(View view){
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/reisesuche";
        //String url = "http://10.0.2.2:8080/api/userdaten/user";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        currentReise = MainActivity.currentUserData.getCurrentUser().getReisen().get(0);
        if(currentReise == null) return;
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(currentReise));

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
                    Log.d("reiseresponse",reiseReponse.getReisendeList().toString());
                    for(Reisender reisender : reiseReponse.getReisendeList())
                    {
                        reisenderList.add(reisender);
                        names.add(reisender.getNickname());
                    }
                    for(Stop stop : reiseReponse.getReise().getStops())
                    {
                        stops.add(stop.getName());
                    }
                    reiseList.add(reiseReponse.getReise());
                    runOnUiThread(() -> {
                        reisendeAdapter.notifyDataSetChanged();
                        stopAdapter.notifyDataSetChanged();
                    });
                }
                else {
                    //EditText editText = findViewById(R.id.dummyResultText);
                    //editText.setText(reiseReponse.getMessage());
                }
            }

        });
    }

    public void fetchItems(View view){
        ListView listView = findViewById(R.id.reisendeList);
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for (int i = 0; i < listView.getCount(); i++)
            if (checked.get(i)) {
                String item = reisenderList.get(i).getNickname();
                items.add(item);
                /* do whatever you want with the checked item */
            }
        itemAdapter.notifyDataSetChanged();
    }

    public int checkPosition()
    {
        ListView listView = findViewById(R.id.stopsList);
        int position = listView.getCheckedItemPosition();
            if (position >= 0) {
                return position;
            }
        return 0;
    }

    public void addBetrag(View view)
    {
        Stop stop =  reiseList.get(0).getStops().get(checkPosition());
        Reise reise = reiseList.get(0);
        EditText betragView = findViewById(R.id.betragField);
        String sbetrag = betragView.getText().toString();
        BigDecimal betrag = new BigDecimal(sbetrag);

        ListView listView = findViewById(R.id.reisendeList);
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for (int i = 0; i < listView.getCount(); i++)
            if (checked.get(i)) {
                Ausgabe ausgabe = new Ausgabe();
                ausgabe.setBetrag(betrag);
                ausgabe.setAnzahlReisende(checked.size());
                ausgabe.setZahler(MainActivity.currentUserData.getCurrentUser().getId());
                ausgabe.setSchuldner(reisenderList.get(i).getId());
                if(stop.getAusgaben() == null) stop.setAusgaben(new ArrayList<>());
                stop.getAusgaben().add(ausgabe);
            }
        reise.getStops().set(checkPosition(), stop);
        updateReise(reise);
        Log.d("ausgaben", stop.getAusgaben().toString());
    }

    public void updateReise(Reise reise)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/updatereise";
        //String url = "http://10.0.2.2:8080/api/userdaten/user";
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
                TextView textView = findViewById(R.id.errorRegView);
                String text = "Es konnte keine Verbindung zum Server hergestellt werden";
                textView.setText(text);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseReponse reiseReponse = new Gson().fromJson(response.body().string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                    //MainActivity.currentUser = reiseReponse.getReisender();
                    Log.d("ausgaben", reiseReponse.getReise().toString());
                    Looper.prepare();
                    Toast.makeText(PaymentDummyActivity.this, "success", Toast.LENGTH_LONG).show();
                }
                else {
                    //EditText editText = findViewById(R.id.dummyResultText);
                    //editText.setText(reiseReponse.getMessage());
                }
            }

        });
    }

    public void fetchPaymentInfo(View view)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/ausgaben";
        //String url = "http://10.0.2.2:8080/api/userdaten/user";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        Reise reise = currentReise;

        AusgabenRequest ausgabenRequest = new AusgabenRequest();
        if(reise == null) return;

        ausgabenRequest.setReise(reise);
        ausgabenRequest.setReisender(MainActivity.currentUserData.getCurrentUser());

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
                ReiseReponse reiseReponse = new Gson().fromJson(Objects.requireNonNull(response.body()).string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                    Log.d("zusammenfassung", reiseReponse.getMessage());
                    Log.d("zusammenfassung", reiseReponse.getSchuldner().toString());
                    Log.d("zusammenfassung", reiseReponse.getBetraege().toString());
                    runOnUiThread(() -> {
                        int counter = 0;
                        for(Reisender reisender : reiseReponse.getSchuldner()){
                            if(!reisender.getId().equals(MainActivity.currentUserData.getCurrentUser().getId())){
                                BigDecimal betrag = reiseReponse.getBetraege().get(counter);
                                String result = MainActivity.currentUserData.getCurrentUser().getNickname()
                                        + " bekommt " + betrag.toString() + "€ von " + reisender.getNickname();
                                if(betrag.compareTo(new BigDecimal(0)) < 0){
                                    result = MainActivity.currentUserData.getCurrentUser().getNickname()
                                            + " zahlt " + betrag.toString() + "€ an " + reisender.getNickname();
                                }
                                items.add(result);
                            }
                            itemAdapter.notifyDataSetChanged();
                            counter++;
                        }
                    });
                }
                else {
                    //EditText editText = findViewById(R.id.dummyResultText);
                    //editText.setText(reiseReponse.getMessage());
                }
            }

        });
    }
}