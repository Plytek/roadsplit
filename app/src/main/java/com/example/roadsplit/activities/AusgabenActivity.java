package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.roadsplit.R;
import com.example.roadsplit.adapter.ReiseUebersichtAdapter;
import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.reponses.ReiseReponse;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AusgabenActivity extends AppCompatActivity {
    private ViewPager screenPager;
    private ReiseUebersichtAdapter reiseUebersichtAdapter;
    private TabLayout tabLayout;
    private Reise reise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen_reisen);
        try {
            Intent intent = getIntent();
            String stringReise = intent.getStringExtra("reise");
            reise = new Gson().fromJson(stringReise, Reise.class);
        } catch (Exception e) {
            Log.d("errorRoadsplit", e.getMessage());
        }
        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        EndpointConnector.fetchPaymentInfo(reise, fetchPaymentCallback());
    }

    private Callback fetchPaymentCallback(){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseReponse reiseReponse = new Gson().fromJson(response.body().string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                    runOnUiThread(() -> {
                        reiseUebersichtAdapter = new ReiseUebersichtAdapter(AusgabenActivity.this, AusgabenActivity.this, null, reiseReponse);
                        MainActivity.currentUser = reiseReponse.getReisender();
                        screenPager.setAdapter(reiseUebersichtAdapter);
                        tabLayout = findViewById(R.id.tab_indicator2);
                        tabLayout.setupWithViewPager(screenPager);
                        tabLayout.getTabAt(0).setIcon(R.drawable.ausgabeiconselector);
                        tabLayout.getTabAt(1).setIcon(R.drawable.minusbutton);
                        tabLayout.getTabAt(2).setIcon(R.drawable.smallbuttondots);
                        tabLayout.getTabAt(3).setIcon(R.drawable.minusbutton);
                    });
                }
            }

        };
    }
}