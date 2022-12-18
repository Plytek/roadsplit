package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.roadsplit.OnSwipeTouchListener;
import com.example.roadsplit.R;

public class TutActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tut_two);
        ConstraintLayout constraintLayout = findViewById(R.id.cLayoutTwo);
        constraintLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft(){
                next(null);
            }
            @Override
            public void onSwipeRight(){
                onSwipeRightSwipe();
            }
        });
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void next(View view) {
        Intent intent = new Intent(this, TutActivityThree.class);
        startActivity(intent);
    }
    public void onSwipeRightSwipe() {
        Intent intent = new Intent(this, TutActivityOne.class);
        startActivity(intent);
    }
}