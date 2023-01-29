package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roadsplit.R;
import com.example.roadsplit.helperclasses.ButtonEffect;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.reponses.ReiseResponse;
import com.google.gson.Gson;

public class ReiseSuccessActivity extends AppCompatActivity {

    String reiseid;
    Reise reise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_create_reise);

        Intent intent = getIntent();
        String reiseString = intent.getStringExtra("reiseResponse");
        ReiseResponse reiseResponse = new Gson().fromJson(reiseString, ReiseResponse.class);
        reise = reiseResponse.getReise();
        reiseid = intent.getStringExtra("id");
        ((TextView)findViewById(R.id.pinTextViewSuccess)).setText(reiseid);
        ButtonEffect.buttonPressDownEffect(findViewById(R.id.sharedUebersichtButton));
    }

    public void copyToClipboard(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("id", reiseid);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_LONG).show();
    }

    public void share(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, tritt meiner Reise auf RoadSplit mit dem Code " + reiseid + " bei!");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void zurReise(View view){
        Intent intent = new Intent(this, AusgabenActivity.class);
        String reiseString = new Gson().toJson(reise);
        intent.putExtra("reise", reiseString);
        startActivity(intent);
        finish();
    }

    public void reiseübersichtAnzeigen(View view){
        Intent intent = new Intent(this,ReiseUebersichtActivity.class);
        startActivity(intent);
        finish();
    }
}