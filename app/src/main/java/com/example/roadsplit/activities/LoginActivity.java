package com.example.roadsplit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.UserAccount;
import com.example.roadsplit.reponses.UserResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences reisenderPref;
    private SharedPreferences uebersichtPref;
    private Reisender reisender;

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
        String url = getResources().getString(R.string.baseendpoint) + "/api/userdaten/login";
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
        System.out.println(new Gson().toJson(userAccount));

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
                UserResponse userResponse = null;
                try {
                    userResponse = new Gson().fromJson(response.body().string(), UserResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {

                    EndpointConnector.storeJwtToken(userResponse.getMessage(), LoginActivity.this);
                    reisender = userResponse.getReisender();
                    SharedPreferences.Editor editor = reisenderPref.edit();
                    editor.putString("reisender", new Gson().toJson(reisender));
                    editor.apply();

                    editor = uebersichtPref.edit();
                    editor.putString("uebersicht", new Gson().toJson(userResponse.getReisen()));
                    editor.apply();

                    if (userResponse.getReisender().isFirsttimelogin()) {
                        reisender.setFirsttimelogin(false);
                        startFirstTimeActivity();
                        sendUserUpdate();
                    } else {
                        success();
                    }
                    findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
                    return;
                }


                try {
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
                } catch (NullPointerException e) {
                    Log.d("okhttp", "null message in userresponse " + e);
                }
                findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
            }
        });
    }

    public void sendUserUpdate() throws IOException {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(getResources().getString(R.string.baseendpoint) + "/api/userdaten/update").newBuilder();
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
        Intent intent = new Intent(this, ReiseUebersichtActivity.class);
        intent.putExtra("from", "login");
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


}
