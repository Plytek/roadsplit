package com.example.roadsplit.tabsetups;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.activities.UploadFileActivity;
import com.example.roadsplit.adapter.DokumenteRecyclerAdapter;
import com.example.roadsplit.model.Dokument;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.utility.ButtonEffect;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DokumentSetup {

    private final View layoutScreen;
    private final Context context;
    private final SharedPreferences reportPref;
    private final AusgabenReport ausgabenReport;

    private final Button uploadButton;
    private final RecyclerView dokumentListView;
    private final ProgressBar dokumentProgressBar;
    private ImageView noDocumentImage;
    private ArrayAdapter<String> dokumentAdapter;
    private AusgabenActivity ausgabenActivity;

    public DokumentSetup(View layoutScreen, Context context, AusgabenActivity ausgabenActivity) {
        this.layoutScreen = layoutScreen;
        this.context = context;

        this.reportPref = context.getSharedPreferences("report", MODE_PRIVATE);
        this.ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);

        uploadButton = layoutScreen.findViewById(R.id.uploadButton);
        dokumentListView = layoutScreen.findViewById(R.id.dokumentListView);
        dokumentProgressBar = layoutScreen.findViewById(R.id.dokumenteProgressBar);
        noDocumentImage = layoutScreen.findViewById(R.id.noDokumenteImageView);

        dokumentProgressBar.setVisibility(View.INVISIBLE);
        ButtonEffect.buttonPressDownEffect(uploadButton);

        if (ausgabenReport.getFileNames() == null || ausgabenReport.getFileNames().isEmpty())
            noDocumentImage.setVisibility(View.VISIBLE);
        else
            noDocumentImage.setVisibility(View.INVISIBLE);

        if (!ausgabenReport.getReise().isOngoing())
            uploadButton.setVisibility(View.GONE);
        else
            uploadButton.setVisibility(View.VISIBLE);

        uploadButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, UploadFileActivity.class);
            intent.putExtra("reise", new Gson().toJson(ausgabenReport.getReise()));
            context.startActivity(intent);
        });

    }

    public void setupDokuments() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        dokumentListView.setLayoutManager(layoutManager);
        DokumenteRecyclerAdapter dokumenteRecyclerAdapter = new DokumenteRecyclerAdapter(context, ausgabenReport.getFileNames(), dokumentProgressBar, ausgabenReport, donwloadFileCallback());
        dokumentListView.setAdapter(dokumenteRecyclerAdapter);

    }

    private Callback donwloadFileCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                Dokument dokument = gson.fromJson(response.body().string(), Dokument.class);
                dokumentProgressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {

                    String encodedImage = dokument.getEncodedImage();
                    byte[] decodedImage = Base64.decode(encodedImage, Base64.DEFAULT);
                    String fileName = dokument.getFileName();

                    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(directory, fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(decodedImage);
                    fos.close();
                    context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));

                } else if (response.code() == 403) {
                    EndpointConnector.toLogin(ausgabenActivity);
                }
            }

        };
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(ausgabenActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }


    }
}
