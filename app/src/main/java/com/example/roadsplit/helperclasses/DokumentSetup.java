package com.example.roadsplit.helperclasses;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.activities.UploadFileActivity;
import com.example.roadsplit.model.Dokument;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.requests.DownloadRequest;
import com.example.roadsplit.requests.UploadRequest;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DokumentSetup {

    private final View layoutScreen;
    private final Context context;
    private final SharedPreferences reportPref;
    private final AusgabenReport ausgabenReport;

    private final Button uploadButton;
    private final Button downloadButton;
    private final ListView dokumentListView;
    private final ProgressBar dokumentProgressBar;
    private ArrayAdapter<String> dokumentAdapter;
    private AusgabenActivity ausgabenActivity;

    public DokumentSetup(View layoutScreen, Context context, AusgabenActivity ausgabenActivity) {
        this.layoutScreen = layoutScreen;
        this.context = context;

        this.reportPref = context.getSharedPreferences("report", MODE_PRIVATE);
        this.ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);

        uploadButton = layoutScreen.findViewById(R.id.uploadButton);
        downloadButton = layoutScreen.findViewById(R.id.downloadButton);
        dokumentListView = layoutScreen.findViewById(R.id.dokumentListView);
        dokumentProgressBar = layoutScreen.findViewById(R.id.dokumenteProgressBar);

        dokumentProgressBar.setVisibility(View.INVISIBLE);
        ButtonEffect.buttonPressDownEffect(downloadButton);
        ButtonEffect.buttonPressDownEffect(uploadButton);

        if(!ausgabenReport.getReise().isOngoing())
            uploadButton.setVisibility(View.GONE);
        else
            uploadButton.setVisibility(View.VISIBLE);

        uploadButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, UploadFileActivity.class);
            intent.putExtra("reise", new Gson().toJson(ausgabenReport.getReise()));
            context.startActivity(intent);
        });

        downloadButton.setOnClickListener(view -> {

            int selectedIndex = dokumentListView.getCheckedItemPosition();
            if(selectedIndex != ListView.INVALID_POSITION) {
                dokumentProgressBar.setVisibility(View.VISIBLE);
                String selectedItem = dokumentAdapter.getItem(selectedIndex);
                DownloadRequest downloadRequest = new DownloadRequest();
                downloadRequest.setReiseId(ausgabenReport.getReise().getId());
                downloadRequest.setFilename(selectedItem);
                EndpointConnector.downloadFile(downloadRequest, donwloadFileCallback());
            }

        });
    }

    public void setupDokuments(){
        dokumentListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        dokumentAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, ausgabenReport.getFileNames());
        dokumentListView.setAdapter(dokumentAdapter);

    }

    private Callback donwloadFileCallback()
    {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                Dokument dokument = gson.fromJson(response.body().string(), Dokument.class);
                dokumentProgressBar.setVisibility(View.INVISIBLE);
                if(response.isSuccessful()) {

                    String encodedImage = dokument.getEncodedImage();
                    byte[] decodedImage = Base64.decode(encodedImage, Base64.DEFAULT);
                    String fileName = dokument.getFileName();

                    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(directory, fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(decodedImage);
                    fos.close();
                    context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));

                }
            }

        };
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(ausgabenActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }


    }
}