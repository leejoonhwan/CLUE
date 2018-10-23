package com.example.a1002732.clue.service;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class SensorService extends IntentService implements SensorEventListener {


    private long lastUpdate = 0;
    private float last_x = 6.0f, last_y = 6.0f, last_z = 6.0f;


    public SensorService() {
        super("SensorService");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 100) {
            lastUpdate = curTime;

            //Check for axial acceleration changes and invoke LocationChangeService
            if(Math.abs(x-last_x) > 8 || Math.abs(y-last_y) > 8 || Math.abs(z-last_z) > 8){
                Intent locationIntent = new Intent(getApplicationContext(), LocationChangeService.class);
                locationIntent.setAction("start_location_tracing");
                startService(locationIntent);
            }

            last_x = x;
            last_y = y;
            last_z = z;
        }

        stopSelf();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SensorManager manager = null;
        Sensor sensor = null;
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
}
