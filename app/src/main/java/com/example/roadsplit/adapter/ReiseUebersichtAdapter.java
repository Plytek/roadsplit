package com.example.roadsplit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.activities.MainScreenReisenActivity;
import com.example.roadsplit.model.Ausgabe;
import com.example.roadsplit.model.AusgabenTyp;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class ReiseUebersichtAdapter extends PagerAdapter {

    private final Reise reise;
    private List<Reisender> reisende;
    private List<Stop> stops;
    private ArrayList<HashMap<String,String>> fullstops = new ArrayList<HashMap<String,String>>();
    Context mContext;
    MainScreenReisenActivity mainScreenReisenActivity;
    List<View> views;

    public ReiseUebersichtAdapter(Context mContext, MainScreenReisenActivity mainScreenReisenActivity, List<View> views, Reise reise, List<Reisender> reisende) {
        this.mContext = mContext;
        this.views = views;
        this.mainScreenReisenActivity = mainScreenReisenActivity;
        this.reise = reise;
        this.stops = reise.getStops();
        this.reisende = reisende;
        //mainScreenReisenActivity.findViewById()
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layoutScreen;

        switch(position)
        {
            case 0:
                layoutScreen = inflater.inflate(R.layout.ausgabenpage,null);
                setUpAusgaben(layoutScreen);
                break;
            case 2:
                layoutScreen = inflater.inflate(R.layout.packlistepage,null);
                break;
            case 1:
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

        for(Stop stop : stops)
        {
            HashMap<String, String> fullstop = new HashMap<>();
            //stopNames.add(stop.getName());
            //TODO: budget immer setzen
            fullstop.put("stop", stop.getName());
/*            if(stop.getBudget() == null) budgetStrings.add("0");
            else budgetStrings.add(stop.getBudget().toString());*/
            if(stop.getBudget() == null) fullstop.put("budget","0");
            else fullstop.put("budget", stop.getBudget().toString());
            /*if(stop.getGesamtausgaben() == null)gesamtAusgabenString.add("0");
            else gesamtAusgabenString.add(stop.getGesamtausgaben().toString());*/
            if(stop.getGesamtausgaben() == null)fullstop.put("gesamt", "0");
            else fullstop.put("gesamt", stop.getGesamtausgaben().toString());
            fullstops.add(fullstop);
        }

        StopAdapter stopAdapter = new StopAdapter(stops, mContext);
        stopListView.setAdapter(stopAdapter);

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
            reise.setStops(stops);
            stopAdapter.notifyDataSetChanged();

        });

        removeStopButton.setOnClickListener(view -> {
            SparseBooleanArray checked = stopListView.getCheckedItemPositions();
            int removedcounter = 0;
            for (int i = 0; i < stopListView.getCount(); i++)
                if (checked.get(i)) {
                    stops.remove(i-removedcounter);
                    removedcounter++;
                }
            reise.setStops(stops);
            stopAdapter.notifyDataSetChanged();
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
                    //budgetStrings.set(i, budget);
                }
            reise.setStops(stops);
            stopAdapter.notifyDataSetChanged();
        });
    }

    public void setUpAusgaben(View layoutscreen){
        Spinner spinner = layoutscreen.findViewById(R.id.planets_spinner2);
        List<String> reisendeNames = new ArrayList<>();
        for(Reisender reisender : reisende) reisendeNames.add(reisender.getNickname());
        ArrayAdapter<String> reisendeAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, reisendeNames);
        reisendeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(reisendeAdapter);

        Spinner typen = layoutscreen.findViewById(R.id.planets_spinner4);
        List<String> names = new ArrayList<>();
        for (AusgabenTyp c : AusgabenTyp.values()) {
            names.add(c.name());
        }
        ArrayAdapter<String> typAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, names);
        typAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typen.setAdapter(typAdapter);

        Spinner stopNameSpinner = layoutscreen.findViewById(R.id.planets_spinner3);
        List<String> stopNames = new ArrayList<>();
        for(Stop stop : stops) stopNames.add(stop.getName());
        ArrayAdapter<String> stopAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, stopNames);
        stopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stopNameSpinner.setAdapter(stopAdapter);

    }
}
