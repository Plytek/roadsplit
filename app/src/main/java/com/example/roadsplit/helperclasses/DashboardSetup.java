package com.example.roadsplit.helperclasses;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.R;
import com.example.roadsplit.activities.PaymentActivity;
import com.example.roadsplit.adapter.DashboardAusgabenAdapter;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.reponses.ReiseResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

public class DashboardSetup {

    private View layoutScreen;
    private Context mContext;

    private AusgabenReport ausgabenReport;
    private SharedPreferences reportPref;

    private int standardColor;
    private final int pressedColor = 0xe0f47521;

    public DashboardSetup(View layoutScreen, Context mContext) {
        this.layoutScreen = layoutScreen;
        this.mContext = mContext;

        this.standardColor = mContext.getResources().getColor(R.color.rdarkgreen);
        this.reportPref = mContext.getSharedPreferences("report", MODE_PRIVATE);

        this.ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("report"))
                {
                    ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);
                    setUpDashboard();
                }
            }
        });

    }

    public void setUpDashboard(){
        RecyclerView details = layoutScreen.findViewById(R.id.ausgabenDetailsRecycler);
        Button privatausgaben = layoutScreen.findViewById(R.id.buttonPrivatAusgaben);
        Button gruppenausgaben = layoutScreen.findViewById(R.id.buttonGruppeAusgaben);
        Button schuldendetails = layoutScreen.findViewById(R.id.buttonAbrechnungsAusgabe);
        FloatingActionButton ausgabeButton = layoutScreen.findViewById(R.id.floating_action_button);

        ButtonEffect.buttonPressDownEffect(ausgabeButton);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        details.setLayoutManager(layoutManager);
        DashboardAusgabenAdapter dashboardAusgabenAdapter = new DashboardAusgabenAdapter(mContext, ausgabenReport, layoutScreen, "privatAusgabe");
        details.setAdapter(dashboardAusgabenAdapter);

        setUpColors(standardColor, pressedColor, standardColor);
        privatausgaben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpColors(standardColor, pressedColor, standardColor);
                details.setLayoutManager(layoutManager);
                DashboardAusgabenAdapter dashboardAusgabenAdapter = new DashboardAusgabenAdapter(mContext, ausgabenReport, layoutScreen, "privatAusgabe");
                details.setAdapter(dashboardAusgabenAdapter);
            }
        });

        gruppenausgaben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpColors(pressedColor, standardColor, standardColor);
                details.setLayoutManager(layoutManager);
                DashboardAusgabenAdapter dashboardAusgabenAdapter = new DashboardAusgabenAdapter(mContext, ausgabenReport, layoutScreen, "gruppenAusgabe");
                details.setAdapter(dashboardAusgabenAdapter);
            }
        });

        schuldendetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpColors(standardColor, standardColor, pressedColor);
                details.setLayoutManager(layoutManager);
                DashboardAusgabenAdapter dashboardAusgabenAdapter = new DashboardAusgabenAdapter(mContext, ausgabenReport, layoutScreen, "schulden");
                details.setAdapter(dashboardAusgabenAdapter);
            }
        });

        ausgabeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PaymentActivity.class);
                Activity activity = (Activity) mContext;
                mContext.startActivity(intent);
                activity.finish();
            }
        });
    }

    private void setUpColors(int gruppeColor, int privatColor, int schuldenColor){
        Button privatausgaben = layoutScreen.findViewById(R.id.buttonPrivatAusgaben);
        Button gruppenausgaben = layoutScreen.findViewById(R.id.buttonGruppeAusgaben);
        Button schuldendetails = layoutScreen.findViewById(R.id.buttonAbrechnungsAusgabe);

        gruppenausgaben.getBackground().setColorFilter(gruppeColor, PorterDuff.Mode.SRC_ATOP);
        privatausgaben.getBackground().setColorFilter(privatColor, PorterDuff.Mode.SRC_ATOP);
        schuldendetails.getBackground().setColorFilter(schuldenColor, PorterDuff.Mode.SRC_ATOP);
    }
}
