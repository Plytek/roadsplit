package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roadsplit.IntroViewPagerAdapter;
import com.example.roadsplit.R;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseReponse;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NeueTestActivity extends AppCompatActivity{

    private Reisender reisender;
    private Reise currentReise;
    List<String> names;
    ArrayAdapter<String> adapter;
    boolean initialStopSaved = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_neue_test);
        this.reisender = MainActivity.currentUser;
        names = new ArrayList<>();

        ListView listView = findViewById(R.id.stopList);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);

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
        if(currentReise == null) addReise();
        Stop stop = new Stop();
        stop.setName(((EditText)findViewById(R.id.startingPoint)).getText().toString());
        /*String budget = ((EditText)findViewById(R.id.budgetText)).getText().toString();
        budget = budget.replaceAll(",", ".");
        stop.setBudget(new BigDecimal(budget));*/
        if(currentReise.getStops() == null) currentReise.setStops(new ArrayList<>());
        if(!currentReise.getStops().contains(stop)) currentReise.getStops().add(stop);
        initialStopSaved = true;
    }

    public void addStop()
    {
        if(currentReise == null) addReise();
        if(!initialStopSaved) addInitialStop();
        Stop stop = new Stop();
        stop.setName(((EditText)findViewById(R.id.stopName)).getText().toString());

        /*String budget = ((EditText)findViewById(R.id.budgetText)).getText().toString();
        budget = budget.replaceAll(",", ".");
        stop.setBudget(new BigDecimal(budget));*/
        if(currentReise.getStops() == null) currentReise.setStops(new ArrayList<>());
        List<Stop> stops = currentReise.getStops();
        if(!currentReise.getStops().isEmpty() &&
                !stop.getName().equals(stops.get(stops.size()-1).getName())) {
            currentReise.getStops().add(stop);
        }

    }

    public void addEverything(View view){
        if(currentReise == null) addReise();
        if(!initialStopSaved) addInitialStop();
        addStop();
        ListView listView = findViewById(R.id.stopList);
        names.clear();
        for(Stop stop : currentReise.getStops())
        {
            names.add(stop.getName());
        }
        adapter.notifyDataSetChanged();
    }

    public void save(View view)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/reise";
        //String url = "http://10.0.2.2:8080/api/userdaten/user";
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
                    Looper.prepare();
                    Toast.makeText(NeueTestActivity.this, reiseReponse.getReise().toString(), Toast.LENGTH_LONG).show();
                }
                else {
                    //EditText editText = findViewById(R.id.dummyResultText);
                    //editText.setText(reiseReponse.getMessage());
                }
            }

        });
    }
}