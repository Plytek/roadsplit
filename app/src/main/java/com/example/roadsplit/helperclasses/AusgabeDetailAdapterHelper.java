package com.example.roadsplit.helperclasses;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.roadsplit.R;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.reponses.ReiseReponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class AusgabeDetailAdapterHelper implements Observer {

    private List<String> reisendeNames;
    private Context mContext;
    private View layoutScreen;
    private ReiseReponse reiseReponse;
    private List<String> ausgabeDetails;

    public AusgabeDetailAdapterHelper(View layoutScreen, Context context, ReiseReponse reiseReponse) {
        this.reiseReponse = reiseReponse;
        this.layoutScreen = layoutScreen;
        this.mContext = context;
        ausgabeDetails = new ArrayList<>();
        if(reiseReponse.getAusgabenRecord() != null)
            ausgabeDetails.addAll(reiseReponse.getAusgabenRecord());
    }

    public void setUpAusgabeDetailPage()
    {
        Spinner filterSpinner = layoutScreen.findViewById(R.id.ausgabeFilterSpinner);
        ListView detailAusgaben = layoutScreen.findViewById(R.id.ausgabeDetailListView);

        reisendeNames = new ArrayList<>();
        for(Reisender reisender : reiseReponse.getReisendeList()) reisendeNames.add(reisender.getNickname());

        ArrayAdapter<String> reisendeAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, reisendeNames);
        reisendeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(reisendeAdapter);


        ArrayAdapter<String> detailAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, ausgabeDetails);
        detailAusgaben.setAdapter(detailAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ausgabeDetails.clear();
                for(String detail : reiseReponse.getAusgabenRecord())
                {
                    if(detail.contains(reiseReponse.getReisendeList().get(i).getNickname())) ausgabeDetails.add(detail);
                }
                detailAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
