package com.example.roadsplit.helperclasses;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.R;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.adapter.PacklistenRecyclerAdapter;
import com.example.roadsplit.model.PacklistenItem;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class PacklisteSetup {
    private View layoutScreen;

    private Context context;
    private AusgabenActivity ausgabenActivity;

    private SharedPreferences reportPref;

    private RecyclerView packlisteRecycler;
    private EditText packlistenItem;
    private Button addButton;
    private Button removeButton;
    private Button saveChanges;

    private AusgabenReport ausgabenReport;
    private Reise reise;
    private List<PacklistenItem> packliste;

    public PacklisteSetup(View layoutScreen, Context context, AusgabenActivity ausgabenActivity) {
        this.layoutScreen = layoutScreen;
        this.context = context;
        this.ausgabenActivity = ausgabenActivity;
        this.reise = ausgabenReport.getReise();
        this.packliste = reise.getPackliste();

        this.reportPref = context.getSharedPreferences("report", MODE_PRIVATE);
        this.ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);

        this.packlisteRecycler = layoutScreen.findViewById(R.id.packlisteRecyclerView);
        this.packlistenItem = layoutScreen.findViewById(R.id.packlistenEditText);
        this.addButton = layoutScreen.findViewById(R.id.addPacklistenItemButton);
        this.removeButton = layoutScreen.findViewById(R.id.removePacklistenItemButton);
        this.saveChanges = layoutScreen.findViewById(R.id.savePacklisteChangesButton);

    }

    public void setUpPackliste() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        packlisteRecycler.setLayoutManager(layoutManager);
        PacklistenRecyclerAdapter packlistenRecyclerAdapter = new PacklistenRecyclerAdapter(context, ausgabenReport.getReise().getPackliste());
        packlisteRecycler.setAdapter(packlistenRecyclerAdapter);

        addButton.setOnClickListener(view -> {
            String itemName = packlistenItem.getText().toString();
            if (itemName.isEmpty()) return;
            PacklistenItem item = new PacklistenItem();
            item.setDone(false);
            item.setItemname(itemName);
            if (packliste == null) packliste = new ArrayList<>();
            packliste.add(item);
            reise.setPackliste(packliste);
        });


    }


}
