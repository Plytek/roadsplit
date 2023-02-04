package com.example.roadsplit.helperclasses;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.roadsplit.R;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.adapter.StopAdapter;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.reponses.ReiseResponse;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

public class ZwischenstopAdapterHelper {

    private Context mContext;
    private View layoutScreen;
    private List<Stop> stops;
    private Reise reise;
    private TextView stopEntfernenErrorView;
    private ListView stopListView;
    private ArrayList<HashMap<String,String>> fullstops = new ArrayList<HashMap<String,String>>();

    private SharedPreferences reportPref;
    private AusgabenReport ausgabenReport;

    public ZwischenstopAdapterHelper(View layoutScreen, Context context) {


        this.reportPref = context.getSharedPreferences("report", MODE_PRIVATE);
        this.ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);
        this.reise = ausgabenReport.getReise();
        this.stops = reise.getStops();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("report")) {
                    ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);
                    reise = ausgabenReport.getReise();
                    stops = reise.getStops();
                    setUpZwischenStops();
                }
                // code to handle change in value for the key
            }
        });

        this.mContext = context;
        this.layoutScreen = layoutScreen;
    }

    public void setUpZwischenStops(){
        Button addStopButton = layoutScreen.findViewById(R.id.plusButton2);
        Button mapButton = layoutScreen.findViewById(R.id.mapAnzeigenButton);
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

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapAnzeigen();
            }
        });
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


    private void mapAnzeigen(){

        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.maplayout);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopUpAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}
