package com.example.roadsplit.adapter;

import android.content.Context;
import android.os.Looper;
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
import com.example.roadsplit.reponses.ReiseReponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ReiseUebersichtAdapter extends PagerAdapter {

    private ReiseReponse reiseReponse;
    private Context mContext;
    private AusgabenActivity ausgabenActivity;
    private List<View> views;
    private AusgabeDetailAdapterHelper ausgabeDetailAdapterHelper;
    private ZwischenstopAdapterHelper zwischenstopAdapterHelper;
    private AusgabenAdapterHelper ausgabenAdapterHelper;

    public ReiseUebersichtAdapter(Context mContext, AusgabenActivity ausgabenActivity, List<View> views, ReiseReponse reiseReponse) {
        this.mContext = mContext;
        this.views = views;
        this.ausgabenActivity = ausgabenActivity;
        this.reiseReponse = reiseReponse;
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
                //updateReise(reiseReponse.getReise());
                EndpointConnector.fetchPaymentInfo(reiseReponse.getReise(), updateReiseCallback());
                ausgabenAdapterHelper = new AusgabenAdapterHelper(mContext, layoutScreen, MainActivity.currentUserData.getCurrentReiseResponse(), ausgabenActivity);
                ausgabenAdapterHelper.setUpAusgaben();
                break;
            case 1:
                layoutScreen = inflater.inflate(R.layout.zwischenstopp,null);
                zwischenstopAdapterHelper = new ZwischenstopAdapterHelper(layoutScreen, mContext, MainActivity.currentUserData.getCurrentReiseResponse());
                zwischenstopAdapterHelper.setUpZwischenStops();
                break;
            case 2:
                layoutScreen = inflater.inflate(R.layout.packlistepage,null);
                break;
            case 3:
                layoutScreen = inflater.inflate(R.layout.ausgabendetailpage,null);
                ausgabeDetailAdapterHelper = new AusgabeDetailAdapterHelper(layoutScreen, mContext, MainActivity.currentUserData.getCurrentReiseResponse());
                ausgabeDetailAdapterHelper.setUpAusgabeDetailPage();
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
                ReiseReponse reponse = new Gson().fromJson(response.body().string(), ReiseReponse.class);
                if(response.isSuccessful()) {
                    MainActivity.currentUserData.setCurrentUser(reponse.getReisender());
                    MainActivity.currentUserData.setCurrentReiseResponse(reiseReponse);
                    MainActivity.currentUserData.notifyObservers();
                    reiseReponse = reponse;
                    Looper.prepare();
                    Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                }
            }

        };
    }

    public void removeListeners(){
        MainActivity.currentUserData.deleteObserver(zwischenstopAdapterHelper);
        MainActivity.currentUserData.deleteObserver(ausgabenAdapterHelper);
        MainActivity.currentUserData.deleteObserver(ausgabeDetailAdapterHelper);
    }



}
