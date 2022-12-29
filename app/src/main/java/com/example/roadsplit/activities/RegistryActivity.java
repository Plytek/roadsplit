package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

public class RegistryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

    }

    public void create(View view)
    {
        findViewById(R.id.errorNickname).setVisibility(View.INVISIBLE);
        findViewById(R.id.errorPassword).setVisibility(View.INVISIBLE);
        findViewById(R.id.erroremail).setVisibility(View.INVISIBLE);

        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/userdaten/user";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        UserAccount userAccount = new UserAccount();
        String username = ((EditText)findViewById(R.id.userRegView)).getText().toString();
        String email = ((EditText)findViewById(R.id.emailRegView)).getText().toString();
        String password = ((EditText)findViewById(R.id.passRegView)).getText().toString();

        if(!areInputsValid(username, password, email)) return;

        userAccount.setNickname(username);
        userAccount.setEmail(email);
        userAccount.setPassword(password);
        userAccount.setFirsttimelogin(true);

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
                    toLogin();
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
                if(message.contains("Name"))
                {
                    runOnUiThread(() -> {
                        TextView textView;
                        textView = findViewById(R.id.errorNickname);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(message);
                    });
                }
                if(message.contains("Email"))
                {
                    runOnUiThread(() -> {
                        TextView textView;
                        textView = findViewById(R.id.erroremail);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(message);
                    });
                }
            }
        });
    }

    public void toLogin()
    {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.putExtra("registered", "success");
        startActivity(intent);
        finish();
    }

    public boolean areInputsValid(String username, String password, String email) {
        if (username.isEmpty()) {
            findViewById(R.id.errorNickname).setVisibility(View.VISIBLE);
            return false;
        }
        if (password.isEmpty()) {
            findViewById(R.id.errorPassword).setVisibility(View.VISIBLE);
            return false;
        }
        if (email.isEmpty()) {
            findViewById(R.id.erroremail).setVisibility(View.VISIBLE);
            return false;
        }
        if (!email.contains("@")) {
            TextView textView = findViewById(R.id.erroremail);
            textView.setVisibility(View.VISIBLE);
            textView.setText("Keine gültige Email");
            return false;
        }
        if(username.contains("@")) {
            TextView textView = findViewById(R.id.errorNickname);
            textView.setVisibility(View.VISIBLE);
            textView.setText("Name darf kein @ enthalten");
            return false;
        }
        return true;
    }

}