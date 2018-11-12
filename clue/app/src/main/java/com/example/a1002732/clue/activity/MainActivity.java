package com.example.a1002732.clue.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.a1002732.clue.R;
import com.example.a1002732.clue.service.MediaService;
import com.example.a1002732.clue.service.SensorService;
import com.example.a1002732.clue.util.NotiUtil;
import com.google.firebase.iid.FirebaseInstanceId;





public class MainActivity extends AppCompatActivity implements LocationListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private final int MY_PERMISSION_REQUEST_LOCATION = 101;
    private final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 101;

    private Intent sensorIntent;
    private Intent mediaIntent;

    protected LocationManager locationManager;

    boolean isGPSEnable = false;
    boolean isNetWorkEnable = false;
    boolean isGetLocation = false;
    Location location;


    TextView infoIp, infoPort;
    TextView textViewState, textViewPrompt;

    CollisionBroadcastReceiver collisionBroadcastReceiver = new CollisionBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoIp = (TextView) findViewById(R.id.infoip);
        infoPort = (TextView) findViewById(R.id.infoport);
        textViewState = (TextView) findViewById(R.id.state);
        textViewPrompt = (TextView) findViewById(R.id.prompt);


        checkPermission();

    }
    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_REQUEST_LOCATION);
        }


        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for(int permission : grantResults) {
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "필수 권한이 없어 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Permission always deny");
                finish();
            }
        }
    }




    @SuppressLint("MissingPermission")
    private void initProc() {
        FirebaseInstanceId.getInstance().getToken();


        if (FirebaseInstanceId.getInstance().getToken() != null) {
            Log.d("이준환", "token = " + FirebaseInstanceId.getInstance().getToken());
        }


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



        // UDP 미디어 전송 서비스 시작
        mediaIntent = new Intent(MainActivity.this, MediaService.class);
        mediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(mediaIntent);




        textViewState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //** 123456은 고유 ID값이므로 아무거나 들어가도 상관 없음
                NotiUtil notiUtil = new NotiUtil(getApplicationContext());
                notiUtil.presentHeadsUpNotification(Notification.VISIBILITY_PUBLIC, R.drawable.iotc_icon, "tttt", "ttttttt");
            }
        });
        // Create the LocationAssistant object
        // locationAssistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 500, false);
        // locationAssistant.start();
    }








    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this, SensorService.class));
        stopService(new Intent(MainActivity.this, MediaService.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(new Intent(MainActivity.this, SensorService.class));
        stopService(new Intent(MainActivity.this, MediaService.class));
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

            Log.d("SensorService", location.getLatitude()+","+location.getLongitude());
        }
    }






    private void updateState(final String state){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewState.setText(state);
            }
        });
    }

    private void updatePrompt(final String prompt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewPrompt.append(prompt);
            }
        });
    }
}
