package com.example.roadsplit;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TabLayoutStrategy implements TabLayoutMediator.TabConfigurationStrategy {


    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        switch (position){
            case 0: tab.setText("Tut 1");
                break;
            case 1: tab.setText("Tut 2");
                break;
            case 2: tab.setText("Tut 3");
                break;
        }
    }
}
