package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.roadsplit.R;
import com.example.roadsplit.adapter.UebersichtListAdapter;
import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.reponses.ReiseReponse;
import com.example.roadsplit.reponses.WikiResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ReiseUebersichtActivity extends AppCompatActivity {
    private List<Reise> reisen;
    private ReiseReponse[] reiseResponses;
    private Reise selectedReise;
    private ListView reisenView;
    private List<Bitmap> images;
    private int imageloadCounter;
    private Handler handler;
    UebersichtListAdapter uebersichtListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reise_uebersicht);


        reisen = new ArrayList<>();
        images = new ArrayList<>();
        final int[] clickedPosition = {-1}; // to store the position of the clicked item
        reisen = MainActivity.currentUser.getReisen();
        reisenView = findViewById(R.id.reisenUebersichtListView);
        reiseResponses = new ReiseReponse[1];

        handler = new Handler(Looper.getMainLooper());
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            imageloadCounter = 0;
            EndpointConnector.fetchPaymentInfoSummary(MainActivity.currentUser.getReisen().get(0), fetchPaymentSummaryCallback());
            handler.post(() -> {
                for(Reise reise : reisen)
                {
                    EndpointConnector.fetchImageFromWiki(reise, wikiCallback(reise));
                }
            });
        });

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EndpointConnector.fetchPaymentInfoSummary(MainActivity.currentUser.getReisen().get(0), fetchPaymentSummaryCallback());
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        reisenView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        UebersichtListAdapter uebersichtListAdapter = new UebersichtListAdapter(Arrays.asList(reiseResponses), ReiseUebersichtActivity.this, images);
                        reisenView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                        reisenView.setAdapter(uebersichtListAdapter);
                    clickedPosition[0] = i;
                    selectedReise = reisen.get(i);
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(clickedPosition[0] != -1)
                {
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

        handler.post(() -> {
            uebersichtListAdapter = new UebersichtListAdapter(Arrays.asList(reiseResponses), ReiseUebersichtActivity.this, images);
            reisenView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            reisenView.setAdapter(uebersichtListAdapter);
        });
    }


    private void goToReise(Reise reise){
        Intent intent = new Intent(this, AusgabenActivity.class);
        String reiseString = new Gson().toJson(reise);
        intent.putExtra("reise", reiseString);
        startActivity(intent);
    }

    private Callback wikiCallback(Reise reise)
    {
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
                if(response.isSuccessful()) {
                    try {
                        String url = wikiResponse.getPages().get(0).getThumbnail().getUrl();
                        url = "https:" + url;
                        url = url.replaceAll("/\\d+px-", "/200px-");
                        downloadImages(url);
                    } catch (Exception e) {
                        downloadImages("https://cdn.discordapp.com/attachments/284675100253487104/1065300629448298578/globeicon.png");
                        //TODO: Set Default Image
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private Callback fetchPaymentSummaryCallback(){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    reiseResponses = new Gson().fromJson(response.body().string(), ReiseReponse[].class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(response.isSuccessful()) {
                    MainActivity.currentUser = reiseResponses[0].getReisender();
                     runOnUiThread(() -> {
                             uebersichtListAdapter = new UebersichtListAdapter(Arrays.asList(reiseResponses), ReiseUebersichtActivity.this, images);
                             reisenView.setAdapter(uebersichtListAdapter);
                             uebersichtListAdapter.notifyDataSetChanged();
                     });
                    }
                }
        };
    }


    public void downloadImages(String url){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
                Bitmap mIcon = null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                images.add(mIcon);
                handler.post(() -> {
                        uebersichtListAdapter.notifyDataSetChanged();
                });
        });
    }
}