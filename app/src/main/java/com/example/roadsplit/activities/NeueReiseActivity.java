package com.example.roadsplit.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
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
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.reponses.ReiseResponse;
import com.example.roadsplit.requests.JoinRequest;
import com.example.roadsplit.utility.ButtonEffect;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NeueReiseActivity extends AppCompatActivity {


    private Dialog dialog;
    private Reisender reisender;
    private SharedPreferences reisenderPref;
    private SharedPreferences reiseResponsePref;
    private SharedPreferences reisePref;
    private SharedPreferences uebersichtPref;

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
                    Intent intent = new Intent(NeueReiseActivity.this, ReiseUebersichtActivity.class);
                    intent.putExtra("from", "success");
                    startActivity(intent);
                    finish();
                } else if (response.code() == 403) {
                    EndpointConnector.toLogin(NeueReiseActivity.this);
                }
            }
        };
    }


}