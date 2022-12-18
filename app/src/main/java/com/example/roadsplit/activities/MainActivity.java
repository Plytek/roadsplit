package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.widget.EditText;

import com.example.roadsplit.OnSwipeTouchListener;
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

        //Das hier auszukommentieren damit die App beim starten auf den Debugger wartet
        //Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setzt einen Listener, der bei einem Linksswipe zur nächsten Activity geht
        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        constraintLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this)
        {
            public void onSwipeLeft() {
                nextActivity(findViewById(R.id.nextButton));
            }
        });
    }

    //Versucht den Nutzer mit dem Uniquename das im Eingabefeld steht aus dem Backend zu holen
    public void fetch(View view) {
        //Neue Instanz des Userservice, übergibt sich für die Callbacks selbst als Activity
        UserService userService = new UserService(this);
        EditText v = findViewById(R.id.uniqueNameField);
        String input = String.valueOf(v.getText());
        //Wenn der Input nicht leer ist wird der UserService aufgerufen und ausgeführt
        if(!input.isEmpty()) userService.fetchByUnique(input);
    }

    //Startet einen neuen Intent (Activity) - die MapActivity
    public void nextActivity(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        //Gibt zusätzliche Daten mit durch .putExtra(Key, Value). In diesem Fall den aktuellen Useraccount als String
        intent.putExtra("user", (new Gson()).toJson(userAccount));
        //Startet die Activity
        startActivity(intent);
    }

}
