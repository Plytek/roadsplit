package com.example.roadsplit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.roadsplit.OnSwipeTouchListener;
import com.example.roadsplit.R;
import com.example.roadsplit.model.Stop;
import com.example.roadsplit.model.UserAccount;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private UserAccount userAccount = null;
    private MapView map = null;
    private RoadManager roadManager;
    private List<Address> adressen;
    private List<Stop> stops;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent i = getIntent();
        String userjson = i.getStringExtra("user");
        this.userAccount = (new Gson()).fromJson(userjson, UserAccount.class);
        try {
            this.stops = userAccount.getReisen().get(0).getStops();
        } catch (NullPointerException e) {
            Log.d("nulli", e.getMessage());
        }

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue("MyOwnUserAgent/1.0");

        LinearLayout linearLayout = findViewById(R.id.mapLayout);
        linearLayout.setOnTouchListener(new OnSwipeTouchListener(MapActivity.this)
        {
            @Override
            public void onSwipeRight() {
                back(findViewById(R.id.nextButton));
            }
        });

        this.roadManager = new OSRMRoadManager(this, Configuration.getInstance().getNormalizedUserAgent());
        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_BIKE);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);

        IMapController mapController = map.getController();
        mapController.setZoom(6.5);
        GeoPoint startPoint = new GeoPoint(53.551086, 9.993682);
        //GeoPoint startPoint = new GeoPoint(0, 0);
        mapController.setCenter(startPoint);

        map.invalidate();

        String[] permissions = new String[1];
        permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        requestPermissionsIfNecessary(permissions);
    }

    public void fetchGeodata(View view)
    {;
        ArrayList<GeoPoint> waypoints = fetchWayPointsFromAdress(stops);
        map.invalidate();
    }

    private void drawWaypoints(ArrayList<GeoPoint> waypoints)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Road road = roadManager.getRoad(waypoints);
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
            handler.post(() -> {
                Random random = new Random();
                for(GeoPoint waypoint : waypoints)
                {
                    Marker marker = new Marker(map);
                    marker.setPosition(waypoint);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setIcon(getResources().getDrawable(R.drawable.logoassetthree));
                    marker.setTitle(" Budget: " + (random.nextInt(1000 - 40) + 40) + "€"); //
                    map.getOverlays().add(marker);
                }
                map.getOverlays().add(roadOverlay);
                IMapController mapController = map.getController();
                mapController.setZoom(8.5);
                mapController.setCenter(waypoints.get(0));
                map.invalidate();
            });
        });
    }

    private ArrayList<GeoPoint> fetchWayPointsFromAdress(List<Stop> stops)
    {
        Geocoder geocoder = new Geocoder(this, Locale.GERMAN);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        adressen = new ArrayList<>();
        executor.execute(() -> {
            for(Stop stop : stops) {
                String name = stop.getName().replace("\n", "");
                try {
                    adressen.add(geocoder.getFromLocationName(name, 1).get(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for(Address address : adressen)
                waypoints.add(new GeoPoint(address.getLatitude(), address.getLongitude()));

            handler.post(() -> {
                drawWaypoints(waypoints);
                EditText v = findViewById(R.id.geodataText);
                v.setText(adressen.toString());
                Log.d("adressen", String.valueOf(adressen));
            });
        });
        return waypoints;
    }

    public void back(View view)
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void showFullscreenMap(View view)
    {
        LinearLayout linearLayout = findViewById(R.id.topMapLayout);
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


}