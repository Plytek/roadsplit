package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Looper;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roadsplit.R;
import com.example.roadsplit.adapter.ReiseUebersichtAdapter;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.reponses.ReiseReponse;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainScreenReisenActivity extends AppCompatActivity {
    private ViewPager screenPager;
    private ReiseUebersichtAdapter reiseUebersichtAdapter;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen_reisen);

        // setup viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        fetchReise(MainActivity.currentUser.getReisen().get(0));

    }

    private void fetchReise(Reise reise)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/reisesuche";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(reise));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        //Erstellt in einem neuen Thread eine Http Anfrage an den Webservice, ruft bei Erfolg onReponse() auf, bei Misserfolg onFailure()
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseReponse reiseReponse = new Gson().fromJson(response.body().string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                        runOnUiThread(() -> {
                            reiseUebersichtAdapter = new ReiseUebersichtAdapter(MainScreenReisenActivity.this, MainScreenReisenActivity.this, null
                                    , MainActivity.currentUser.getReisen().get(0), reiseReponse.getReisendeList());
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

        });
    }


}