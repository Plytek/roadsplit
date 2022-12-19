
package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.roadsplit.OnSwipeTouchListener;
import com.example.roadsplit.R;

public class TutActivityOne extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tut_one);
        ConstraintLayout constraintLayout = findViewById(R.id.cLayout);
        constraintLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft(){
                next();
            }
        });
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void next() {
        Intent intent = new Intent(this, TutActivityTwo.class);
        startActivity(intent);
    }

    public void next(View view) {
        Intent intent = new Intent(this, TutActivityTwo.class);
        startActivity(intent);
    }
}