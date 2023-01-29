package com.example.roadsplit;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.requests.AusgabenRequest;
import com.example.roadsplit.requests.JoinRequest;
import com.example.roadsplit.requests.UpdateRequest;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EndpointConnector {

    public static void updateReisenderInfo(long id, Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/userdaten/userUpdate";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();


        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(new UpdateRequest(id)));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void saveAusgabe(AusgabenRequest ausgabenRequest, Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/ausgaben";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(ausgabenRequest));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }


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
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    public static void uploadFile(Context context, Callback callback, Uri uri)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/uploadFile/";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        File file = new File(uri.getPath());
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
                .build();

        Request request = new Request.Builder().url(httpBuilder.build())
                .post(requestBody).build();

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
