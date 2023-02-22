package com.example.roadsplit.tabsetups;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.activities.AusgabenActivity;
import com.example.roadsplit.adapter.StopRecyclerAdapter;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.requests.UpdateRequest;
import com.example.roadsplit.utility.ItemMoveCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ZwischenstopSetup {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private final SharedPreferences reportPref;
    private final SharedPreferences reisenderPref;
    private final SharedPreferences reisePref;
    private Context context;
    private AusgabenActivity ausgabenActivity;
    private View layoutScreen;
    private EditText zwischenstopText;
    private RecyclerView zwischenstopRecycler;
    private Button saveChanges;
    private Button showMap;
    private Button addZwischenStop;
    private ProgressBar progressBar;

    private AusgabenReport ausgabenReport;
    private Reisender reisender;
    private Reise reise;
    private List<Stop> stops;

    private FusedLocationProviderClient fusedLocationClient;
    private MapView map;
    private GeoPoint lastLocation;

    public ZwischenstopSetup(Context context, AusgabenActivity ausgabenActivity, View layoutScreen) {
        this.context = context;
        this.ausgabenActivity = ausgabenActivity;
        this.layoutScreen = layoutScreen;

        this.zwischenstopText = layoutScreen.findViewById(R.id.zwischenstopTextField);
        this.zwischenstopRecycler = layoutScreen.findViewById(R.id.zwischenstoppDetailsRecycler);
        this.saveChanges = layoutScreen.findViewById(R.id.saveStopChangesButton);
        this.showMap = layoutScreen.findViewById(R.id.showMapButton);
        this.addZwischenStop = layoutScreen.findViewById(R.id.addZwischenStopButton);
        this.progressBar = layoutScreen.findViewById(R.id.zstopProgressBar);

        progressBar.setVisibility(View.INVISIBLE);

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        this.reportPref = context.getSharedPreferences("report", MODE_PRIVATE);
        this.reisenderPref = context.getSharedPreferences("reisender", MODE_PRIVATE);
        this.reisePref = context.getSharedPreferences("reise", MODE_PRIVATE);
        this.ausgabenReport = new Gson().fromJson(reportPref.getString("report", "fehler"), AusgabenReport.class);
        this.reisender = new Gson().fromJson(reisenderPref.getString("reisender", "fehler"), Reisender.class);

        this.reise = ausgabenReport.getReise();
        this.stops = reise.getStops();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        zwischenstopRecycler.setLayoutManager(layoutManager);
        StopRecyclerAdapter stopRecyclerAdapter = new StopRecyclerAdapter(context, stops);
        zwischenstopRecycler.setAdapter(stopRecyclerAdapter);


        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(stopRecyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(zwischenstopRecycler);

        saveChanges.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            reise.setStops(stops);
            UpdateRequest updateRequest = new UpdateRequest(reisender, reise);
            EndpointConnector.updateReise(updateRequest, updateReiseCallback(), context);
        });

        showMap.setOnClickListener(view -> {
            String[] permissions = new String[1];
            permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            requestPermissionsIfNecessary(permissions);
            mapAnzeigen();
        });

        addZwischenStop.setOnClickListener(view -> {
            String stopName = zwischenstopText.getText().toString();
            if (stopName.isEmpty()) return;
            Stop stop = new Stop();
            stop.setName(stopName);
            stop.setGesamtausgaben(BigDecimal.ZERO);
            stop.setBudget(BigDecimal.ZERO);
            stop.setAusgaben(new ArrayList<>());

            stops.add(stop);
            stopRecyclerAdapter.notifyItemChanged(stops.size() - 1);
        });
    }

    private Callback updateReiseCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                ausgabenReport = gson.fromJson(response.body().string(), AusgabenReport.class);
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = reportPref.edit();
                    editor.putString("report", gson.toJson(ausgabenReport));
                    editor.apply();

                    editor = reisePref.edit();
                    editor.putString("reise", gson.toJson(ausgabenReport.getReise()));
                    editor.apply();


                    ausgabenActivity.runOnUiThread(() ->
                    {
                        Intent intent = new Intent(context, AusgabenActivity.class);
                        intent.putExtra("reise", new Gson().toJson(ausgabenReport.getReise()));
                        intent.putExtra("returning", "1");
                        ausgabenActivity.startActivity(intent);
                        ausgabenActivity.finish();
                    });
                } else if (response.code() == 403) {
                    EndpointConnector.toLogin(ausgabenActivity);
                }

            }
        };
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    (AppCompatActivity) context,
                    permissionsToRequest.toArray(new String[0]),
                    1);
            mapAnzeigen();
        }
    }

    private void mapAnzeigen() {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.maplayout);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopUpAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);

        fetchWayPointsFromAdress(stops, dialog);

    /*    Handler mHandler = new Handler();
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                getCurrentLocation();
                Log.d("location", "Task executed at: " + new java.util.Date());
                mHandler.postDelayed(this, 5000);
            }
        };

        mHandler.postDelayed(mRunnable, 0);

        dialog.setOnDismissListener(dialogInterface -> mHandler.removeCallbacks(mRunnable));*/
    }

    private void drawWaypoints(ArrayList<GeoPoint> waypoints, Dialog dialog) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Configuration.getInstance().setUserAgentValue("RoadSplitUserAgent/1.0");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        OSRMRoadManager roadManager = new OSRMRoadManager(context, Configuration.getInstance().getNormalizedUserAgent());
        roadManager.setMean(OSRMRoadManager.MEAN_BY_CAR);

        map = dialog.findViewById(R.id.mapView3);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);

