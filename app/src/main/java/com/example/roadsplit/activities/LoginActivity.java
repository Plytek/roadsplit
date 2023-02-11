package com.example.roadsplit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.helperclasses.ButtonEffect;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.UserAccount;
import com.example.roadsplit.reponses.UserResponse;
import com.example.roadsplit.reponses.WikiResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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

public class LoginActivity extends AppCompatActivity {

    public static Map<String, Bitmap> imageMap = new HashMap<>();
    private SharedPreferences reisenderPref;
    private SharedPreferences uebersichtPref;
    private Reisender reisender;
    private int imageloadCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        this.reisenderPref = getSharedPreferences("reisender", MODE_PRIVATE);
        this.uebersichtPref = getSharedPreferences("uebersicht", MODE_PRIVATE);

        Intent intent = getIntent();
        if (intent.getStringExtra("registered") != null)
            Toast.makeText(this, "Account erfolgreich registriert!", Toast.LENGTH_LONG).show();
        findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
        //findViewById(R.id.plsloginButton).setOnTouchListener(new HapticTouchListener());
        Button button = findViewById(R.id.plsloginButton);
        ButtonEffect.buttonPressDownEffect(button);
    }

    //TODO: Textfeld auf PW Feld von Email ändern
    public void register(View view) {
        Intent intent = new Intent(this, RegistryActivity.class);
        startActivity(intent);
        finish();
    }

    public void login(View view) {
        findViewById(R.id.loginProgressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.erroremail).setVisibility(View.INVISIBLE);
        findViewById(R.id.errorPassword).setVisibility(View.INVISIBLE);

        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/userdaten/login";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        UserAccount userAccount = new UserAccount();
        String username = ((EditText) findViewById(R.id.emailLogView)).getText().toString();
        String password = ((EditText) findViewById(R.id.passLogView)).getText().toString();

        boolean ok = true;
        if (username.isEmpty()) {
            findViewById(R.id.erroremail).setVisibility(View.VISIBLE);
            findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
            ok = false;
        }
        if (password.isEmpty()) {
            findViewById(R.id.errorPassword).setVisibility(View.VISIBLE);
            findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
            ok = false;
        }
        if (!ok) {
            reject(view);
            return;
        }

        userAccount.setNickname(username);
        userAccount.setPassword(password);
        userAccount.setFirsttimelogin(false);
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(userAccount));

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
                UserResponse userResponse = new Gson().fromJson(response.body().string(), UserResponse.class);
                if (response.isSuccessful()) {
                    reisender = userResponse.getReisender();
                    SharedPreferences.Editor editor = reisenderPref.edit();
                    editor.putString("reisender", new Gson().toJson(reisender));
                    editor.apply();

                    editor = uebersichtPref.edit();
                    editor.putString("uebersicht", new Gson().toJson(userResponse.getReisen()));
                    editor.apply();


                    if (userResponse.getReisender().isFirsttimelogin()) {
                        reisender.setFirsttimelogin(false);
                        imageloadCounter = 0;
                        for (Reise reise : reisender.getReisen()) {
                            EndpointConnector.fetchImageFromWiki(reise, wikiCallback(reise, true));
                        }
                        sendUserUpdate();
                        startFirstTimeActivity();
                    } else {
                        imageloadCounter = 0;
                        for (Reise reise : reisender.getReisen()) {
                            EndpointConnector.fetchImageFromWiki(reise, wikiCallback(reise, false));
                        }
                    }
                    findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
                    return;
                }

                String message = userResponse.getMessage();
                if (message.contains("Passwort")) {
                    runOnUiThread(() -> {
                        TextView textView;
                        textView = findViewById(R.id.errorPassword);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(message);
                    });
                } else {
                    runOnUiThread(() -> {
                        TextView textView;
                        textView = findViewById(R.id.erroremail);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(message);
                    });
                }
                findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
            }
        });
    }

    public void sendUserUpdate() throws IOException {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(MainActivity.BASEURL + "/api/userdaten/update").newBuilder();
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(reisender));

        OkHttpClient nextclient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();
        Call nextcall = nextclient.newCall(request);
        nextcall.execute();
    }

    public void success() {
        Intent intent = new Intent(this, ReiseUebersichtTestActivity.class);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
    }

    public void startFirstTimeActivity() {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
        finish();
    }

    public void reject(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    public void confirm(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
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
                if (firsttime)
                    startFirstTimeActivity();
                else
                    success();
            }
        });
    }
}
