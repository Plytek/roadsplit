package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.roadsplit.OnSwipeTouchListener;
import com.example.roadsplit.R;

public class TutActivityThree extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tut_three);
        ConstraintLayout constraintLayout = findViewById(R.id.cLayoutThree);
        constraintLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft(){back(null);}
            @Override
            public void onSwipeRight(){
                swipeRight();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void next(View view) {
        Intent intent = new Intent(this, NeueReiseActivity.class);
        startActivity(intent);
    }

    public void swipeRight() {
        Intent intent = new Intent(this, TutActivityTwo.class);
        startActivity(intent);
    }
}