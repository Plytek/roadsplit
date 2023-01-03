package com.example.roadsplit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.activities.MainScreenReisenActivity;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Stop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class ReiseUebersichtAdapter extends PagerAdapter {

    private final Reise reise;
    private List<Stop> stops;
    Context mContext;
    MainScreenReisenActivity mainScreenReisenActivity;
    List<View> views;

    public ReiseUebersichtAdapter(Context mContext, MainScreenReisenActivity mainScreenReisenActivity, List<View> views, Reise reise) {
        this.mContext = mContext;
        this.views = views;
        this.mainScreenReisenActivity = mainScreenReisenActivity;
        this.reise = reise;
        this.stops = reise.getStops();
        //mainScreenReisenActivity.findViewById()
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layoutScreen;

        switch(position)
        {
            case 2:
                layoutScreen = inflater.inflate(R.layout.dokumentepage,null);
                ((TextView)mainScreenReisenActivity.findViewById(R.id.textView6)).setTextColor(Color.RED);
                break;
            case 1:
                layoutScreen = inflater.inflate(R.layout.packlistepage,null);
                break;
            case 0:
                layoutScreen = inflater.inflate(R.layout.zwischenstopp,null);
                setUpZwischenStops(layoutScreen);
                break;
            case 3:
                layoutScreen = inflater.inflate(R.layout.ausgabenpage,null);
                break;
            default:
                layoutScreen = inflater.inflate(R.layout.dokumentepage,null);
                break;
        }

        container.addView(layoutScreen);

        return layoutScreen;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View)object);

    }

    public void setUpZwischenStops(View layoutScreen){
        Button addStopButton = layoutScreen.findViewById(R.id.plusButton2);
        Button removeStopButton = layoutScreen.findViewById(R.id.minusButton2);
        Button addBudgetButton = layoutScreen.findViewById(R.id.plusButton3);

        ListView stopListView = layoutScreen.findViewById(R.id.stopNamenListView);
        ListView budgetListView = layoutScreen.findViewById(R.id.BudgetListView);
        ListView gesamtAusgabenListView = layoutScreen.findViewById(R.id.AusgabenListView);

        List<String> stopNames = new ArrayList<>();
        List<String> budgetStrings = new ArrayList<>();
        List<String> gesamtAusgabenString = new ArrayList<>();

        for(Stop stop : stops)
        {
            stopNames.add(stop.getName());
            //TODO: budget immer setzen
            if(stop.getBudget() == null) budgetStrings.add("0");
            else budgetStrings.add(stop.getBudget().toString());
            if(stop.getGesamtausgaben() == null)gesamtAusgabenString.add("0");
            else gesamtAusgabenString.add(stop.getGesamtausgaben().toString());
        }

        stopListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<String> stopAdapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_list_item_multiple_choice, stopNames);
        stopListView.setAdapter(stopAdapter);
        ArrayAdapter<String> budgetAdapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_list_item_1, budgetStrings);
        budgetListView.setAdapter(budgetAdapter);
        ArrayAdapter<String> gesamtausgabenAdapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_list_item_1, gesamtAusgabenString);
        gesamtAusgabenListView.setAdapter(gesamtausgabenAdapter);

        addStopButton.setOnClickListener(view -> {
            String stopName = ((EditText)layoutScreen.findViewById(R.id.pinTextView2)).getText().toString();
            String budget = ((EditText)layoutScreen.findViewById(R.id.pinTextView3)).getText().toString();
            if(stopName.isEmpty()) return;

            Stop stop = new Stop();
            stop.setName(stopName);
            if(budget.isEmpty()) stop.setBudget(new BigDecimal(0));
            else stop.setBudget(new BigDecimal(budget));
            stop.setGesamtausgaben(new BigDecimal(0));

            stops.add(stop);
            stopNames.add(stop.getName());
            budgetStrings.add(stop.getBudget().toString());
            gesamtAusgabenString.add(stop.getGesamtausgaben().toString());
            reise.setStops(stops);
            stopAdapter.notifyDataSetChanged();
            budgetAdapter.notifyDataSetChanged();
            gesamtausgabenAdapter.notifyDataSetChanged();

        });

        removeStopButton.setOnClickListener(view -> {
            SparseBooleanArray checked = stopListView.getCheckedItemPositions();
            int removedcounter = 0;
            for (int i = 0; i < stopListView.getCount(); i++)
                if (checked.get(i)) {
                    stops.remove(i-removedcounter);
                    stopNames.remove(i-removedcounter);
                    budgetStrings.remove(i-removedcounter);
                    gesamtAusgabenString.remove(i-removedcounter);
                    removedcounter++;
                }
            reise.setStops(stops);
            stopAdapter.notifyDataSetChanged();
            budgetAdapter.notifyDataSetChanged();
            gesamtausgabenAdapter.notifyDataSetChanged();
        });

        addBudgetButton.setOnClickListener(view -> {
            String budget = ((EditText)layoutScreen.findViewById(R.id.pinTextView3)).getText().toString();
            //Nur "*d.dd und *d,dd*"
            String regex = "^\\d+([.,]\\d{2})?$";
            if(budget.isEmpty()) return;
            if(!budget.matches(regex))
            {
                //TODO: Fehlerbehandlung UI
                return;
            }
            budget = budget.replaceAll(",", ".");
            SparseBooleanArray checked = stopListView.getCheckedItemPositions();
            for (int i = 0; i < stopListView.getCount(); i++)
                if (checked.get(i)) {
                    stops.get(i).setBudget(new BigDecimal(budget));
                    budgetStrings.set(i, budget);
                }
            reise.setStops(stops);
            budgetAdapter.notifyDataSetChanged();

        });
    }
}
