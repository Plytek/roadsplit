package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.roadsplit.R;
import com.example.roadsplit.activities.testing.DummyPlanungActivity;
import com.example.roadsplit.activities.testing.FurkanTestActivity;
import com.example.roadsplit.activities.testing.MapActivity;
import com.example.roadsplit.activities.testing.ReiseErstellenTestActivity;
import com.example.roadsplit.activities.testing.TutorialActivity;
import com.example.roadsplit.activities.testing.UserCreateActivity;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.UserAccount;
import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainActivity extends AppCompatActivity {


    private UserAccount userAccount = null;
    public static Reisender currentUser;
    public static final String BASEURL = "http://167.172.167.221:8080";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Das hier auszukommentieren damit die App beim starten auf den Debugger wartet
        //Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    //Startet einen neuen Intent (Activity) - die MapActivity
    public void openMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        //Gibt zusätzliche Daten mit durch .putExtra(Key, Value). In diesem Fall den aktuellen Useraccount als String
        intent.putExtra("user", (new Gson()).toJson(currentUser));
        //Startet die Activity
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

    public void turorialAnim(View view){
        Intent intent = new Intent(this, TutActivity.class);
        startActivity(intent);
    }

    public void einloggen(View view){
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void reiseErstelen(View view){
        Intent intent = new Intent(this, ReiseErstellenTestActivity.class);
        startActivity(intent);
    }

    public void testBent(View view){
        Intent intent = new Intent(this, ReiseErstellenActivity.class);
        startActivity(intent);
    }

}
