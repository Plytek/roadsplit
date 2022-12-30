package com.example.roadsplit.activities.testing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.roadsplit.R;

public class ReiseSuccessDummy extends AppCompatActivity {

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reise_success_dummy);
        Intent intent = getIntent();
        this.id = intent.getStringExtra("id");
        ((TextView)findViewById(R.id.codeTextView)).setText(id);
    }

    public void copyToClipboard(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("test",
                ((EditText)findViewById(R.id.reiseNameText)).getText().toString());
        clipboard.setPrimaryClip(clip);
    }

    public void share(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, tritt meiner Reise auf RoadSplit mit dem Code " + id + " bei!");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}