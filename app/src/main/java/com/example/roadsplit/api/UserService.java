package com.example.roadsplit.api;

import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.R;
import com.example.roadsplit.model.UserAccount;
import com.google.gson.Gson;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserService implements Callback{

    private final MainActivity mainActivity;

    public UserService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void fetchByUnique(String uniquename)
    {
        OkHttpClient client = new OkHttpClient();
        //String url = "http://6367-178-0-193-19.ngrok.io/api/userdaten/user";
        String url = "http://10.0.2.2:8080/api/userdaten/user";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder.addQueryParameter("uniquename",uniquename);

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();

        client.newCall(request).enqueue(this);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        e.printStackTrace();
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if(response.isSuccessful())
        {
            Gson gson = new Gson();
            UserAccount user = gson.fromJson(response.body().string(), UserAccount.class);

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String regex = "[\\[, \\],]";
                    String stops = "";
                    String reisen = "";
                    String nickname = "";
                    if(user != null && user.getReisen().get(0) != null) {
                        reisen = user.getReisen().toString();
                        reisen = reisen.replaceAll(regex, "");
                    }
                    if(user != null && !reisen.isEmpty() && user.getReisen().get(0).getStops() != null) {
                        stops = user.getReisen().get(0).getStops().toString();
                        stops = stops.replaceAll(regex, "");
                    }
                    if(user != null && user.getNickname() != null)
                        nickname = user.getNickname();

                    ((EditText) mainActivity.findViewById(R.id.stopsText)).setText(stops);
                    ((EditText) mainActivity.findViewById(R.id.reisenText)).setText(reisen);
                    ((EditText) mainActivity.findViewById(R.id.usernameText)).setText(nickname);
                }
            });
        }
    }
}