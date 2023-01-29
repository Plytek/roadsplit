package com.example.roadsplit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roadsplit.R;
import com.example.roadsplit.helperclasses.ButtonEffect;
import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.reponses.ReiseResponse;
import com.example.roadsplit.reponses.UserResponse;
import com.example.roadsplit.requests.JoinRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NeueReiseActivity extends AppCompatActivity {

    private Dialog dialog;
    private SharedPreferences prefs;
    private Reisender reisender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neue_reise);

        this.prefs = getSharedPreferences("reisender", MODE_PRIVATE);
        this.reisender = new Gson().fromJson(prefs.getString("reisender", "fehler"), Reisender.class);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if(reisender != null)
        {
            String welcome = "WAS GEHT, " + reisender.getNickname().toUpperCase();
            ((TextView)findViewById(R.id.textView10)).setText(welcome);
        }


        ButtonEffect.buttonPressDownEffect(findViewById(R.id.reiseErstellenButton));
        ButtonEffect.buttonPressDownEffect(findViewById(R.id.reiseBeitretenButton));

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        finish();
                        break;
                    case R.id.navigation_dashboard:
                        break;
                    case R.id.navigation_notifications:
                        intent = new Intent(NeueReiseActivity.this, ReiseUebersichtTestActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    public void reiseErstellen(View view)
    {
        Intent intent = new Intent(this, ReiseErstellenActivity.class);
        startActivity(intent);
    }

    public void reiseBeitreten(View view)
    {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    public void join(View view) {
        EditText editText = dialog.findViewById(R.id.pinTextView);
        String uniquename = editText.getText().toString();
        JoinRequest joinRequest = new JoinRequest(reisender.getId(), uniquename);
        EndpointConnector.reiseBeitreten(joinRequest, reiseBeitretenCallback());

    }

    private Callback reiseBeitretenCallback(){
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
                if(response.isSuccessful()) {
                    UserResponse userResponse = new UserResponse("ok", reiseResponse.getReisender(), System.currentTimeMillis());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("reisender", gson.toJson(userResponse));
                    editor.apply();
                    Looper.prepare();
                    dialog.dismiss();
                    Toast.makeText(NeueReiseActivity.this, "Reise erfolgreich beigetreten", Toast.LENGTH_LONG).show();
                }
            }
        };
    }
}