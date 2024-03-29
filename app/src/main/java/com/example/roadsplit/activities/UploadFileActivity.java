package com.example.roadsplit.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UploadFileActivity extends AppCompatActivity {

    private Reise reise;
    private Reisender reisender;
    private SharedPreferences reportPref;
    private SharedPreferences reisenderPref;
    private ProgressBar uploadProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Intent intent = getIntent();
        reise = new Gson().fromJson(intent.getStringExtra("reise"), Reise.class);
        this.reportPref = getSharedPreferences("report", MODE_PRIVATE);
        this.reisenderPref = getSharedPreferences("reisender", MODE_PRIVATE);
        this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
        uploadProgressBar = findViewById(R.id.uploadProgressBar);
        uploadProgressBar.setVisibility(View.INVISIBLE);

        uploadFile();
    }


    public void uploadFile() {
        uploadProgressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*"); // Set the MIME type to allow any file type
        String[] mimeTypes = {"image/*", "application/pdf"}; // Allow images and PDFs
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Get the input stream
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                // Get the byte array
                byte[] bytes = getBytes(inputStream);
                String fileName = null;
                String fileType = null;
                Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
                    fileName = cursor.getString(displayNameIndex);
                    fileType = cursor.getString(mimeTypeIndex);
                    cursor.close();
                }
                // Extract the file extension from the file type
                if (fileType != null && fileType.contains("/")) {
                    fileType = fileType.substring(fileType.lastIndexOf("/") + 1);
                }

                openConfirmDialog(bytes, fileName, fileType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resultCode == RESULT_CANCELED) {
            finish();
        }

    }

    private void openConfirmDialog(byte[] bytes, String fileName, String fileType) {
        Dialog dialog = new Dialog(this) {
            @Override
            public void onBackPressed() {
                finish();
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setTitle("Dateinamen ändern?");
        builder.setPositiveButton("Datei speichern", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFilename = input.getText().toString();
                // Set the TextView's text to the value entered by the user
                if (!newFilename.isEmpty()) {
                    int dotIndex = fileName.lastIndexOf(".");
                    String fileExtension = fileName.substring(dotIndex);
                    EndpointConnector.uploadFile(bytes, newFilename + fileExtension, fileType, reise, reisender, uploadFileCallback(), UploadFileActivity.this);
                    dialog.dismiss();
                } else {
                    EndpointConnector.uploadFile(bytes, fileName, fileType, reise, reisender, uploadFileCallback(), UploadFileActivity.this);
                    dialog.dismiss();
                }

            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    alertDialog.dismiss();
                    finish();
                }
                return true;
            }
        });

       /* dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fileuploaddialog);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopUpAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView filename = dialog.findViewById(R.id.filenameChangeEditText);
        TextView uploadButton = dialog.findViewById(R.id.confirmUploadButton);
        TextView cancelButton = dialog.findViewById(R.id.cancelUploadButton);


        cancelButton.setOnClickListener(view -> {
            dialog.dismiss();
            finish();
        });

        uploadButton.setOnClickListener(view -> {
            String newFilename = filename.getText().toString();
            if (!newFilename.isEmpty()) {
                int dotIndex = fileName.lastIndexOf(".");
                String fileExtension = fileName.substring(dotIndex);
                EndpointConnector.uploadFile(bytes, newFilename + fileExtension, fileType, reise, reisender, uploadFileCallback());
                dialog.dismiss();
            } else {
                EndpointConnector.uploadFile(bytes, fileName, fileType, reise, reisender, uploadFileCallback());
                dialog.dismiss();
            }
        });*/


    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private Callback uploadFileCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                TextView textView = findViewById(R.id.errorRegView);
                String text = "Es konnte keine Verbindung zum Server hergestellt werden";
                textView.setText(text);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                AusgabenReport ausgabenReport = gson.fromJson(response.body().string(), AusgabenReport.class);
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = reportPref.edit();
                    editor.putString("report", gson.toJson(ausgabenReport));
                    editor.apply();


                    runOnUiThread(() -> {
                        uploadProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadFileActivity.this, "Upload Erfolgreich", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UploadFileActivity.this, AusgabenActivity.class);
                        intent.putExtra("reise", new Gson().toJson(reise, Reise.class));
                        intent.putExtra("returning", "3");
                        startActivity(intent);
                        finish();
                    });
                } else if (response.code() == 403) {
                    EndpointConnector.toLogin(UploadFileActivity.this);
                }
            }
        };
    }

}
