package com.example.roadsplit.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roadsplit.R;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.reponses.ReiseResponse;
import com.example.roadsplit.utility.ButtonEffect;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ReiseSuccessActivity extends AppCompatActivity {

    public static Map<String, Bitmap> imageMap = new HashMap<>();
    private Reisender reisender;
    private String reiseid;
    private Reise reise;
    private SharedPreferences reisenderPref;
    private SharedPreferences reisePref;
    private SharedPreferences uebersichtPref;
    private int imageloadCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_create_reise);

        this.reisenderPref = getSharedPreferences("reisender", MODE_PRIVATE);
        this.reisePref = getSharedPreferences("reise", MODE_PRIVATE);
        this.uebersichtPref = getSharedPreferences("uebersicht", MODE_PRIVATE);

        this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);

        Intent intent = getIntent();
        String reiseString = intent.getStringExtra("reiseResponse");
        ReiseResponse reiseResponse = new Gson().fromJson(reiseString, ReiseResponse.class);
        reise = reiseResponse.getReise();
        reiseid = intent.getStringExtra("id");
        ((TextView) findViewById(R.id.pinTextViewSuccess)).setText(reiseid);
        ButtonEffect.buttonPressDownEffect(findViewById(R.id.sharedUebersichtButton));
    }

    public void copyToClipboard(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("id", reiseid);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_LONG).show();
    }

    public void share(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, tritt meiner Reise auf RoadSplit mit dem Code " + reiseid + " bei!");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void zurReise(View view) {
        Intent intent = new Intent(this, AusgabenActivity.class);
        String reiseString = new Gson().toJson(reise);
        intent.putExtra("reise", reiseString);
        startActivity(intent);
        finish();
    }

    public void reiseübersichtAnzeigen(View view) {
        Intent intent = new Intent(this, ReiseUebersichtActivity.class);
        intent.putExtra("from", "success");
        startActivityForResult(intent, 1);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
    }

}