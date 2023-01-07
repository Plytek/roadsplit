package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.roadsplit.R;
import com.example.roadsplit.model.Reise;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ReiseUebersichtActivity extends AppCompatActivity {
    private List<Reise> reisen;
    private List<String> reiseNames;
    private Reise selectedReise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reise_uebersicht);
        reisen = new ArrayList<>();
        reiseNames = new ArrayList<>();
        final int[] clickedPosition = {-1}; // to store the position of the clicked item
        reisen = MainActivity.currentUser.getReisen();
        for(Reise reise : reisen)
        {
            reiseNames.add(reise.getName());
        }

        ListView reisenView = findViewById(R.id.reisenUebersichtListView);
        ArrayAdapter<String> reisenAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reiseNames);
        reisenView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        reisenView.setAdapter(reisenAdapter);
        reisenView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    clickedPosition[0] = i;
                    selectedReise = reisen.get(i);
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(clickedPosition[0] != -1)
                {
                    goToReise(selectedReise);
                }
                return true;
            }
        });

        reisenView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

    }

    private void goToReise(Reise reise){
        Intent intent = new Intent(this, MainScreenReisenActivity.class);
        String reiseString = new Gson().toJson(reise);
        intent.putExtra("reise", reiseString);
        startActivity(intent);
    }
}