/*        IMapController mapController = map.getController();
        mapController.setZoom(6.5);
        GeoPoint startPoint = new GeoPoint(53.551086, 9.993682);
        mapController.setCenter(startPoint);
        map.invalidate();*/

        executor.execute(() -> {

            Road road = roadManager.getRoad(waypoints);
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
            handler.post(() -> {
                int counter = 0;
                for (GeoPoint waypoint : waypoints) {
                    BigDecimal budget = stops.get(counter).getBudget();
                    if (budget == null) budget = new BigDecimal(0);
                    Marker marker = new Marker(map);
                    marker.setPosition(waypoint);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setIcon(context.getResources().getDrawable(R.drawable.baseline_place_black_24dp));
                    marker.setTitle(" Budget: " + stops.get(counter).getBudget() + "€"); //
                    map.getOverlays().add(marker);
                    counter++;
                }
                getCurrentLocation();


                map.getOverlays().add(roadOverlay);
/*                IMapController mapController = map.getController();
                mapController.setZoom(8.5);
                try {
                    mapController.setCenter(waypoints.get(0));
                } catch (Exception e) {
                    Log.d("map", "Error beim map fetchen");
                    e.printStackTrace();
                    mapController.setCenter(new GeoPoint(53.551086, 9.993682));
                }*/
                map.invalidate();
            });
        });


    }

    private void fetchWayPointsFromAdress(List<Stop> stops, Dialog dialog) {
        Geocoder geocoder = new Geocoder(context, Locale.GERMAN);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        List<Address> adressen = new ArrayList<>();
        executor.execute(() -> {
            for (Stop stop : stops) {
                String name = stop.getName().replace("\n", "");
                try {
                    adressen.add(geocoder.getFromLocationName(name, 1).get(0));
                } catch (Exception e) {
                    Log.d("geocoder", "Error beim Finden der Adresse im Geocoder");
                }
            }
            for (Address address : adressen)
                waypoints.add(new GeoPoint(address.getLatitude(), address.getLongitude()));
            handler.post(() -> {
                drawWaypoints(waypoints, dialog);
            });
        });
    }


    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(ausgabenActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(ausgabenActivity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Convert the location to a GeoPoint
                                lastLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                                if (lastLocation != null) {
                                    Marker marker = new Marker(map);
                                    marker.setPosition(lastLocation);
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    marker.setIcon(context.getResources().getDrawable(R.drawable.baseline_my_location_black_24dp));
                                    marker.setTitle("It's you!"); //
                                    map.getOverlays().add(marker);

                                    IMapController mapController = map.getController();
                                    mapController.setZoom(8.5);
                                    try {
                                        mapController.setCenter(lastLocation);
                                    } catch (Exception e) {
                                        Log.d("map", "Error beim map fetchen");
                                        e.printStackTrace();
                                        mapController.setCenter(new GeoPoint(53.551086, 9.993682));
                                    }
                                    map.invalidate();
                                }
                                // Do something with the GeoPoint
                            }
                        }
                    });
        }
    }
}
