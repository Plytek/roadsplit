package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;

import com.example.roadsplit.R;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DummyPlanungActivity extends AppCompatActivity {

    private Reisender reisender;
    private Reise currentReise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_planung);
        Intent i = getIntent();
        String userjson = i.getStringExtra("user");
        this.reisender = (new Gson()).fromJson(userjson, Reisender.class);
    }

    public void addReise(View view)
    {
        Reise reise = new Reise();
        reise.setName(((EditText)findViewById(R.id.reiseNameText)).getText().toString());
        if (reisender.getReisen() == null) reisender.setReisen(new ArrayList<>());
        reisender.getReisen().add(reise);
        currentReise = reise;
    }

    public void addStop(View view)
    {
        Stop stop = new Stop();
        stop.setName(((EditText)findViewById(R.id.stopNameText)).getText().toString());
        String budget = ((EditText)findViewById(R.id.stopNameText)).getText().toString();
        budget = budget.replaceAll(",", ".");
        stop.setBudget(new BigDecimal(budget));

        //reisender.getReisen().stream().findAny();
    }



}