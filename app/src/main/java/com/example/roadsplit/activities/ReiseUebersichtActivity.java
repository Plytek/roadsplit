package com.example.roadsplit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.adapter.UebersichtRecAdapter;
import com.example.roadsplit.helperclasses.DashboardSetup;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.reponses.ReiseUebersicht;
import com.example.roadsplit.reponses.UserResponse;
import com.example.roadsplit.reponses.WikiResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ReiseUebersichtActivity extends AppCompatActivity implements SensorEventListener {

    private Map<String, Bitmap> imageMap = new HashMap<>();
    private List<Reise> reisen;
    private List<ReiseUebersicht> uebersicht;
    private List<ReiseUebersicht> ongoingResponses;
    private List<ReiseUebersicht> finishedResponses;
    private Reise selectedReise;
    private RecyclerView reisenView;
    private RecyclerView reisenViewFinished;
    //private List<Bitmap> images;
    private int imageloadCounter;
    private Handler handler;
    private UebersichtRecAdapter uebersichtRecAdapter;
    private UebersichtRecAdapter uebersichtRecAdapterFinish;

    private SharedPreferences reisenderPref;
    private SharedPreferences uebersichtPref;
    private SharedPreferences reisePref;

    private Reisender reisender;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reise_uebersicht_test);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastUpdate = System.currentTimeMillis();

        String from = getIntent().getStringExtra("from");
        if (from != null && from.equals("success")) {
            imageMap = ReiseSuccessActivity.imageMap;
        } else if (from != null && from.equals("dashboard")) {
            imageMap = DashboardSetup.imageMap;
        } else {
            imageMap = LoginActivity.imageMap;
        }
        setResult(RESULT_OK);

        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#325a4f")));
        // setSupportActionBar(null);

        EndpointConnector.baseurl = getResources().getString(R.string.baseendpoint);

        this.reisenderPref = getSharedPreferences("reisender", MODE_PRIVATE);
        this.uebersichtPref = getSharedPreferences("uebersicht", MODE_PRIVATE);
        this.reisePref = getSharedPreferences("reise", MODE_PRIVATE);

        try {
            this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
            this.uebersicht = Arrays.asList(new Gson().fromJson(uebersichtPref.getString("uebersicht", "fehler"), ReiseUebersicht[].class));
        } catch (Exception e) {
            toLogin();
            finish();
            return;
        }

        if (EndpointConnector.getJwtToken(this).equals("error")) toLogin();

        this.ongoingResponses = new ArrayList<>();
        this.finishedResponses = new ArrayList<>();

        for (ReiseUebersicht reiseUebersicht : uebersicht) {
            if (reiseUebersicht.getReise().isOngoing())
                ongoingResponses.add(reiseUebersicht);
            else
                finishedResponses.add(reiseUebersicht);
        }

        reisen = new ArrayList<>();
        final int[] clickedPosition = {-1}; // to store the position of the clicked item
        reisen = reisender.getReisen();
        reisenView = findViewById(R.id.recyclerViewOnGoing);
        reisenViewFinished = findViewById(R.id.recyclerViewFinished);

        ImageView rightLaufend = findViewById(R.id.sidescrollRightLaufend);
        ImageView leftLaufend = findViewById(R.id.sidescrollLeftLaufend);
        ImageView rightFinished = findViewById(R.id.sidescrollRightFinished);
        ImageView leftFinished = findViewById(R.id.sidescrollLeftFinished);
        ImageView logout = findViewById(R.id.settingsView);
        FloatingActionButton neueReiseButton = findViewById(R.id.floatingNeueReiseButton);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        /*preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("reisender")) {
                    reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
                } else if (key.equals("uebersicht")) {
                    reiseResponses = new Gson().fromJson(reiseResponsePref.getString("reiseResponses", "fehler"), ReiseResponse[].class);
                    ongoingResponses = new ArrayList<>();
                    finishedResponses = new ArrayList<>();
                    for (ReiseResponse reiseResponse : reiseResponses) {
                        if (reiseResponse.getReise().isOngoing())
                            ongoingResponses.add(reiseResponse);
                        else
                            finishedResponses.add(reiseResponse);
                    }
                    uebersichtRecAdapter.notifyDataSetChanged();
                    uebersichtRecAdapterFinish.notifyDataSetChanged();
                }
                // code to handle change in value for the key
            }
        });*/

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        reisenView.setLayoutManager(layoutManager);
        RecyclerView.LayoutManager layoutManagerFinished = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        reisenViewFinished.setLayoutManager(layoutManagerFinished);

  /*      handler = new Handler(Looper.getMainLooper());
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            imageloadCounter = 0;
            EndpointConnector.fetchPaymentInfoSummary(reisender.getReisen().get(0), reisender, fetchPaymentSummaryCallback(false));
            handler.post(() -> {
                for (Reise reise : reisen) {
                    EndpointConnector.fetchImageFromWiki(reise, wikiCallback(reise));
                }
            });
        });*/


        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (clickedPosition[0] != -1) {
                    goToReise(selectedReise);
                }
                return true;
            }
        });


        reisenView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        reisenViewFinished.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        neueReiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReiseUebersichtActivity.this, NeueReiseActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLogin();
                SharedPreferences.Editor editor = reisenderPref.edit();
                editor.clear();
                editor.apply();
                editor = uebersichtPref.edit();
                editor.clear();
                editor.apply();

            }
        });
        uebersichtRecAdapter = new UebersichtRecAdapter(this, ongoingResponses, imageMap);
        reisenView.setAdapter(uebersichtRecAdapter);
        uebersichtRecAdapterFinish = new UebersichtRecAdapter(this, finishedResponses, imageMap);
        reisenViewFinished.setAdapter(uebersichtRecAdapterFinish);

        reisenView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = 0;
                try {
                    firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                } catch (Exception e) {
                    return;
                }
                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                if (firstVisibleItem == 0) {
                    rightLaufend.setVisibility(View.VISIBLE);
                    leftLaufend.setVisibility(View.GONE);
                } else if (lastVisibleItem == recyclerView.getAdapter().getItemCount() - 1) {
                    leftLaufend.setVisibility(View.VISIBLE);
                    rightLaufend.setVisibility(View.GONE);
                } else {
                    rightLaufend.setVisibility(View.GONE);
                    leftLaufend.setVisibility(View.GONE);
                }
            }
        });

        reisenViewFinished.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = 0;
                try {
                    firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                } catch (Exception e) {
                    return;
                }
                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                if (firstVisibleItem == 0) {
                    rightFinished.setVisibility(View.VISIBLE);
                    leftFinished.setVisibility(View.GONE);
                } else if (lastVisibleItem == recyclerView.getAdapter().getItemCount() - 1) {
                    leftFinished.setVisibility(View.VISIBLE);
                    rightFinished.setVisibility(View.GONE);
                } else {
                    rightFinished.setVisibility(View.GONE);
                    leftFinished.setVisibility(View.GONE);
                }
            }
        });

