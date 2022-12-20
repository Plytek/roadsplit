package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.roadsplit.R;
import com.example.roadsplit.TabAdapter;
import com.example.roadsplit.TabLayoutStrategy;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TutorialActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        initViews();
    }

    protected void initViews(){
        tabLayout = findViewById(R.id.tuttab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        viewPager2.setAdapter(new TabAdapter(this));
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutStrategy());
        tabLayoutMediator.attach();

    }
}