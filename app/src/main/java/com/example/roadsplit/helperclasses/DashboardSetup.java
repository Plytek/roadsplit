package com.example.roadsplit.helperclasses;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.activities.PaymentActivity;
import com.example.roadsplit.activities.ReiseUebersichtActivity;
import com.example.roadsplit.adapter.DashboardAusgabenAdapter;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.reponses.UserResponse;
import com.example.roadsplit.reponses.WikiResponse;
import com.example.roadsplit.requests.UpdateRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DashboardSetup {

    public static Map<String, Bitmap> imageMap = new HashMap<>();
    private final View layoutScreen;
    private final Context mContext;
    private final AusgabenActivity ausgabenActivity;
    private final SharedPreferences reportPref;
    private final SharedPreferences reisenderPref;
    private final SharedPreferences reiseResponsePref;
    private final int standardColor;
    private final int pressedColor = 0xe0f47521;
    private SharedPreferences uebersichtPref;
    private AusgabenReport ausgabenReport;
    private Reisender reisender;
    private PieChart pieChart;
    private ExpandableLayout expandableLayout;
    private TextView expandButton;
    private ProgressBar progressBar;
    private int imageloadCounter;
    private FloatingActionButton statisticsButton;

    public DashboardSetup(View layoutScreen, Context mContext, AusgabenActivity ausgabenActivity) {
        this.layoutScreen = layoutScreen;
        this.mContext = mContext;
        this.ausgabenActivity = ausgabenActivity;

        this.standardColor = mContext.getResources().getColor(R.color.rdarkgreen);
        this.reportPref = mContext.getSharedPreferences("report", MODE_PRIVATE);
        this.reisenderPref = mContext.getSharedPreferences("reisender", MODE_PRIVATE);
        this.uebersichtPref = mContext.getSharedPreferences("uebersicht", MODE_PRIVATE);
        this.reiseResponsePref = mContext.getSharedPreferences("reiseResponses", MODE_PRIVATE);

        this.pieChart = layoutScreen.findViewById(R.id.piechart);
        this.expandableLayout = layoutScreen.findViewById(R.id.expandable_layout);
        this.expandButton = layoutScreen.findViewById(R.id.expand_button);
        this.progressBar = layoutScreen.findViewById(R.id.dashboardProgressBar);
        this.statisticsButton = layoutScreen.findViewById(R.id.statisticButton);


        this.progressBar.setVisibility(View.INVISIBLE);

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
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandableLayout.isExpanded()) {
                    statisticsButton.setImageDrawable(ausgabenActivity.getResources().getDrawable(R.drawable.baseline_chevron_right_black_24dp));
                    expandableLayout.collapse();
                } else {
                    expandableLayout.expand();
                    statisticsButton.setImageDrawable(ausgabenActivity.getResources().getDrawable(R.drawable.baseline_expand_more_black_24dp));
                }
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
        double gesamt = 0;
        for (Ausgabe ausgabe : ausgaben) {
            switch (ausgabe.getAusgabenTyp()) {
                case Allgemein:
                    allgemein = allgemein + ausgabe.getBetrag().doubleValue();
                    gesamt = gesamt + ausgabe.getBetrag().doubleValue();
                    break;
                case Restaurants:
                    restaurants = restaurants + ausgabe.getBetrag().doubleValue();
                    gesamt = gesamt + ausgabe.getBetrag().doubleValue();
                    break;
                case Transport:
                    transport = transport + ausgabe.getBetrag().doubleValue();
                    gesamt = gesamt + ausgabe.getBetrag().doubleValue();
                    break;
                case Tanken:
                    tanken = tanken + ausgabe.getBetrag().doubleValue();
                    gesamt = gesamt + ausgabe.getBetrag().doubleValue();
                    break;
                case Unterkunft:
                    unterkunft = unterkunft + ausgabe.getBetrag().doubleValue();
                    gesamt = gesamt + ausgabe.getBetrag().doubleValue();
                    break;
                case Shopping:
                    shopping = shopping + ausgabe.getBetrag().doubleValue();
                    gesamt = gesamt + ausgabe.getBetrag().doubleValue();
                    break;
                case Aktivitaeten:
                    aktivitaeten = aktivitaeten + ausgabe.getBetrag().doubleValue();
                    gesamt = gesamt + ausgabe.getBetrag().doubleValue();
                    break;
                case Gebuehren:
                    gebuehren = gebuehren + ausgabe.getBetrag().doubleValue();
                    gesamt = gesamt + ausgabe.getBetrag().doubleValue();
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
                Intent intent = new Intent(ausgabenActivity, PaymentActivity.class);
                ausgabenActivity.startActivityForResult(intent, 1);
            }
        });

        reiseBeenden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(ausgabenActivity, reiseBeenden, Gravity.END);
                popupMenu.getMenuInflater().inflate(R.menu.options_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.reiseBeenden_item:
                                progressBar.setVisibility(View.VISIBLE);
                                UpdateRequest updateRequest = new UpdateRequest();
                                Reise reise = ausgabenReport.getReise();
                                reise.setOngoing(false);
                                updateRequest.setReise(reise);
                                updateRequest.setReisender(reisender);
                                EndpointConnector.updateReise(updateRequest, updateReiseCallback(), ausgabenActivity);
                                return true;
                            case R.id.logout_item:
                                SharedPreferences.Editor editor = reisenderPref.edit();
                                editor.clear();
                                editor.apply();
                                editor = reiseResponsePref.edit();
                                editor.clear();
                                editor.apply();
                                editor = uebersichtPref.edit();
                                editor.clear();
                                editor.apply();
                                editor = reportPref.edit();
                                editor.clear();
                                editor.apply();
                                EndpointConnector.toLogin(ausgabenActivity);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }

           /* @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                //final EditText input = new EditText(mContext);
                //builder.setView(input);
                builder.setTitle("Reise wirklich beenden?");
                builder.setPositiveButton("Reise beenden", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Set the TextView's text to the value entered by the user
                        //textView.setText(input.getText().toString());
                        Reise reise = ausgabenReport.getReise();
                        reise.setOngoing(false);
                        UpdateRequest updateRequest = new UpdateRequest(reisender, reise);
                        //TODO: FIX THIS
                        EndpointConnector.updateReise(updateRequest, updateReiseCallback(), mContext);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }*/
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
                AusgabenReport ausgabenResponse = new Gson().fromJson(response.body().string(), AusgabenReport.class);
                if (response.isSuccessful()) {
                    ausgabenReport = ausgabenResponse;
                    SharedPreferences.Editor editor = reportPref.edit();
                    editor.putString("report", new Gson().toJson(ausgabenReport));
                    editor.apply();

                    EndpointConnector.updateOverview(reisender, updateOverviewCallback(), ausgabenActivity);

                } else if (response.code() == 403) {
                    EndpointConnector.toLogin(ausgabenActivity);
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
                } else if (response.code() == 403) {
                    EndpointConnector.toLogin(ausgabenActivity);
                }
            }
        };
    }


    private Callback wikiCallback(Reise reise, boolean firsttime) {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                WikiResponse wikiResponse = null;
                try {
                    wikiResponse = new Gson().fromJson(response.body().string(), WikiResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {

                    try {
                        String url = wikiResponse.getPages().get(0).getThumbnail().getUrl();
                        url = "https:" + url;
                        url = url.replaceAll("/\\d+px-", "/200px-");
                        downloadImages(url, reise.getName(), firsttime);


                    } catch (Exception e) {
                        downloadImages("https://cdn.discordapp.com/attachments/284675100253487104/1065300629448298578/globeicon.png", reise.getName(), firsttime);
                        //TODO: Set Default Image
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void downloadImages(String url, String reisename, boolean firsttime) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            imageMap.put(reisename, mIcon);
            imageloadCounter++;
            if (imageloadCounter == reisender.getReisen().size()) {
                progressBar.setVisibility(View.INVISIBLE);
                //reiseübersichtAnzeigen();
                Intent intent = new Intent(ausgabenActivity, ReiseUebersichtActivity.class);
                intent.putExtra("from", "dashboard");
                ausgabenActivity.startActivityForResult(intent, 1);
                ausgabenActivity.finish();
            }
        });
    }

    private Callback updateOverviewCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                UserResponse userResponse = null;
                try {
                    userResponse = new Gson().fromJson(response.body().string(), UserResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    reisender = userResponse.getReisender();

                    SharedPreferences.Editor editor = reisenderPref.edit();
                    editor.putString("reisender", new Gson().toJson(reisender));
                    editor.apply();

                    editor = uebersichtPref.edit();
                    editor.putString("uebersicht", new Gson().toJson(userResponse.getReisen()));
                    editor.apply();

                    imageloadCounter = 0;

                    for (Reise reise : reisender.getReisen()) {
                        EndpointConnector.fetchImageFromWiki(reise, wikiCallback(reise, false));
                    }

                }
            }
        };
    }
}
