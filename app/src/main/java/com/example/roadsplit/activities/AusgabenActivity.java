package com.example.roadsplit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.adapter.DashboardOverviewAdapter;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AusgabenActivity extends AppCompatActivity {
    private ViewPager screenPager;
    private DashboardOverviewAdapter dashboardOverviewAdapter;
    private TabLayout tabLayout;
    private Reise reise;

    private Reisender reisender;
    private AusgabenReport ausgabenReport;
    private SharedPreferences reisenderPref;
    private SharedPreferences reiseResponsePref;
    private SharedPreferences reisePref;
    private SharedPreferences reportPref;
    private int returning;

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
        try {
            returning = Integer.parseInt(intent.getStringExtra("returning"));
            //TODO: Richtige Exception fangen
        } catch (Exception e) {
            returning = 0;
        }
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
        EndpointConnector.fetchAusgabenReport(reise, reisender, ausgabenReportCallback(), this);
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

                        reise = ausgabenReport.getReise();

                        SharedPreferences.Editor editor = reportPref.edit();
                        editor.putString("report", gson.toJson(ausgabenReport));
                        editor.apply();

                        dashboardOverviewAdapter = new DashboardOverviewAdapter(AusgabenActivity.this, AusgabenActivity.this, ausgabenReport);


                        screenPager.setAdapter(dashboardOverviewAdapter);
                        tabLayout = findViewById(R.id.tab_indicator2);
                        int width = tabLayout.getWidth();
                        int height = tabLayout.getHeight();
                        //Drawable drawable = getResources().getDrawable(R.drawable.tabindicatorneu);
                        //drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 0.1), (int) (drawable.getIntrinsicHeight() * 0.1));
                        //tabLayout.setSelectedTabIndicator(drawable);
                        tabLayout.setupWithViewPager(screenPager);
                        tabLayout.getTabAt(0).setIcon(R.drawable.reisekosteniconneu);
                        tabLayout.getTabAt(1).setIcon(R.drawable.zwiconneutest);
                        tabLayout.getTabAt(2).setIcon(R.drawable.luggageicon96);
                        tabLayout.getTabAt(3).setIcon(R.drawable.documentsiconneutwo);
                        screenPager.setCurrentItem(returning);
                    });
                } else if (response.code() == 403) {
                    EndpointConnector.toLogin(AusgabenActivity.this);
                }
            }

        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
    }

}