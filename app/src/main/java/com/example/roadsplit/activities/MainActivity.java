package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.roadsplit.OnSwipeTouchListener;
import com.example.roadsplit.R;
import com.example.roadsplit.api.UserService;
import com.example.roadsplit.model.Reisender;
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
    public static Reisender currentUser;
    public static final String BASEURL = "https://416a-88-70-249-101.ngrok.io";


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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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
        intent.putExtra("user", (new Gson()).toJson(currentUser));
        //Startet die Activity
        startActivity(intent);
    }

    public void tutorial(View view) {
        Intent intent = new Intent(this, TutActivityOne.class);
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
        intent.putExtra("user", (new Gson()).toJson(currentUser));
        startActivity(intent);
    }


}
