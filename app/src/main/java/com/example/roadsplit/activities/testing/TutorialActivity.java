package com.example.roadsplit.activities.testing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.roadsplit.adapter.IntroViewPagerAdapter;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.model.ScreenItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager screenPager;
    private IntroViewPagerAdapter introViewPagerAdapter;
    private TabLayout tabIndicator;
    private List<ScreenItem> mList;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        tabIndicator = findViewById(R.id.tab_indicator);
        screenPager = findViewById(R.id.screen_viewpager);

        // fill list screen
        mList = new ArrayList<>();
        mList.add(new ScreenItem("Plane deine Reise",
                "Lass dir deine vergangenen Reisen durch den Kopf gehen und schau dir an, was du damals alles unternommen und ausgegeben hast.",
                R.drawable.canvasone));
        mList.add(new ScreenItem("Rechnungen teilen",
                "Verbringe mehr Zeit mit deinen Freunden, anstatt mühsam Rechnungen und Ausgaben zu teilen." + "\n" + " Einfach eintragen und auswählen.",
                R.drawable.canvastwo));
        mList.add(new ScreenItem("Gespeicherte Reisen",
                "Lass dir deine vergangenen Reisen durch den Kopf gehen und schau dir an, was du damals alles unternommen und ausgegeben hast.",
                R.drawable.canvasthree));

        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);
        tabIndicator.setupWithViewPager(screenPager);

    }

    public void next(View view) {
        position = screenPager.getCurrentItem();
        if (position < mList.size()) {
            position++;
            screenPager.setCurrentItem(position);
        }
    }

    public void skip(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}