/*        handler.post(() -> {
            uebersichtRecAdapter = new UebersichtRecAdapter(this, ongoingResponses, imageMap);
            reisenView.setAdapter(uebersichtRecAdapter);
            uebersichtRecAdapterFinish = new UebersichtRecAdapter(this, finishedResponses, imageMap);
            reisenViewFinished.setAdapter(uebersichtRecAdapterFinish);
        });*/
    }

    private void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToReise(Reise reise) {
        Intent intent = new Intent(this, AusgabenActivity.class);
        String reiseString = new Gson().toJson(reise);
        intent.putExtra("reise", reiseString);
        startActivity(intent);
    }

    private Callback updateOverviewCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (response.isSuccessful()) {
                    UserResponse userResponse = new Gson().fromJson(response.body().string(), UserResponse.class);
                    reisender = userResponse.getReisender();
                    uebersicht = userResponse.getReisen();
                    SharedPreferences.Editor editor = reisenderPref.edit();
                    editor.putString("reisender", new Gson().toJson(reisender));
                    editor.apply();

                    editor = uebersichtPref.edit();
                    editor.putString("uebersicht", new Gson().toJson(userResponse.getReisen()));
                    editor.apply();

                    ongoingResponses = new ArrayList<>();
                    finishedResponses = new ArrayList<>();

                    for (ReiseUebersicht reiseUebersicht : uebersicht) {
                        if (reiseUebersicht.getReise().isOngoing())
                            ongoingResponses.add(reiseUebersicht);
                        else
                            finishedResponses.add(reiseUebersicht);
                    }

                    imageloadCounter = 0;
                    for (Reise reise : reisender.getReisen()) {
                        EndpointConnector.fetchImageFromWiki(reise, wikiCallback(reise));
                    }
                }
                if (response.code() == 403) {
                    toLogin();
                }
            }
        };
    }

    private Callback wikiCallback(Reise reise) {
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
                        downloadImages(url, reise.getName());
                    } catch (Exception e) {
                        downloadImages("https://cdn.discordapp.com/attachments/284675100253487104/1065300629448298578/globeicon.png", reise.getName());
                        //TODO: Set Default Image
                        e.printStackTrace();
                    }
                }
            }
        };
    }


    public void downloadImages(String url, String reisename) {
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
            //images.add(mIcon);
            imageMap.put(reisename, mIcon);
            imageloadCounter++;
            if (imageloadCounter == reisender.getReisen().size()) {
                runOnUiThread(() -> {
                    uebersichtRecAdapter.notifyDataSetChanged();
                    uebersichtRecAdapterFinish.notifyDataSetChanged();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (reisender == null) {
            toLogin();
        }
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        refresh();
    }

    private void refresh() {
        EndpointConnector.updateOverview(reisender, updateOverviewCallback(), this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            // Code to start a new Activity when the phone is shaken
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}