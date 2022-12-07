package com.example.roadsplit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import com.example.roadsplit.model.UserAccount;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.Task;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MainActivity extends AppCompatActivity {

    private EditText stops;
    private EditText reisen;
    private EditText nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.stops = findViewById(R.id.stopsText);
        this.reisen = findViewById(R.id.reisenText);
        this.nickname = findViewById(R.id.usernameText);

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) Log.d("googleacc", "Schon eingeloggt");
        else Log.d("googleacc", "Noch nicht eingeloggt");
    }



    public void fetch(View view)
    {
        UserService userService = new UserService(this);
        EditText v = findViewById(R.id.uniqueNameField);

        String input = String.valueOf(v.getText());

        if(!input.isEmpty()) userService.fetchByUnique(input);
    }






}
