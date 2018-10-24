package com.example.a1002732.clue.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.a1002732.clue.R;
import com.example.a1002732.clue.helper.LocationAssistant;
import com.example.a1002732.clue.service.LocationService;
import com.example.a1002732.clue.service.SensorService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private final int MY_PERMISSION_REQUEST_LOCATION = 100;
    private final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 101;

    private Intent sensorIntent;
    private Intent locationIntent;

    private LocationAssistant locationAssistant;

    protected LocationManager locationManager;

    boolean isGPSEnable = false;
    boolean isNetWorkEnable = false;
    boolean isGetLocation = false;
    Location location;
    double lat;
    double lon;

    CollisionBroadcastReceiver collisionBroadcastReceiver = new CollisionBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkPermission();


        //센서 서비스 시작
        sensorIntent = new Intent(MainActivity.this, SensorService.class);
        sensorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(sensorIntent);



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetWorkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);



        // 내 위치 서비스 시작
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, this);



        registerReceiver(collisionBroadcastReceiver, new IntentFilter("COLLISION_DETECTED_INTERNAL"));


        // Create the LocationAssistant object
        // locationAssistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 500, false);
        // locationAssistant.start();
    }




    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Location Service", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_REQUEST_LOCATION);

        }

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "Location Service", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_REQUEST_COARSE_LOCATION);

        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationAssistant.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(new Intent(MainActivity.this, SensorService.class));
        System.exit(0);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d("이준환", location.getLatitude() + " , "+location.getLongitude()+" , "+location.getProvider());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /**
     * Steps to take in case a collision is detected, notified from LocationChangeService.
     */
    public class CollisionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("SensorService", "감지");
        }
    }
}
