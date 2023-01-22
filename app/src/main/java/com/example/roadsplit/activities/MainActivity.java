package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.testing.DummyPlanungActivity;
import com.example.roadsplit.activities.testing.MapActivity;
import com.example.roadsplit.activities.testing.PaymentDummyActivity;
import com.example.roadsplit.activities.testing.UserCreateActivity;
import com.example.roadsplit.adapter.UebersichtListAdapter;
import com.example.roadsplit.model.CurrentUserData;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.UserAccount;
import com.example.roadsplit.reponses.ReiseReponse;
import com.example.roadsplit.reponses.UserResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Getter
@Setter
public class MainActivity extends AppCompatActivity {


    private UserAccount userAccount = null;
    //public static Reisender currentUser;
    public static final String BASEURL = "http://167.172.167.221:8080";
    public static CurrentUserData currentUserData;
    //public static final String BASEURL = "https://b5dc-88-70-249-101.ngrok.io";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Das hier auszukommentieren damit die App beim starten auf den Debugger wartet
        //Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        currentUserData = new CurrentUserData();
        try {
            login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Startet einen neuen Intent (Activity) - die MapActivity
    public void openMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        //Gibt zusätzliche Daten mit durch .putExtra(Key, Value). In diesem Fall den aktuellen Useraccount als String
        intent.putExtra("user", (new Gson()).toJson(currentUserData.getCurrentUser()));
        //Startet die Activity
        startActivity(intent);
    }

    public void neueReise(View view) {
        Intent intent = new Intent(this, NeueReiseActivity.class);
        startActivity(intent);
    }

    public void createUser(View view){
        Intent intent = new Intent(this, UserCreateActivity.class);
        startActivity(intent);
    }

    public void registrieren(View view) {
        Intent intent = new Intent(this, RegistryActivity.class);
        startActivity(intent);
    }

    public void reiseText(View view){
        Intent intent = new Intent(this, DummyPlanungActivity.class);
        intent.putExtra("user", (new Gson()).toJson(currentUserData.getCurrentUser()));
        startActivity(intent);
    }

    public void turorialAnim(View view){
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

    public void einloggen(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void reiseErstelen(View view){
        Intent intent = new Intent(this, PaymentDummyActivity.class);
        startActivity(intent);
    }

    public void testBent(View view){
        Intent intent = new Intent(this, ReiseErstellenActivity.class);
        startActivity(intent);
    }

    public void testKnecht(View view){
        //Intent intent = new Intent(this, testknechtActivitay.class);
        //startActivity(intent);
    }


    public void reiseDetail(View view){
        Intent intent = new Intent(this, ReiseUebersichtTestActivity.class);
        startActivity(intent);
    }


    private void login()
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/userdaten/login";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        UserAccount userAccount = new UserAccount();
        String username = "lpa";
        String password = "test1";

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
                    currentUserData.setCurrentUser(userResponse.getReisender());
                    EndpointConnector.fetchPaymentInfoSummary(currentUserData.getCurrentUser().getReisen().get(0), fetchPaymentInfoSummaryCallback());
                }
            }
        });
    }

    private Callback fetchPaymentInfoSummaryCallback(){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseReponse[] reiseResponses = new ReiseReponse[0];
                try {
                    reiseResponses = new Gson().fromJson(response.body().string(), ReiseReponse[].class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(response.isSuccessful()) {
                    currentUserData.setCurrentReiseReponses(reiseResponses);
                    currentUserData.notifyObservers();
                    }
                }
            };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.currentUserData.deleteObservers();
        //MainActivity.currentUserData = null;
    }
}
