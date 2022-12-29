package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roadsplit.R;
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

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Intent intent = getIntent();
        if(intent.getStringExtra("registered") != null)
            Toast.makeText(this, "Account erfolgreich registriert!", Toast.LENGTH_LONG).show();
        findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
    }

    public void register(View view)
    {
        Intent intent = new Intent(this, RegistryActivity.class);
        startActivity(intent);
        finish();
    }

    public void realLogin(View view)
    {
        findViewById(R.id.loginProgressBar).setVisibility(View.VISIBLE);
       findViewById(R.id.erroremail).setVisibility(View.INVISIBLE);
       findViewById(R.id.errorPassword).setVisibility(View.INVISIBLE);

        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/userdaten/login";
        //String url = "http://10.0.2.2:8080/api/userdaten/user";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();


        UserAccount userAccount = new UserAccount();
        String username = ((EditText)findViewById(R.id.emailLogView)).getText().toString();
        String password = ((EditText)findViewById(R.id.passLogView)).getText().toString();

        boolean ok = true;
        if(username.isEmpty()){
            findViewById(R.id.erroremail).setVisibility(View.VISIBLE);
            findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
            ok = false;
        }
        if(password.isEmpty())
        {
            findViewById(R.id.errorPassword).setVisibility(View.VISIBLE);
            findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
            ok = false;
        }
        if(!ok) return;

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
                UserResponse userResponse =  new Gson().fromJson(response.body().string(), UserResponse.class);
                if(response.isSuccessful())
                {
                    MainActivity.currentUser = userResponse.getReisender();
                    Log.d("currentuser", MainActivity.currentUser.toString());
                    findViewById(R.id.loginProgressBar).setVisibility(View.INVISIBLE);
                    success();
                    return;
                }

                String message = userResponse.getMessage();
                if(message.contains("Passwort"))
                {
                    runOnUiThread(() -> {
                        TextView textView;
                        textView = findViewById(R.id.errorPassword);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(message);
                    });
                }
                else
                {
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

    public void success()
    {
        Intent intent = new Intent(this, NeueReiseActivity.class);
        startActivity(intent);
        finish();
    }
}