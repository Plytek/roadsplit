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
import com.example.roadsplit.helperclasses.DashboardSetup;
import com.example.roadsplit.helperclasses.ZwischenstopAdapterHelper;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DashboardOverviewAdapter extends PagerAdapter {



    private Context context;
    private SharedPreferences reportPref;
    private SharedPreferences reisePref;
    private AusgabenReport ausgabenReport;


    public DashboardOverviewAdapter(Context mContext, AusgabenReport ausgabenReport) {
        this.context = mContext;
        this.ausgabenReport = ausgabenReport;
        this.reportPref = context.getSharedPreferences("report", MODE_PRIVATE);
        this.reisePref = context.getSharedPreferences("reise", MODE_PRIVATE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("report")) {
                    DashboardOverviewAdapter.this.ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);
                }
            }
        });

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layoutScreen;
        switch(position)
        {
            case 0:
                layoutScreen = inflater.inflate(R.layout.ausgabendashboard,null);
                DashboardSetup dashboardSetup = new DashboardSetup(layoutScreen, context);
                dashboardSetup.setUpDashboard();
                break;
            case 1:
                layoutScreen = inflater.inflate(R.layout.zwischenstopp,null);
                ZwischenstopAdapterHelper zwischenstopAdapterHelper = new ZwischenstopAdapterHelper(layoutScreen, context);
                zwischenstopAdapterHelper.setUpZwischenStops();
                break;
            case 2:
                layoutScreen = inflater.inflate(R.layout.packlistepage,null);
                break;
            case 3:
                layoutScreen = inflater.inflate(R.layout.dokumentepage,null);
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

    public Callback ausgabenReportCallback(){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                ausgabenReport = gson.fromJson(response.body().string(), AusgabenReport.class);
                if(response.isSuccessful()) {
                    SharedPreferences.Editor editor = reportPref.edit();
                    editor.putString("report", gson.toJson(ausgabenReport));
                    editor.apply();

                    editor = reisePref.edit();
                    editor.putString("reise", gson.toJson(ausgabenReport.getReise()));
                    editor.apply();


                    Looper.prepare();
                    Toast.makeText(context, "report fetch", Toast.LENGTH_SHORT).show();
                }
            }

        };
    }
}
