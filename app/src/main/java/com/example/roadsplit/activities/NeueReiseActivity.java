package com.example.roadsplit.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.helperclasses.ButtonEffect;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.reponses.ReiseResponse;
import com.example.roadsplit.reponses.UserResponse;
import com.example.roadsplit.reponses.WikiResponse;
import com.example.roadsplit.requests.JoinRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NeueReiseActivity extends AppCompatActivity {


    public static Map<String, Bitmap> imageMap = new HashMap<>();
    private Dialog dialog;

    private Reisender reisender;
    private SharedPreferences reisenderPref;
    private SharedPreferences reiseResponsePref;
    private SharedPreferences reisePref;
    private SharedPreferences uebersichtPref;
    private int imageloadCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neue_reise);

        this.reisenderPref = getSharedPreferences("reisender", MODE_PRIVATE);
        this.reiseResponsePref = getSharedPreferences("reiseResponse", MODE_PRIVATE);
        this.reisePref = getSharedPreferences("reise", MODE_PRIVATE);
        this.uebersichtPref = getSharedPreferences("uebersicht", MODE_PRIVATE);
        reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
        if (reisender != null) {
            String welcome = "Was geht, " + reisender.getNickname();
            ((TextView) findViewById(R.id.textView10)).setText(welcome);
        }
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ButtonEffect.buttonPressDownEffect(findViewById(R.id.reiseErstellenButton));
        ButtonEffect.buttonPressDownEffect(findViewById(R.id.reiseBeitretenButton));

        ImageView logout = findViewById(R.id.logoutView);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EndpointConnector.toLogin(NeueReiseActivity.this);
                SharedPreferences.Editor editor = reisenderPref.edit();
                editor.clear();
                editor.apply();
                editor = uebersichtPref.edit();
                editor.clear();
                editor.apply();

            }
        });
    }

    public void reiseErstellen(View view) {
        Intent intent = new Intent(this, ReiseErstellenActivity.class);
        startActivity(intent);
    }

    public void reiseBeitreten(View view) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    public void join(View view) {
        EditText editText = dialog.findViewById(R.id.pinTextView);
        String uniquename = editText.getText().toString();
        JoinRequest joinRequest = new JoinRequest(reisender.getId(), uniquename);
        EndpointConnector.reiseBeitreten(joinRequest, reiseBeitretenCallback(), this);

    }

    private Callback reiseBeitretenCallback() {
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

                    editor = reiseResponsePref.edit();
                    editor.putString("reiseResponse", gson.toJson(reiseResponse));
                    editor.apply();

                    editor = reisePref.edit();
                    editor.putString("reise", gson.toJson(reiseResponse.getReise()));
                    editor.apply();

                    Looper.prepare();
                    dialog.dismiss();
                    Toast.makeText(NeueReiseActivity.this, "Reise erfolgreich beigetreten", Toast.LENGTH_LONG).show();
                    EndpointConnector.updateOverview(reisender, updateOverviewCallback(), NeueReiseActivity.this);
                } else if (response.code() == 403) {
                    EndpointConnector.toLogin(NeueReiseActivity.this);
                }
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
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
                Intent intent = new Intent(NeueReiseActivity.this, ReiseUebersichtActivity.class);
                intent.putExtra("from", "join");
                startActivityForResult(intent, 1);
            }
        });
    }

    private Callback updateOverviewCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                TextView textView = findViewById(R.id.errorRegView);
                String text = "Es konnte keine Verbindung zum Server hergestellt werden";
                textView.setText(text);
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