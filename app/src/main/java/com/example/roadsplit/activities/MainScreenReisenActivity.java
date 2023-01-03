package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.roadsplit.R;
import com.example.roadsplit.adapter.ReiseUebersichtAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainScreenReisenActivity extends AppCompatActivity {
    private ViewPager screenPager;
    private ReiseUebersichtAdapter reiseUebersichtAdapter;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen_reisen);

        // setup viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        reiseUebersichtAdapter = new ReiseUebersichtAdapter(this, this, null, MainActivity.currentUser.getReisen().get(0));
        screenPager.setAdapter(reiseUebersichtAdapter);
        tabLayout = findViewById(R.id.tab_indicator2);
        tabLayout.setupWithViewPager(screenPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ausgabeiconselector);
        tabLayout.getTabAt(1).setIcon(R.drawable.minusbutton);
        tabLayout.getTabAt(2).setIcon(R.drawable.smallbuttondots);
        tabLayout.getTabAt(3).setIcon(R.drawable.minusbutton);
    }


}