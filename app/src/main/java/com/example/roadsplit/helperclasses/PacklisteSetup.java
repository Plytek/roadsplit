package com.example.roadsplit.helperclasses;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.adapter.PacklistenRecyclerAdapter;
import com.example.roadsplit.model.PacklistenItem;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.requests.UpdateRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PacklisteSetup {
    private View layoutScreen;

    private Context context;
    private AusgabenActivity ausgabenActivity;

    private SharedPreferences reisenderPref;
    private SharedPreferences reportPref;
    private SharedPreferences reisePref;

    private RecyclerView packlisteRecycler;
    private EditText packlistenItem;
    private Button addButton;
    private Button removeButton;
    private Button saveChanges;

    private AusgabenReport ausgabenReport;
    private Reisender reisender;
    private Reise reise;
    private List<PacklistenItem> packliste;

    public PacklisteSetup(Context context, AusgabenActivity ausgabenActivity, View layoutScreen) {
        this.layoutScreen = layoutScreen;
        this.context = context;
        this.ausgabenActivity = ausgabenActivity;

        this.reisenderPref = context.getSharedPreferences("reisender", MODE_PRIVATE);
        this.reportPref = context.getSharedPreferences("report", MODE_PRIVATE);
        this.reisePref = context.getSharedPreferences("reise", MODE_PRIVATE);
        this.ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);
        this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
        this.reise = ausgabenReport.getReise();
        this.packliste = reise.getPackliste();

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
            packlistenRecyclerAdapter.notifyItemChanged(packliste.size() - 1);
        });

        saveChanges.setOnClickListener(view -> {
            reise.setPackliste(packlistenRecyclerAdapter.getRecentsDataList());
            UpdateRequest updateRequest = new UpdateRequest(reisender, reise);
            EndpointConnector.updateReise(updateRequest, updateReiseCallback(), context);
        });

    }

    private Callback updateReiseCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                ausgabenReport = gson.fromJson(response.body().string(), AusgabenReport.class);
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = reportPref.edit();
                    editor.putString("report", gson.toJson(ausgabenReport));
                    editor.apply();

                    editor = reisePref.edit();
                    editor.putString("reise", gson.toJson(ausgabenReport.getReise()));
                    editor.apply();


                    ausgabenActivity.runOnUiThread(() ->
                    {
                        Intent intent = new Intent(context, AusgabenActivity.class);
                        intent.putExtra("reise", new Gson().toJson(ausgabenReport.getReise()));
                        intent.putExtra("returning", "2");
                        ausgabenActivity.startActivity(intent);
                        ausgabenActivity.finish();
                    });
                } else if (response.code() == 403) {
                    EndpointConnector.toLogin(ausgabenActivity);
                }
            }
        };
    }
}
