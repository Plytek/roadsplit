package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.roadsplit.R;

public class FurkanTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furkan_test);
    }

    public void clickButton(View button) {
        EditText editText = findViewById(R.id.cloudText);
        editText.setText("Hallo");
//        Button button1 = (Button) button;
        Button button1 = findViewById(R.id.heavenButton);
        button1.setText("Moin");
    }

    public void changeScreen(View clickable) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Servus", ((EditText) findViewById(R.id.cloudText)).getText().toString());
        startActivity(intent);
        finish();

    }
}