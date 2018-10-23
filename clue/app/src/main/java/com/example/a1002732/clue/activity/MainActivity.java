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
import com.example.a1002732.clue.service.SensorService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationAssistant.Listener{

    private final int MY_PERMISSION_REQUEST_LOCATION = 100;
    private Intent sensorIntent;
    private LocationAssistant locationAssistant;

    CollisionBroadcastReceiver collisionBroadcastReceiver = new CollisionBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorIntent = new Intent(MainActivity.this, SensorService.class);
        sensorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(sensorIntent);

        checkPermission();


        registerReceiver(collisionBroadcastReceiver, new IntentFilter("COLLISION_DETECTED_INTERNAL"));


        // Create the LocationAssistant object
        locationAssistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 500, false);
        locationAssistant.start();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Location Service", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_LOCATION);

        } else {

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
    public void onNeedLocationPermission() {

    }

    @Override
    public void onExplainLocationPermission() {

    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNeedLocationSettingsChange() {

    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNewLocationAvailable(Location location) {

    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    /**
     * Steps to take in case a collision is detected, notified from LocationChangeService.
     */
    public class CollisionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(getApplicationContext(), (String) intent.getExtras().get("accidentLocation"), Toast.LENGTH_SHORT).show();
        }
    }
}
