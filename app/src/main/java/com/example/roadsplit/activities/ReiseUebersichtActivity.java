package com.example.roadsplit.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.adapter.UebersichtRecAdapter;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.reponses.ReiseResponse;
import com.example.roadsplit.reponses.ReiseUebersicht;
import com.example.roadsplit.reponses.UserResponse;
import com.example.roadsplit.reponses.WikiResponse;
import com.example.roadsplit.requests.JoinRequest;
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

    private static Map<String, Bitmap> imageMap = new HashMap<>();
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

    private ImageView rightLaufend;
    private ImageView leftLaufend;
    private ImageView rightFinished;
    private ImageView leftFinished;
    private ImageView logout;
    private TextView finishedText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reise_uebersicht_test);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastUpdate = System.currentTimeMillis();

        setResult(RESULT_OK);

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


        if (uebersicht == null || uebersicht.isEmpty()) {
            Intent intent = new Intent(this, NeueReiseActivity.class);
            startActivity(intent);
        }
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
        progressBar = findViewById(R.id.uebersichtProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        rightLaufend = findViewById(R.id.sidescrollRightLaufend);
        leftLaufend = findViewById(R.id.sidescrollLeftLaufend);
        rightFinished = findViewById(R.id.sidescrollRightFinished);
        leftFinished = findViewById(R.id.sidescrollLeftFinished);
        logout = findViewById(R.id.settingsView);
        finishedText = findViewById(R.id.textViewFinished);
        FloatingActionButton neueReiseButton = findViewById(R.id.floatingNeueReiseButton);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        reisenView.setLayoutManager(layoutManager);
        RecyclerView.LayoutManager layoutManagerFinished = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        reisenViewFinished.setLayoutManager(layoutManagerFinished);


        reisenView.setVisibility(View.INVISIBLE);
        reisenViewFinished.setVisibility(View.INVISIBLE);

     /*   finishedText.setVisibility(View.VISIBLE);
        if (finishedResponses == null || finishedResponses.isEmpty()) {
            finishedText.setVisibility(View.GONE);
            rightFinished.setVisibility(View.GONE);
            leftFinished.setVisibility(View.GONE);
        } else if (finishedResponses.size() == 1) {
            rightFinished.setVisibility(View.GONE);
            leftFinished.setVisibility(View.GONE);
        }
        if (ongoingResponses == null || ongoingResponses.isEmpty() || ongoingResponses.size() == 1) {
            rightLaufend.setVisibility(View.GONE);
            leftLaufend.setVisibility(View.GONE);
        }*/

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
                //Intent intent = new Intent(ReiseUebersichtActivity.this, NeueReiseActivity.class);
                //startActivity(intent);
                Dialog dialog = new Dialog(ReiseUebersichtActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.beitreten_join_popup);
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);

                Button erstellen = dialog.findViewById(R.id.reiseErstellenPopupButton);
                Button join = dialog.findViewById(R.id.reiseBeitretenPopupButton);
                erstellen.setOnClickListener(v -> {
                    Intent intent = new Intent(ReiseUebersichtActivity.this, ReiseErstellenActivity.class);
                    startActivity(intent);
                });
                join.setOnClickListener(v -> {
                    dialog.dismiss();
                    reiseBeitreten();
                });

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
                if (ongoingResponses.size() == 1) {
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
                if (finishedResponses.size() == 1) {
                    rightFinished.setVisibility(View.GONE);
                    leftFinished.setVisibility(View.GONE);
                }
            }
        });

        lastUpdate = System.currentTimeMillis();
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

    private Callback updateOverviewCallback(boolean refresh) {
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
                        EndpointConnector.fetchImageFromWiki(reise, wikiCallback(reise, refresh));
                    }
                }
                if (response.code() == 403) {
                    toLogin();
                }
            }
        };
    }

    private Callback wikiCallback(Reise reise, boolean refresh) {
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
                        downloadImages(url, reise.getName(), refresh);
                    } catch (Exception e) {
                        downloadImages("https://cdn.discordapp.com/attachments/284675100253487104/1065300629448298578/globeicon.png", reise.getName(), refresh);
                        //TODO: Set Default Image
                        e.printStackTrace();
                    }
                }
            }
        };
    }


    public void downloadImages(String url, String reisename, boolean refresh) {
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
                    uebersichtRecAdapter = new UebersichtRecAdapter(this, ongoingResponses, imageMap);
                    reisenView.setAdapter(uebersichtRecAdapter);
                    uebersichtRecAdapterFinish = new UebersichtRecAdapter(this, finishedResponses, imageMap);
                    reisenViewFinished.setAdapter(uebersichtRecAdapterFinish);
                    reisenView.setVisibility(View.VISIBLE);
                    reisenViewFinished.setVisibility(View.VISIBLE);

                    finishedText.setVisibility(View.VISIBLE);
                    if (finishedResponses == null || finishedResponses.isEmpty()) {
                        finishedText.setVisibility(View.GONE);
                        rightFinished.setVisibility(View.GONE);
                        leftFinished.setVisibility(View.GONE);
                    } else if (finishedResponses.size() == 1) {
                        rightFinished.setVisibility(View.GONE);
                        leftFinished.setVisibility(View.GONE);
                    }
                    if (ongoingResponses == null || ongoingResponses.isEmpty() || ongoingResponses.size() == 1) {
                        rightLaufend.setVisibility(View.GONE);
                        leftLaufend.setVisibility(View.GONE);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                });

/*                Intent intent = new Intent(this, ReiseUebersichtActivity.class);
                startActivity(intent);
                finish();*/
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (reisender == null || System.currentTimeMillis() - lastUpdate > 86400000) {
            toLogin();
        }
        if (uebersicht == null || uebersicht.isEmpty()) {
            finish();
        }
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();
        refresh();
    }

    private void refresh() {
        progressBar.setVisibility(View.VISIBLE);
        EndpointConnector.updateOverview(reisender, updateOverviewCallback(false), this);
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

    private Callback reiseBeitretenCallback(Dialog dialog) {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                TextView textView = findViewById(R.id.errorRegView);
                String text = "Es konnte keine Verbindung zum Server hergestellt werden";
                textView.setText(text);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                ReiseResponse reiseResponse = gson.fromJson(response.body().string(), ReiseResponse.class);
                if (response.isSuccessful()) {

                    reisender = reiseResponse.getReisender();
                    SharedPreferences.Editor editor = reisenderPref.edit();
                    editor.putString("reisender", gson.toJson(reiseResponse.getReisender()));
                    editor.apply();

                    editor = reisePref.edit();
                    editor.putString("reise", gson.toJson(reiseResponse.getReise()));
                    editor.apply();

                    Looper.prepare();
                    dialog.dismiss();
                    Toast.makeText(ReiseUebersichtActivity.this, "Reise erfolgreich beigetreten", Toast.LENGTH_LONG).show();
                    EndpointConnector.updateOverview(reisender, updateOverviewCallback(true), ReiseUebersichtActivity.this);
                } else if (response.code() == 403) {
                    EndpointConnector.toLogin(ReiseUebersichtActivity.this);
                }
            }
        };
    }

    private void reiseBeitreten() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        Button join = dialog.findViewById(R.id.reiseBeitretenSlide);
        join.setOnClickListener(v -> {
            TextView idView = dialog.findViewById(R.id.pinTextView);
            String uniquename = idView.getText().toString();
            JoinRequest joinRequest = new JoinRequest(reisender.getId(), uniquename);
            EndpointConnector.reiseBeitreten(joinRequest, reiseBeitretenCallback(dialog), this);
        });

    }
}