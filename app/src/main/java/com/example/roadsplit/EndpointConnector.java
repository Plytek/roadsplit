package com.example.roadsplit;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.example.roadsplit.activities.MainActivity;
import com.example.roadsplit.model.Dokument;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.requests.AusgabenRequest;
import com.example.roadsplit.requests.JoinRequest;
import com.example.roadsplit.requests.UploadRequest;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    public static void fetchPaymentInfo(Reise reise, Reisender reisender, Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/ausgaben";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        AusgabenRequest ausgabenRequest = new AusgabenRequest();
        if(reise == null) return;

        ausgabenRequest.setReise(reise);
        ausgabenRequest.setReisender(reisender);

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(ausgabenRequest));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void fetchPaymentInfoSummary(Reise reise, Reisender reisender, Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/ausgabenSummary";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        AusgabenRequest ausgabenRequest = new AusgabenRequest();
        if(reise == null) return;

        ausgabenRequest.setReise(reise);
        ausgabenRequest.setReisender(reisender);

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(ausgabenRequest));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void fetchAusgabenReport(Reise reise, Reisender reisender, Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/ausgabenReport";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        AusgabenRequest ausgabenRequest = new AusgabenRequest();
        if(reise == null) return;

        ausgabenRequest.setReise(reise);
        ausgabenRequest.setReisender(reisender);

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

    public static void updateAusgaben(AusgabenRequest ausgabenRequest, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/ausgabe";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(ausgabenRequest));

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
    public static void uploadFile(byte[] byteImage, String filename, String filetype, Reise reise, Callback callback){
        String byteString = Base64.encodeToString(byteImage, Base64.NO_WRAP);
        Dokument dokument = new Dokument(byteString, filetype, filename);
        UploadRequest uploadRequest = new UploadRequest(dokument, reise);
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/reisedaten/uploadFile";

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(uploadRequest));
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);

    }


}
