package com.example.roadsplit.helperclasses;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.activities.PaymentActivity;
import com.example.roadsplit.activities.ReiseUebersichtTestActivity;
import com.example.roadsplit.adapter.DashboardAusgabenAdapter;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.reponses.ReiseResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DashboardSetup {

    private final View layoutScreen;
    private final Context mContext;
    private final AusgabenActivity ausgabenActivity;
    private final SharedPreferences reportPref;
    private final SharedPreferences reisenderPref;
    private final SharedPreferences reiseResponsePref;
    private final int standardColor;
    private final int pressedColor = 0xe0f47521;
    private AusgabenReport ausgabenReport;
    private Reisender reisender;

    private PieChart pieChart;
    private ExpandableLayout expandableLayout;
    private TextView expandButton;

    public DashboardSetup(View layoutScreen, Context mContext, AusgabenActivity ausgabenActivity) {
        this.layoutScreen = layoutScreen;
        this.mContext = mContext;
        this.ausgabenActivity = ausgabenActivity;

        this.standardColor = mContext.getResources().getColor(R.color.rdarkgreen);
        this.reportPref = mContext.getSharedPreferences("report", MODE_PRIVATE);
        this.reisenderPref = mContext.getSharedPreferences("reisender", MODE_PRIVATE);
        this.reiseResponsePref = mContext.getSharedPreferences("reiseResponses", MODE_PRIVATE);

        this.pieChart = layoutScreen.findViewById(R.id.piechart);
        this.expandableLayout = layoutScreen.findViewById(R.id.expandable_layout);
        this.expandButton = layoutScreen.findViewById(R.id.expand_button);

        this.ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);
        this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("report")) {
                    ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);
                    setUpDashboard();
                }
            }
        });
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandableLayout.isExpanded())
                    expandableLayout.collapse();
                else
                    expandableLayout.expand();
            }
        });
    }

    public void setUpPiechart(String type) {
        TextView allgemeinView = layoutScreen.findViewById(R.id.allgemeinPercent);
        TextView restaurantsView = layoutScreen.findViewById(R.id.restaurantsPercent);
        TextView transportView = layoutScreen.findViewById(R.id.transportPercent);
        TextView tankenView = layoutScreen.findViewById(R.id.tankenPercent);
        TextView unterkunftView = layoutScreen.findViewById(R.id.unterkunftPercent);
        TextView shoppingView = layoutScreen.findViewById(R.id.shoppingPercent);
        TextView aktivitaetenView = layoutScreen.findViewById(R.id.aktivitaetenPercent);
        TextView gebuerenView = layoutScreen.findViewById(R.id.gebuerenPercent);


        List<Ausgabe> ausgaben;

        switch (type) {
            case "privat":
                ausgaben = ausgabenReport.getAusgabenFuerSelbst();
                break;
            case "gruppe":
                ausgaben = ausgabenReport.getAusgabenFuerGruppe();
                break;
            case "schulden":
                ausgaben = ausgabenReport.getAusgabenFuerGruppe();
                break;
            default:
                ausgaben = ausgabenReport.getAusgabenFuerSelbst();
                break;
        }


        double allgemein = 0;
        double restaurants = 0;
        double transport = 0;
        double tanken = 0;
        double unterkunft = 0;
        double shopping = 0;
        double aktivitaeten = 0;
        double gebuehren = 0;
        double gesamt = ausgaben.size();
        for (Ausgabe ausgabe : ausgaben) {
            switch (ausgabe.getAusgabenTyp()) {
                case Allgemein:
                    allgemein++;
                    break;
                case Restaurants:
                    restaurants++;
                    break;
                case Transport:
                    transport++;
                    break;
                case Tanken:
                    tanken++;
                    break;
                case Unterkunft:
                    unterkunft++;
                    break;
                case Shopping:
                    shopping++;
                    break;
                case Aktivitaeten:
                    aktivitaeten++;
                    break;
                case Gebuehren:
                    gebuehren++;
                    break;
            }
        }
        allgemein = (double) Math.round(allgemein / gesamt * 100 * 100) / 100;
        restaurants = (double) Math.round(restaurants / gesamt * 100 * 100) / 100;
        transport = (double) Math.round(transport / gesamt * 100 * 100) / 100;
        tanken = (double) Math.round(tanken / gesamt * 100 * 100) / 100;
        unterkunft = (double) Math.round(unterkunft / gesamt * 100 * 100) / 100;
        shopping = (double) Math.round(shopping / gesamt * 100 * 100) / 100;
        aktivitaeten = (double) Math.round(aktivitaeten / gesamt * 100 * 100) / 100;
        gebuehren = (double) Math.round(gebuehren / gesamt * 100 * 100) / 100;

        allgemeinView.setText(allgemein + "%");
        restaurantsView.setText(restaurants + "%");
        transportView.setText(transport + "%");
        tankenView.setText(tanken + "%");
        unterkunftView.setText(unterkunft + "%");
        shoppingView.setText(shopping + "%");
        aktivitaetenView.setText(aktivitaeten + "%");
        gebuerenView.setText(gebuehren + "%");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pieChart.clearChart();
            pieChart.addPieSlice(
                    new PieModel(
                            "Allgemein",
                            (int) allgemein,
                            mContext.getColor(R.color.red)));

            pieChart.addPieSlice(
                    new PieModel(
                            "Restaurants",
                            (int) restaurants,
                            mContext.getColor(R.color.rlightgreen)));
            pieChart.addPieSlice(
                    new PieModel(
                            "Transport",
                            (int) transport,
                            mContext.getColor(R.color.rgrey)));
            pieChart.addPieSlice(
                    new PieModel(
                            "Tanken",
                            (int) tanken,
                            mContext.getColor(R.color.purple_200)));
            pieChart.addPieSlice(
                    new PieModel(
                            "Unterkunft",
                            (int) unterkunft,
                            mContext.getColor(R.color.marineblue)));
            pieChart.addPieSlice(
                    new PieModel(
                            "Shopping",
                            (int) shopping,
                            mContext.getColor(R.color.yellow)));
            pieChart.addPieSlice(
                    new PieModel(
                            "Aktivitäten",
                            (int) aktivitaeten,
                            mContext.getColor(R.color.rdarkgreen)));
            pieChart.addPieSlice(
                    new PieModel(
                            "Gebühren",
                            (int) gebuehren,
                            mContext.getColor(R.color.black)));
        }
        for (PieModel pieModel : pieChart.getData()) {
            //pieModel.get
        }
        pieChart.animate();
    }

    public void animatePieChart() {
        pieChart.animate();
    }

    public void setUpDashboard() {
        RecyclerView details = layoutScreen.findViewById(R.id.ausgabenDetailsRecycler);
        Button privatausgaben = layoutScreen.findViewById(R.id.buttonPrivatAusgaben);
        Button gruppenausgaben = layoutScreen.findViewById(R.id.buttonGruppeAusgaben);
        Button schuldendetails = layoutScreen.findViewById(R.id.buttonAbrechnungsAusgabe);
        Button reiseBeenden = layoutScreen.findViewById(R.id.reiseAbschliessenButton);
        FloatingActionButton ausgabeButton = layoutScreen.findViewById(R.id.floating_action_button);

        if (!ausgabenReport.getReise().isOngoing()) {
            ausgabeButton.setVisibility(View.GONE);
            reiseBeenden.setVisibility(View.GONE);
        } else {
            ausgabeButton.setVisibility(View.VISIBLE);
            reiseBeenden.setVisibility(View.VISIBLE);
        }


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
                setUpPiechart("privat");
            }
        });

        gruppenausgaben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpColors(pressedColor, standardColor, standardColor);
                details.setLayoutManager(layoutManager);
                DashboardAusgabenAdapter dashboardAusgabenAdapter = new DashboardAusgabenAdapter(mContext, ausgabenReport, layoutScreen, "gruppenAusgabe");
                details.setAdapter(dashboardAusgabenAdapter);
                setUpPiechart("gruppe");
            }
        });

        schuldendetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpColors(standardColor, standardColor, pressedColor);
                details.setLayoutManager(layoutManager);
                DashboardAusgabenAdapter dashboardAusgabenAdapter = new DashboardAusgabenAdapter(mContext, ausgabenReport, layoutScreen, "schulden");
                details.setAdapter(dashboardAusgabenAdapter);
                setUpPiechart("schulden");
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

        reiseBeenden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.reiseabschliessenpopup);
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.getWindow().getAttributes().windowAnimations = R.style.PopUpAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);

                Button bestaetigen = dialog.findViewById(R.id.confirmReiseBeendenButton);
                Button abbrechen = dialog.findViewById(R.id.cancelReiseBeendenButton);

                bestaetigen.setOnClickListener(btn -> {
                    Reise reise = ausgabenReport.getReise();
                    reise.setOngoing(false);
                    EndpointConnector.updateReise(reise, updateReiseCallback());
                    dialog.dismiss();
                });

                abbrechen.setOnClickListener(btn -> {
                    dialog.dismiss();
                });

            }
        });
    }

    private void setUpColors(int gruppeColor, int privatColor, int schuldenColor) {
        Button privatausgaben = layoutScreen.findViewById(R.id.buttonPrivatAusgaben);
        Button gruppenausgaben = layoutScreen.findViewById(R.id.buttonGruppeAusgaben);
        Button schuldendetails = layoutScreen.findViewById(R.id.buttonAbrechnungsAusgabe);

        gruppenausgaben.getBackground().setColorFilter(gruppeColor, PorterDuff.Mode.SRC_ATOP);
        privatausgaben.getBackground().setColorFilter(privatColor, PorterDuff.Mode.SRC_ATOP);
        schuldendetails.getBackground().setColorFilter(schuldenColor, PorterDuff.Mode.SRC_ATOP);
    }

    private Callback updateReiseCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseResponse reiseResponse = new Gson().fromJson(response.body().string(), ReiseResponse.class);
                if (response.isSuccessful()) {
                    //EndpointConnector.fetchAusgabenReport(reiseResponse.getReise(), reiseResponse.getReisender(), updateAusgabenCallback());
                    EndpointConnector.fetchPaymentInfoSummary(ausgabenReport.getReise(), reisender, fetchPaymentSummaryCallback());
                }
            }
        };
    }

    private Callback updateAusgabenCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ausgabenReport = new Gson().fromJson(response.body().string(), AusgabenReport.class);
                if (response.isSuccessful()) {
                    Intent intent = new Intent(mContext, AusgabenActivity.class);
                    intent.putExtra("reise", new Gson().toJson(ausgabenReport.getReise()));
                    SharedPreferences.Editor editor = reportPref.edit();
                    editor.putString("report", new Gson().toJson(ausgabenReport));
                    editor.apply();
                    mContext.startActivity(intent);
                    ausgabenActivity.finish();
                }
            }
        };
    }

    private Callback fetchPaymentSummaryCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                ReiseResponse[] reiseResponses = gson.fromJson(response.body().string(), ReiseResponse[].class);

                if (response.isSuccessful()) {
                    Reisender reisender = reiseResponses[0].getReisender();

                    SharedPreferences.Editor editor = reisenderPref.edit();
                    editor.putString("reisender", gson.toJson(reisender));
                    editor.apply();

                    editor = reiseResponsePref.edit();
                    editor.putString("reiseResponses", gson.toJson(reiseResponses));
                    editor.apply();

                    Intent intent = new Intent(mContext, ReiseUebersichtTestActivity.class);
                    mContext.startActivity(intent);
                    ausgabenActivity.finish();
                }
            }
        };
    }
}
