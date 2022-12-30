package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.roadsplit.R;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.model.UserAccount;
import com.example.roadsplit.reponses.ReiseReponse;
import com.example.roadsplit.reponses.UserResponse;
import com.example.roadsplit.requests.JoinRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DummyPlanungActivity extends AppCompatActivity {

    private Reisender reisender;
    private Reise currentReise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_planung);
        Intent i = getIntent();
        String userjson = i.getStringExtra("user");
        this.reisender = (new Gson()).fromJson(userjson, Reisender.class);
    }

    public void addReise(View view)
    {
        Reise reise = new Reise();
        reise.setName(((EditText)findViewById(R.id.reiseNameText)).getText().toString());
        reise.setUniquename(reisender.getId().toString());
        if (reisender.getReisen() == null) reisender.setReisen(new ArrayList<>());
        reisender.getReisen().add(reise);
        currentReise = reise;
    }

    public void addStop(View view)
    {
        Stop stop = new Stop();
        stop.setName(((EditText)findViewById(R.id.stopNameText)).getText().toString());
        String budget = ((EditText)findViewById(R.id.budgetText)).getText().toString();
        budget = budget.replaceAll(",", ".");
        stop.setBudget(new BigDecimal(budget));
        if(currentReise.getStops() == null) currentReise.setStops(new ArrayList<>());
        if(!currentReise.getStops().contains(stop)) currentReise.getStops().add(stop);
    }

    public void save(View view)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/reise";
        //String url = "http://10.0.2.2:8080/api/userdaten/user";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();


        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(currentReise));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        //Erstellt in einem neuen Thread eine Http Anfrage an den Webservice, ruft bei Erfolg onReponse() auf, bei Misserfolg onFailure()
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                TextView textView = findViewById(R.id.errorRegView);
                String text = "Es konnte keine Verbindung zum Server hergestellt werden";
                textView.setText(text);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseReponse reiseReponse = new Gson().fromJson(response.body().string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                    EditText editText = findViewById(R.id.dummyResultText);
                    Looper.prepare();
                    editText.setText(reiseReponse.getReise().toString());
                }
                else {
                    EditText editText = findViewById(R.id.dummyResultText);
                    editText.setText(reiseReponse.getMessage());
                }
            }

        });
    }

    public void copyToClipboard(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("test",
                ((EditText)findViewById(R.id.reiseNameText)).getText().toString());
        clipboard.setPrimaryClip(clip);
    }

    public void paste(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        ((EditText)findViewById(R.id.stopNameText)).setText(clip.getItemAt(0).getText().toString());
    }

    public void join(View view)
    {
        EditText editText = findViewById(R.id.joinReiseText);
        String uniquename = editText.getText().toString();
        JoinRequest joinRequest = new JoinRequest(reisender.getId(), uniquename);
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/join";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(joinRequest));
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                TextView textView = findViewById(R.id.errorRegView);
                String text = "Es konnte keine Verbindung zum Server hergestellt werden";
                textView.setText(text);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseReponse reiseReponse = new Gson().fromJson(response.body().string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                    EditText editText = findViewById(R.id.dummyResultText);
                    Looper.prepare();
                    editText.setText(reiseReponse.getReise().toString());
                }
                else {
                    EditText editText = findViewById(R.id.dummyResultText);
                    editText.setText(reiseReponse.getMessage());
                }
            }

        });
    }



}