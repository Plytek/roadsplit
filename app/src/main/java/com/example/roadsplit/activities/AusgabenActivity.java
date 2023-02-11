package com.example.roadsplit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.adapter.DashboardOverviewAdapter;
import com.example.roadsplit.adapter.ReiseUebersichtAdapter;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.reponses.ReiseResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AusgabenActivity extends AppCompatActivity {
    private ViewPager screenPager;
    private ReiseUebersichtAdapter reiseUebersichtAdapter;
    private DashboardOverviewAdapter dashboardOverviewAdapter;
    private TabLayout tabLayout;
    private Reise reise;

    private Reisender reisender;
    private ReiseResponse reiseResponse;
    private AusgabenReport ausgabenReport;
    private SharedPreferences reisenderPref;
    private SharedPreferences reiseResponsePref;
    private SharedPreferences reisePref;
    private SharedPreferences reportPref;
    private boolean returning;

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

        Intent intent = getIntent();
        returning = intent.getBooleanExtra("returning", false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        this.reisenderPref = getSharedPreferences("reisender", MODE_PRIVATE);
        this.reiseResponsePref = getSharedPreferences("reiseResponse", MODE_PRIVATE);
        this.reisePref = getSharedPreferences("reise", MODE_PRIVATE);
        this.reportPref = getSharedPreferences("report", MODE_PRIVATE);

        this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
        //this.reiseResponse = new Gson().fromJson(reisenderPref.getString("reiseResponse", "fehler"), ReiseResponse.class);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Gson gson = new Gson();
                switch (key) {
                    case "reisender":
                        reisender = gson.fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
                        break;
                    case "reiseResponse":
                        reiseResponse = gson.fromJson(reiseResponsePref.getString("reiseResponse", "fehler"), ReiseResponse.class);
                        break;
                    case "reise":
                        reise = gson.fromJson(reiseResponsePref.getString("reise", "fehler"), Reise.class);
                        break;
                    case "report":
                        ausgabenReport = gson.fromJson(reiseResponsePref.getString("report", "fehler"), AusgabenReport.class);
                        dashboardOverviewAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        //EndpointConnector.fetchPaymentInfo(reise, reisender,fetchPaymentCallback());
        EndpointConnector.fetchAusgabenReport(reise, reisender, ausgabenReportCallback());
    }

    private Callback ausgabenReportCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                ausgabenReport = gson.fromJson(response.body().string(), AusgabenReport.class);
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        //reiseUebersichtAdapter = new ReiseUebersichtAdapter(AusgabenActivity.this, AusgabenActivity.this, null, reiseResponse, ausgabenReport);

                        SharedPreferences.Editor editor = reisenderPref.edit();

                        editor = reportPref.edit();
                        editor.putString("report", gson.toJson(ausgabenReport));
                        editor.apply();

                        dashboardOverviewAdapter = new DashboardOverviewAdapter(AusgabenActivity.this, AusgabenActivity.this, ausgabenReport);

                        screenPager.setAdapter(dashboardOverviewAdapter);
                        tabLayout = findViewById(R.id.tab_indicator2);
                        tabLayout.setupWithViewPager(screenPager);
                        tabLayout.getTabAt(0).setIcon(R.drawable.dokumenteiconp);
                        tabLayout.getTabAt(1).setIcon(R.drawable.stoppsiconp);
                        tabLayout.getTabAt(2).setIcon(R.drawable.packlisteiconp);
                        tabLayout.getTabAt(3).setIcon(R.drawable.kosteniconp);
                        if (returning) screenPager.setCurrentItem(3);
                    });
                }
            }

        };
    }


    private Callback fetchPaymentCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                reiseResponse = gson.fromJson(response.body().string(), ReiseResponse.class);
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {

                        SharedPreferences.Editor editor = reisenderPref.edit();
                        editor.putString("reisender", gson.toJson(reiseResponse.getReisender()));
                        editor.apply();

                        editor = reiseResponsePref.edit();
                        editor.putString("reiseResponse", gson.toJson(reiseResponse));
                        editor.apply();

                        editor = reisePref.edit();
                        editor.putString("reise", gson.toJson(reiseResponse.getReise()));
                        editor.apply();


                        EndpointConnector.fetchAusgabenReport(reise, reisender, ausgabenReportCallback());
                    });
                }
            }

        };
    }

}