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
import com.example.roadsplit.helperclasses.AppSettings;

public class successCreateReiseActivity extends AppCompatActivity {

    String reiseid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_create_reise);

        Intent intent = getIntent();
        reiseid = intent.getStringExtra("id");
        ((TextView)findViewById(R.id.pinTextViewSuccess)).setText(reiseid);
        AppSettings.buttonPressDownEffect(findViewById(R.id.sharedUebersichtButton));
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
        //TODO: zur aktuellen Reise nicht main
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}