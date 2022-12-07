package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.roadsplit.R;
import com.example.roadsplit.api.UserService;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void fetch(View view) {
        UserService userService = new UserService(this);
        EditText v = findViewById(R.id.uniqueNameField);
        String input = String.valueOf(v.getText());
        if(!input.isEmpty()) userService.fetchByUnique(input);
    }
}
