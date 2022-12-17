package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.roadsplit.R;
import com.example.roadsplit.api.UserService;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.model.UserAccount;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainActivity extends AppCompatActivity {

    private UserAccount userAccount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void fetch(View view) {
        UserService userService = new UserService(this);
        EditText v = findViewById(R.id.uniqueNameField);
        String input = String.valueOf(v.getText());
        if(!input.isEmpty()) userService.fetchByUnique(input);
    }

    public void nextActivity(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("user", (new Gson()).toJson(userAccount));
        startActivity(intent);
    }

    public void test(View view) {
        Button button = (Button) view;
        button.setText("Bent");
        TextView eins = findViewById(R.id.usernameText);
        eins.setText("1");
       //test


    }

}
