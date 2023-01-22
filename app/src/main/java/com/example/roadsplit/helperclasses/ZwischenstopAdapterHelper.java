package com.example.roadsplit.helperclasses;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.adapter.StopAdapter;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.reponses.ReiseReponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ZwischenstopAdapterHelper implements Observer {

    private Context mContext;
    private View layoutScreen;
    private ListView stopListView;
    private List<Stop> stops;
    private Reise reise;
    private TextView stopEntfernenErrorView;
    private ArrayList<HashMap<String,String>> fullstops = new ArrayList<HashMap<String,String>>();


    public ZwischenstopAdapterHelper(View layoutScreen, Context context, ReiseReponse reiseReponse) {
        MainActivity.currentUserData.addObserver(this);
        this.stops = MainActivity.currentUserData.getCurrentReiseResponse().getReise().getStops();
        this.reise = MainActivity.currentUserData.getCurrentReiseResponse().getReise();
        this.mContext = context;
        this.layoutScreen = layoutScreen;
    }

    public void setUpZwischenStops(){
        Button addStopButton = layoutScreen.findViewById(R.id.plusButton2);
        Button removeStopButton = layoutScreen.findViewById(R.id.minusButton2);
        Button addBudgetButton = layoutScreen.findViewById(R.id.plusButton3);
        stopEntfernenErrorView = layoutScreen.findViewById(R.id.stopEntfernenErrorView);
        stopEntfernenErrorView.setVisibility(View.INVISIBLE);

        stopListView = layoutScreen.findViewById(R.id.stopNamenListView);

        for(Stop stop : stops)
        {
            HashMap<String, String> fullstop = new HashMap<>();
            //TODO: budget immer setzen
            fullstop.put("stop", stop.getName());
            if(stop.getBudget() == null) fullstop.put("budget","0");
            else fullstop.put("budget", stop.getBudget().toString());
            if(stop.getGesamtausgaben() == null)fullstop.put("gesamt", "0");
            else fullstop.put("gesamt", stop.getGesamtausgaben().toString());
            fullstops.add(fullstop);
        }

        StopAdapter stopAdapter = new StopAdapter(stops, mContext);
        stopListView.setAdapter(stopAdapter);

        addStopButton.setOnClickListener(view -> {
            stopEntfernenErrorView.setVisibility(View.INVISIBLE);
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
            stopEntfernenErrorView.setVisibility(View.INVISIBLE);
            SparseBooleanArray checked = stopListView.getCheckedItemPositions();
            int removedcounter = 0;
            for (int i = 0; i < stopListView.getCount(); i++)
                if (checked.get(i)) {
                    if (!stops.get(i-removedcounter).getGesamtausgaben().equals(new BigDecimal(0))){
                        stopEntfernenErrorView.setVisibility(View.VISIBLE);
                        return;
                    }
                    stops.remove(i-removedcounter);
                    removedcounter++;
                }
            reise.setStops(stops);
            stopAdapter.notifyDataSetChanged();
        });

        addBudgetButton.setOnClickListener(view -> {
            stopEntfernenErrorView.setVisibility(View.INVISIBLE);
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


    @Override
    public void update(Observable observable, Object o) {
        this.stops = MainActivity.currentUserData.getCurrentReiseResponse().getReise().getStops();
        this.reise = MainActivity.currentUserData.getCurrentReiseResponse().getReise();
        //setUpZwischenStops();
    }
}
