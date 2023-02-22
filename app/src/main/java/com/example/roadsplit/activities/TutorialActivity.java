package com.example.roadsplit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.roadsplit.R;
import com.example.roadsplit.adapter.IntroViewPagerAdapter;
import com.example.roadsplit.model.ScreenItem;
import com.example.roadsplit.utility.ButtonEffect;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager screenPager;
    private IntroViewPagerAdapter introViewPagerAdapter;
    private TabLayout tabIndicator;
    private Button btnNext;
    private int position = 0;
    private Button btnGetStarted;
    private Animation btnAnim;
    private TextView tvSkip;
    private Boolean visible = false;
    private List<ScreenItem> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_tut);
        // ini views
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);
        tvSkip = findViewById(R.id.tv_skip);

        ButtonEffect.buttonPressDownEffect(btnNext);
        ButtonEffect.buttonPressDownEffect(btnGetStarted);

        // fill list screen
        this.mList = new ArrayList<>();
        mList.add(new ScreenItem("Plane deine Reise", "Du musst kein Planungsgenie sein, wir helfen dir bei dem Planen deiner Reise und du kümmerst dich um das Genießen deiner Reise.", R.drawable.img1));
        mList.add(new ScreenItem("Rechnungen teilen", "Genieße die Reise mit deinen Freunden oder deiner Familie und verbringe nicht die Zeit damit Rechnungen kompliziert aufzuteilen.", R.drawable.img2));
        mList.add(new ScreenItem("Gespeicherte Reisen", "Wir bewahren deine Reisen auf und damit auch deine Erinnerungen. Sieh dir an was du wo und wann ausgegeben hast auf deinen Reisen.", R.drawable.img3));

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tablayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // tablayout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size() - 1) {
                    loadLastScreen();
                } else if (visible == true) {
                    btnNext.setVisibility(View.VISIBLE);
                    btnGetStarted.setVisibility(View.INVISIBLE);
                    tvSkip.setVisibility(View.VISIBLE);
                    tabIndicator.setVisibility(View.VISIBLE);
                    visible = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }

    public void nextTut(View view) {
        position = screenPager.getCurrentItem();
        if (position < mList.size()) {
            position++;
            screenPager.setCurrentItem(position);
        }
        if (position == mList.size() - 1) {
            loadLastScreen();
        }
    }

    public void getStarted(View view) {
        Intent mainActivity = new Intent(getApplicationContext(), ReiseUebersichtActivity.class);
        startActivity(mainActivity);
        finish();
    }

    public void skip(View view) {
        screenPager.setCurrentItem(mList.size());
    }

    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        btnGetStarted.setAnimation(btnAnim);
        visible = true;
    }
}