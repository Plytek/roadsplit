package com.example.roadsplit.helperclasses;


import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.requests.AusgabenRequest;
import com.example.roadsplit.requests.JoinRequest;
import com.google.gson.Gson;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class EndpointConnector {

    public static void fetchImageFromWiki(Reise reise, Callback callback)
    {
        String reisename = reise.getStops().get(reise.getStops().size()-1).getName();
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.wikimedia.org/core/v1/wikipedia/en/search/page?q=" +
                reisename + "&limit=1";

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void fetchPaymentInfo(Reise reise, Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/ausgaben";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        AusgabenRequest ausgabenRequest = new AusgabenRequest();
        if(reise == null) return;

        ausgabenRequest.setReise(reise);
        ausgabenRequest.setReisender(MainActivity.currentUser);

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(ausgabenRequest));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void reiseBeitreten(JoinRequest joinRequest, Callback callback)
    {

        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/join";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(joinRequest));
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void saveReise(Reise reise, Callback callback) {

        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/reise";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        if (reise == null) return;
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(reise));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        //Erstellt in einem neuen Thread eine Http Anfrage an den Webservice, ruft bei Erfolg onReponse() auf, bei Misserfolg onFailure()
        client.newCall(request).enqueue(callback);
    }


    public static void updateReise(Reise reise, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/updatereise";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        if (reise == null) return;
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(reise));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void fetchLocationInfo(String location, Callback callback){

        OkHttpClient client = new OkHttpClient();
        String url = "https://eu1.locationiq.com/v1/autocomplete?key=pk.07b1dd6de2c45e289e1470fc2407c7e5&q="
                + location + "&format=json";

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

}
