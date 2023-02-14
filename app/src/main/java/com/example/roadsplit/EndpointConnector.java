package com.example.roadsplit;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roadsplit.activities.LoginActivity;
import com.example.roadsplit.model.Dokument;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.requests.AusgabenRequest;
import com.example.roadsplit.requests.DownloadRequest;
import com.example.roadsplit.requests.JoinRequest;
import com.example.roadsplit.requests.UpdateRequest;
import com.example.roadsplit.requests.UploadRequest;
import com.google.gson.Gson;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class EndpointConnector {

    public static String baseurl;


    public static void fetchImageFromWiki(Reise reise, Callback callback) {
        String reisename = reise.getStops().get(reise.getStops().size() - 1).getName();
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

    public static void fetchPaymentInfo(Reise reise, Reisender reisender, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = baseurl + "/api/reisedaten/ausgaben";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        AusgabenRequest ausgabenRequest = new AusgabenRequest();
        if (reise == null) return;

        ausgabenRequest.setReise(reise);
        ausgabenRequest.setReisender(reisender);

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(ausgabenRequest));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void fetchPaymentInfoSummary(Reise reise, Reisender reisender, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = baseurl + "/api/reisedaten/ausgabenSummary";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        AusgabenRequest ausgabenRequest = new AusgabenRequest();
        if (reise == null) return;

        ausgabenRequest.setReise(reise);
        ausgabenRequest.setReisender(reisender);

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(ausgabenRequest));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void fetchAusgabenReport(Reise reise, Reisender reisender, Callback callback, Context context) {
        OkHttpClient client = new OkHttpClient();
        String url = baseurl + "/api/reisedaten/ausgabenReport";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        String jwt = getJwtToken(context);

        AusgabenRequest ausgabenRequest = new AusgabenRequest();
        if (reise == null) return;

        ausgabenRequest.setReise(reise);
        ausgabenRequest.setReisender(reisender);

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(ausgabenRequest));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .header("Authorization", "Bearer " + jwt)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void reiseBeitreten(JoinRequest joinRequest, Callback callback, Context context) {

        OkHttpClient client = new OkHttpClient();
        String jwt = getJwtToken(context);
        String url = baseurl + "/api/reisedaten/join";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(joinRequest));
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .header("Authorization", "Bearer " + jwt)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void saveReise(Reise reise, Callback callback, Context context) {

        OkHttpClient client = new OkHttpClient();
        String url = baseurl + "/api/reisedaten/reise";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        String jwt = getJwtToken(context);

        if (reise == null) return;
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(reise));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .header("Authorization", "Bearer " + jwt)
                .post(formBody)
                .build();

        //Erstellt in einem neuen Thread eine Http Anfrage an den Webservice, ruft bei Erfolg onReponse() auf, bei Misserfolg onFailure()
        client.newCall(request).enqueue(callback);
    }


    public static void updateReise(UpdateRequest updateRequest, Callback callback, Context context) {
        OkHttpClient client = new OkHttpClient();
        String url = baseurl + "/api/reisedaten/updatereise";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        String jwt = getJwtToken(context);

        if (updateRequest.getReise() == null) return;
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(updateRequest));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .header("Authorization", "Bearer " + jwt)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void updateOverview(Reisender reisender, Callback callback, Context context) {
        OkHttpClient client = new OkHttpClient();
        String url = baseurl + "/api/userdaten/update";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        String jwt = getJwtToken(context);

        if (reisender == null) return;
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(reisender));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .header("Authorization", "Bearer " + jwt)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }


    public static void updateAusgaben(AusgabenRequest ausgabenRequest, Callback callback, Context context) {
        OkHttpClient client = new OkHttpClient();
        String url = baseurl + "/api/reisedaten/ausgabe";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        String jwt = getJwtToken(context);
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(ausgabenRequest));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .header("Authorization", "Bearer " + jwt)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void fetchLocationInfo(String location, Callback callback) {
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

    public static void uploadFile(byte[] byteImage, String filename, String filetype, Reise reise, Reisender reisender, Callback callback, Context context) {
        String byteString = Base64.encodeToString(byteImage, Base64.NO_WRAP);
        Dokument dokument = new Dokument(byteString, filetype, filename);
        UploadRequest uploadRequest = new UploadRequest(dokument, reise, reisender);
        OkHttpClient client = new OkHttpClient();
        String jwt = getJwtToken(context);
        String url = baseurl + "/api/reisedaten/uploadFile";

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(uploadRequest));
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .header("Authorization", "Bearer " + jwt)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);

    }

    public static void downloadFile(DownloadRequest downloadRequest, Callback callback, Context context) {

        OkHttpClient client = new OkHttpClient();
        String url = baseurl + "/api/reisedaten/downloadFile";
        String jwt = getJwtToken(context);

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(downloadRequest));
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .header("Authorization", "Bearer " + jwt)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);

    }

    public static void storeJwtToken(String token, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("jwt_token", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        //editor.putString("jwt_token", Base64.encodeToString(token.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
        editor.putString("jwt_token", token);
        editor.apply();

    }


    public static String getJwtToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("jwt_token", MODE_PRIVATE);
        //editor.putString("jwt_token", Base64.encodeToString(token.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
        return preferences.getString("jwt_token", "error");
    }

    public static void toLogin(AppCompatActivity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        context.finish();
    }

}
