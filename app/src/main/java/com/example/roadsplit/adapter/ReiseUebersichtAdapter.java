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
    private Context mContext;
    private AusgabenActivity ausgabenActivity;
    private List<View> views;
    private AusgabeDetailAdapterHelper ausgabeDetailAdapterHelper;
    private ZwischenstopAdapterHelper zwischenstopAdapterHelper;
    private AusgabenAdapterHelper ausgabenAdapterHelper;

    public ReiseUebersichtAdapter(Context mContext, AusgabenActivity ausgabenActivity, List<View> views, ReiseResponse reiseResponse) {
        this.mContext = mContext;
        this.views = views;
        this.ausgabenActivity = ausgabenActivity;
        this.reiseResponse = reiseResponse;
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
                EndpointConnector.fetchPaymentInfo(reiseResponse.getReise(), updateReiseCallback());
                ausgabenAdapterHelper = new AusgabenAdapterHelper(mContext, layoutScreen, MainActivity.currentUserData.getCurrentReiseResponse(), ausgabenActivity, this);
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
/*                layoutScreen = inflater.inflate(R.layout.ausgabendetailpage,null);
                ausgabeDetailAdapterHelper = new AusgabeDetailAdapterHelper(layoutScreen, mContext, MainActivity.currentUserData.getCurrentReiseResponse());
                ausgabeDetailAdapterHelper.setUpAusgabeDetailPage();*/
                EndpointConnector.fetchPaymentInfo(reiseResponse.getReise(), updateReiseCallback());
                layoutScreen = inflater.inflate(R.layout.ausgabendashboard,null);
                ausgabenAdapterHelper = new AusgabenAdapterHelper(mContext, layoutScreen, MainActivity.currentUserData.getCurrentReiseResponse(), ausgabenActivity, this);
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
                ReiseResponse reponse = new Gson().fromJson(response.body().string(), ReiseResponse.class);
                if(response.isSuccessful()) {
                    MainActivity.currentUserData.setCurrentUser(reponse.getReisender());
                    MainActivity.currentUserData.setCurrentReiseResponse(reiseResponse);
                    MainActivity.currentUserData.notifyObservers();
                    reiseResponse = reponse;
                    Looper.prepare();
                    Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                }
            }

        };
    }


}
