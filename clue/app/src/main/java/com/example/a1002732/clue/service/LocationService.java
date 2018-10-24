package com.example.a1002732.clue.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class LocationService extends IntentService implements LocationListener{
    private static final long MIN_DISTANCE_UPDATE = 0;

    protected LocationManager locationManager;

    boolean isGPSEnable = false;
    boolean isNetWorkEnable = false;
    boolean isGetLocation = false;
    Location location;
    double lat;
    double lon;

    public LocationService() {
        super("LocationService");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetWorkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("이준환", location.getLatitude() + " , "+location.getLongitude()+" , "+location.getProvider());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
