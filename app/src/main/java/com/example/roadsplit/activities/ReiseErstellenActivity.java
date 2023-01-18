package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.roadsplit.R;
import com.example.roadsplit.helperclasses.AppSettings;
import com.example.roadsplit.helperclasses.EndpointConnector;
import com.example.roadsplit.model.Location;
import com.example.roadsplit.model.LocationInfo;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseReponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ReiseErstellenActivity extends AppCompatActivity{

    private Reisender reisender;
    private Reise currentReise;
    private List<String> zwischenstops;
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView start;
    private AutoCompleteTextView end;
    private AutoCompleteTextView zwischenstop;
    private List<String> suggestions;
    private long last = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_reise_erstellen);
        this.reisender = MainActivity.currentUser;
        zwischenstops = new ArrayList<>();
        suggestions = new ArrayList<>();


        ListView listView = findViewById(R.id.stopList);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, zwischenstops);
        listView.setAdapter(adapter);


        start = findViewById(R.id.startingPoint);
        end = findViewById(R.id.endStopView);
        zwischenstop = findViewById(R.id.zwischenStopView);
        start.addTextChangedListener(fetchAutoCompleteTextWatcher());
        end.addTextChangedListener(fetchAutoCompleteTextWatcher());
        zwischenstop.addTextChangedListener(fetchAutoCompleteTextWatcher());

        start.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                System.out.println(selectedItem);
            }
        });

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

    public void saveReise(View view) {
        if (currentReise == null) {
            if (((EditText) findViewById(R.id.reiseName)).getText().toString().isEmpty()) return;
            addReise();
        }
        addInitialStop();
        addZwischenStops();
        addLastStop();
        zwischenstops.clear();
        EndpointConnector.saveReise(currentReise, saveReiseCallback());

    }

    private Callback saveReiseCallback()
    {
        return new Callback() {
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
                }
            }
        };
    }

    private TextWatcher fetchAutoCompleteTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Get the user's input
                long currentTimeMillis = System.currentTimeMillis();
                String input = s.toString();
                if(input.length() < 3) return;

                if (currentTimeMillis - last >= 1000) {
                    // At least 1 second has passed since the last check
                    fetchLocationInfo(input);
                    last = currentTimeMillis;
                }
            }
        };
    }

    private void fetchLocationInfo(String loc) {
        long current = System.currentTimeMillis();
        if (last == 0) last = current;
        else if (current - last < 300) {
            System.out.println(current - last);
            return;
        }
        last = current;
        EndpointConnector.fetchLocationInfo(loc, locationCallback());
    }
    private Callback locationCallback()
    {
       return new Callback() {
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
                        suggestions = new ArrayList<>();
                        for(LocationInfo info : finalLocationInfo)
                        {
                            Location location = info.getAddress();
                            suggestions.add(location.getName() + " (" + location.getPostcode() + ", " + location.getCountry() + ")");
                        }
                        ArrayAdapter<String> suggestionsAdapter = new ArrayAdapter<>(ReiseErstellenActivity.this,
                                android.R.layout.simple_list_item_1, suggestions);
                        start.setAdapter(suggestionsAdapter);
                        end.setAdapter(suggestionsAdapter);
                        zwischenstop.setAdapter(suggestionsAdapter);
                    });
                    Objects.requireNonNull(response.body()).close();
                }
            }
        };
    }

    private void reiseSuccess(String id)
    {
        Intent intent = new Intent(this, successCreateReiseActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }
}