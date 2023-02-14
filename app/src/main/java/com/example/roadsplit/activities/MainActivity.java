package com.example.roadsplit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.testing.MapActivity;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.UserAccount;
import com.example.roadsplit.reponses.ReiseResponse;
import com.example.roadsplit.reponses.UserResponse;
import com.example.roadsplit.requests.UploadRequest;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Getter
@Setter
public class MainActivity extends AppCompatActivity {

    //public static final String BASEURL = "https://eb82-84-63-180-89.ngrok.io";
    //public static Reisender currentUser;
    public static final String BASEURL = "http://167.172.167.221:8080";
    private static final int REQUEST_CODE_PICK_FILE = 1;
    //public static CurrentUserData currentUserData;
    private final UserAccount userAccount = null;
    private SharedPreferences reisenderPref;
    private SharedPreferences reiseResponsesPref;

    private ImageView retrievedImage;
    private ReiseResponse reiseResponse;
    private Reisender reisender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Das hier auszukommentieren damit die App beim starten auf den Debugger wartet
        //Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retrievedImage = findViewById(R.id.retrievedImageView);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        this.reisenderPref = getSharedPreferences("reisender", MODE_PRIVATE);
        this.reiseResponsesPref = getSharedPreferences("reiseResponses", MODE_PRIVATE);

        //this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);
        try {
            //login();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Startet einen neuen Intent (Activity) - die MapActivity
    public void openMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        //Gibt zusätzliche Daten mit durch .putExtra(Key, Value). In diesem Fall den aktuellen Useraccount als String
        //Startet die Activity
        startActivity(intent);
    }

    public void neueReise(View view) {
        Intent intent = new Intent(this, NeueReiseActivity.class);
        startActivity(intent);
    }

    public void createUser(View view) {
    }

    public void registrieren(View view) {
        Intent intent = new Intent(this, RegistryActivity.class);
        startActivity(intent);
    }

    public void reiseText(View view) {
    }

    public void turorialAnim(View view) {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

    public void einloggen(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void reiseErstelen(View view) {
    }

    public void testBent(View view) {
        Intent intent = new Intent(this, ReiseErstellenActivity.class);
        startActivity(intent);
    }

    public void testKnecht(View view) {
        setContentView(R.layout.testknecht);
    }


    public void reiseDetail(View view) {
        Intent intent = new Intent(this, ReiseUebersichtActivity.class);
        startActivity(intent);
    }


    private void login() {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.BASEURL + "/api/userdaten/login";
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        UserAccount userAccount = new UserAccount();
        String username = "lpa";
        String password = "test1";

        userAccount.setNickname(username);
        userAccount.setPassword(password);
        userAccount.setFirsttimelogin(false);
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(userAccount));

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(formBody)
                .build();

        //Erstellt in einem neuen Thread eine Http Anfrage an den Webservice, ruft bei Erfolg onReponse() auf, bei Misserfolg onFailure()
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                UserResponse userResponse = new Gson().fromJson(response.body().string(), UserResponse.class);
                if (response.isSuccessful()) {
                    reisender = userResponse.getReisender();
                    SharedPreferences.Editor editor = reisenderPref.edit();
                    editor.putString("reisender", new Gson().toJson(userResponse.getReisender()));
                    editor.apply();
                    EndpointConnector.fetchPaymentInfoSummary(userResponse.getReisender().getReisen().get(0), userResponse.getReisender(), fetchPaymentInfoSummaryCallback());
                }
            }
        });
    }

    private Callback fetchPaymentInfoSummaryCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ReiseResponse[] reiseResponses = new ReiseResponse[0];
                try {
                    reiseResponses = new Gson().fromJson(response.body().string(), ReiseResponse[].class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    reiseResponse = reiseResponses[0];
                    SharedPreferences.Editor editor = reiseResponsesPref.edit();
                    editor.putString("reiseResponses", new Gson().toJson(reiseResponses));
                    editor.apply();
                }
            }
        };
    }

    public void uploadFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
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

                EndpointConnector.uploadFile(bytes, fileName, fileType, reisender.getReisen().get(0), reisender, uploadFileCallback(), this);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

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

    public String getPath(Uri uri) {

        String path = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                UploadRequest uploadRequest = gson.fromJson(response.body().string(), UploadRequest.class);
                if (response.isSuccessful()) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                        byte[] byteArray = Base64.decode(uploadRequest.getDokument().getEncodedImage(), Base64.NO_WRAP);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        runOnUiThread(() -> {
                            retrievedImage.setImageBitmap(bitmap);
                        });
                    }
                }
            }
        };
    }

}
