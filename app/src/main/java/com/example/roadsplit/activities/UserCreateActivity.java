package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.roadsplit.R;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.model.UserAccount;
import com.example.roadsplit.reponses.UserResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserCreateActivity extends AppCompatActivity {

    private final String BASEURL = "https://825b-88-70-249-101.ngrok.io";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create);

    }

    public void create(View view)
    {
        OkHttpClient client = new OkHttpClient();
        String url = BASEURL + "/api/userdaten/user";
        //String url = "http://10.0.2.2:8080/api/userdaten/user";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();


        UserAccount userAccount = new UserAccount();
        String username = ((EditText)findViewById(R.id.nameText)).getText().toString();
        String email = ((EditText)findViewById(R.id.emailText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordText)).getText().toString();

        if(username.isEmpty() ||
            email.isEmpty() ||
            password.isEmpty() ||
        !email.contains("@"))
        {
            TextView textView = findViewById(R.id.resultText);
            textView.setText("fail");
            return;
        }

        userAccount.setNickname(username);
        userAccount.setEmail(email);
        userAccount.setPassword(password);

        CheckBox checkBox = findViewById(R.id.firstTimeLoginBox);
        userAccount.setFirsttimelogin(checkBox.isChecked());
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
                    //MainActivity.currentUser = userResponse.getReisender();
                }
                TextView textView = findViewById(R.id.resultText);
                String text = "User: " + userResponse.getReisender() + "\n" + userResponse.getMessage();
                textView.setText(text);
            }
        });
    }

    public void login(View view)
    {
        OkHttpClient client = new OkHttpClient();
        String url = BASEURL + "/api/userdaten/login";
        //String url = "http://10.0.2.2:8080/api/userdaten/user";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();


        UserAccount userAccount = new UserAccount();
        String username = ((EditText)findViewById(R.id.nameText)).getText().toString();
        String email = ((EditText)findViewById(R.id.emailText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordText)).getText().toString();

        if(username.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty() ||
                !email.contains("@"))
        {
            TextView textView = findViewById(R.id.resultText);
            textView.setText("fail");
            return;
        }

        userAccount.setNickname(username);
        userAccount.setEmail(email);
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
                }
                TextView textView = findViewById(R.id.resultText);
                String text = "User: " + userResponse.getReisender() + "\n" + userResponse.getMessage();
                textView.setText(text);
            }
        });
    }
    
}