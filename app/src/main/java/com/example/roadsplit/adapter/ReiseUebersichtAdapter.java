package com.example.roadsplit.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.helperclasses.AusgabeDetailAdapterHelper;
import com.example.roadsplit.helperclasses.AusgabenAdapterHelper;
import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.helperclasses.ZwischenstopAdapterHelper;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.reponses.ReiseResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import lombok.Getter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Getter
public class ReiseUebersichtAdapter extends PagerAdapter {

    private ReiseResponse reiseResponse;
    private Reisender reisender;

    private Context mContext;
    private AusgabenActivity ausgabenActivity;
    private List<View> views;
    private AusgabeDetailAdapterHelper ausgabeDetailAdapterHelper;
    private ZwischenstopAdapterHelper zwischenstopAdapterHelper;
    private AusgabenAdapterHelper ausgabenAdapterHelper;

    private SharedPreferences reisenderPref;
    private SharedPreferences reiseResponsePref;
    private SharedPreferences reisePref;

    public ReiseUebersichtAdapter(Context mContext, AusgabenActivity ausgabenActivity, List<View> views, ReiseResponse reiseResponseX) {
        this.mContext = mContext;
        this.views = views;
        this.ausgabenActivity = ausgabenActivity;
        this.reiseResponse = reiseResponseX;

        this.reisenderPref = mContext.getSharedPreferences("reisender", MODE_PRIVATE);
        this.reiseResponsePref = mContext.getSharedPreferences("reiseResponse", MODE_PRIVATE);
        this.reisePref = mContext.getSharedPreferences("reise", MODE_PRIVATE);

        this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
        //this.reiseResponse = new Gson().fromJson(reiseResponsePref.getString("reiseResponse", "fehler"), ReiseResponse.class);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("reisender")) {
                    reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
                } else if(key.equals("reiseResponse")) {
                    reiseResponse = new Gson().fromJson(reiseResponsePref.getString("reiseResponse", "fehler"), ReiseResponse.class);
                }
                // code to handle change in value for the key
            }
        });
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
                EndpointConnector.fetchPaymentInfo(reiseResponse.getReise(), reisender, updateReiseCallback());
                ausgabenAdapterHelper = new AusgabenAdapterHelper(mContext, layoutScreen, reiseResponse, ausgabenActivity, this);
                ausgabenAdapterHelper.setUpAusgaben();
                break;
            case 1:
                layoutScreen = inflater.inflate(R.layout.zwischenstopp,null);
                zwischenstopAdapterHelper = new ZwischenstopAdapterHelper(layoutScreen, mContext, reiseResponse);
                zwischenstopAdapterHelper.setUpZwischenStops();
                break;
            case 2:
                layoutScreen = inflater.inflate(R.layout.packlistepage,null);
                break;
            case 3:
/*                layoutScreen = inflater.inflate(R.layout.ausgabendetailpage,null);
                ausgabeDetailAdapterHelper = new AusgabeDetailAdapterHelper(layoutScreen, mContext, MainActivity.currentUserData.getCurrentReiseResponse());
                ausgabeDetailAdapterHelper.setUpAusgabeDetailPage();*/
                EndpointConnector.fetchPaymentInfo(reiseResponse.getReise(), reisender, updateReiseCallback());
                layoutScreen = inflater.inflate(R.layout.ausgabendashboard,null);
                ausgabenAdapterHelper = new AusgabenAdapterHelper(mContext, layoutScreen, reiseResponse, ausgabenActivity, this);
                ausgabenAdapterHelper.setUpDashboard();
                zwischenstopAdapterHelper.setUpZwischenStops();
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


    public Callback updateReiseCallback(){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                ReiseResponse currentReiseResponse = gson.fromJson(response.body().string(), ReiseResponse.class);
                if(response.isSuccessful()) {
                    SharedPreferences.Editor editor = reisenderPref.edit();
                    editor.putString("reisender", gson.toJson(currentReiseResponse.getReisender()));
                    editor.apply();

                    editor = reiseResponsePref.edit();
                    editor.putString("reiseResponse", gson.toJson(currentReiseResponse));
                    editor.apply();

                    editor = reisePref.edit();
                    editor.putString("reise", gson.toJson(currentReiseResponse.getReise()));
                    editor.apply();

                    reiseResponse = currentReiseResponse;
                    Looper.prepare();
                    Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                }
            }

        };
    }


}
