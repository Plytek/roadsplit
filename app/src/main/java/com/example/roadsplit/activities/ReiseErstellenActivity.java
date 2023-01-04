package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roadsplit.R;
import com.example.roadsplit.helperclasses.AppSettings;
import com.example.roadsplit.model.LocationInfo;
import com.example.roadsplit.model.LocationInfoList;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseReponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReiseErstellenActivity extends AppCompatActivity{

    private Reisender reisender;
    private Reise currentReise;
    private List<String> zwischenstops;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> autocompleteAdapter;
    private AutoCompleteTextView start;
    private List<String> autocompletes;
    private long last = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_reise_erstellen);
        this.reisender = MainActivity.currentUser;
        zwischenstops = new ArrayList<>();
        autocompletes = new ArrayList<>();
        autocompletes.add("Maschen");
        autocompletes.add("Masczh");
        autocompletes.add("Masoej");
        autocompletes.add("Masfdg");


        ListView listView = findViewById(R.id.stopList);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, zwischenstops);
        listView.setAdapter(adapter);

        start = findViewById(R.id.startingPoint);
/*        start.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String text = start.getText().toString();
                if(text.isEmpty() || text.length() < 3) return false;
                fetchLocationInfo(text);
                return false;
            }
        });*/

        AppSettings.buttonPressDownEffect(findViewById(R.id.reiseErstellenButton));
        AppSettings.buttonPressDownEffect(findViewById(R.id.plusButton));
        AppSettings.buttonPressDownEffect(findViewById(R.id.minusButton));



    }

    //HERE
    public void addToZwischenstops(View view){
        EditText editText = findViewById(R.id.zwischenStopView);
        String stop = editText.getText().toString();
        if(stop.isEmpty()) return;
        try {
            if(zwischenstops.get(zwischenstops.size()-1).equals(stop)) return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        zwischenstops.add(stop);
        editText.setText(" ");
        adapter.notifyDataSetChanged();
    }

    public void deleteFromZwischenstops(View view){
        if(zwischenstops.isEmpty()) return;
        zwischenstops.remove(zwischenstops.size()-1);
        adapter.notifyDataSetChanged();
    }

    public void addReise()
    {
        Reise reise = new Reise();
        reise.setName(((EditText)findViewById(R.id.reiseName)).getText().toString());
        reise.setUniquename(reisender.getId().toString());
        if (reisender.getReisen() == null) reisender.setReisen(new ArrayList<>());
        reisender.getReisen().add(reise);
        currentReise = reise;
    }

    public void addInitialStop()
    {
        Stop stop = new Stop();
        stop.setName(((EditText)findViewById(R.id.startingPoint)).getText().toString());
        if(currentReise.getStops() == null) currentReise.setStops(new ArrayList<>());
        if(!currentReise.getStops().contains(stop)) currentReise.getStops().add(stop);
    }

    public void addLastStop(){
        Stop stop = new Stop();
        stop.setName(((EditText)findViewById(R.id.endStopView)).getText().toString());
        if(currentReise.getStops() == null) currentReise.setStops(new ArrayList<>());
        if(!currentReise.getStops().contains(stop)) currentReise.getStops().add(stop);
    }

    public void addZwischenStops(){
        for(String zwischenstop : zwischenstops){
            Stop stop = new Stop();
            stop.setName(zwischenstop);
            currentReise.getStops().add(stop);
        }
    }

    public void saveReise(View view)
    {
        if(currentReise == null) {
            if(((EditText)findViewById(R.id.reiseName)).getText().toString().isEmpty()) return;
            addReise();
        }
        addInitialStop();
        addZwischenStops();
        addLastStop();
        zwischenstops.clear();

        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/reise";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

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
                TextView textView = findViewById(R.id.errorRegView);
                String text = "Es konnte keine Verbindung zum Server hergestellt werden";
                textView.setText(text);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseReponse reiseReponse = new Gson().fromJson(response.body().string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                    MainActivity.currentUser = reiseReponse.getReisender();
                    reiseSuccess(reiseReponse.getReise().getUniquename());
                    Looper.prepare();
                    Toast.makeText(ReiseErstellenActivity.this, reiseReponse.getReise().toString(), Toast.LENGTH_LONG).show();
                }
                else {
                    //EditText editText = findViewById(R.id.dummyResultText);
                    //editText.setText(reiseReponse.getMessage());
                }
            }

        });
    }

    public void locationTest(View view)
    {
        fetchLocationInfo("ellernb");
    }

    private void fetchLocationInfo(String loc){
        long current = System.currentTimeMillis();
        if(last == 0) last = current;
        else if(current - last < 300){
            System.out.println(current - last);
            return;
        }
        last = current;

        OkHttpClient client = new OkHttpClient();
        String url = "https://eu1.locationiq.com/v1/search?key=" +
                getResources().getString(R.string.lockey) + "&q=" + loc + "&format=json";

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .get()
                .build();

        //Erstellt in einem neuen Thread eine Http Anfrage an den Webservice, ruft bei Erfolg onReponse() auf, bei Misserfolg onFailure()
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                LocationInfo[] locationInfo = new LocationInfo[1];
                try {
                    locationInfo = new Gson().fromJson(response.body().string(), LocationInfo[].class);
                } catch (Exception e){
                    try {
                        locationInfo[0] = new Gson().fromJson(response.body().string(), LocationInfo.class);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if(response.isSuccessful()) {

                    LocationInfo[] finalLocationInfo = locationInfo;

                    runOnUiThread(() -> {
                        autocompletes.clear();
                        for(LocationInfo info : finalLocationInfo)
                        {
                            autocompletes.add(info.getDisplay_name());
                        }

                        autocompleteAdapter.notifyDataSetChanged();
                    });
                    autocompletes.add("Maschen11");
                    Log.d("locationinfo", autocompletes.toString());
                }
            }

        });
    }

    private void reiseSuccess(String id)
    {
        Intent intent = new Intent(this, successCreateReiseActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }
}