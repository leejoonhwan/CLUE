package com.example.a1002732.clue.activity;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.widget.TextView;
import android.widget.Toast;
import com.example.a1002732.clue.R;
import com.example.a1002732.clue.service.SensorService;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements LocationListener{
    private final static String TAG = MainActivity.class.getSimpleName();
    private final int MY_PERMISSION_REQUEST_LOCATION = 100;
    private final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 101;

    private Intent sensorIntent;
    private Intent locationIntent;

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
        textViewState = (TextView)findViewById(R.id.state);
        textViewPrompt = (TextView)findViewById(R.id.prompt);

        FirebaseInstanceId.getInstance().getToken();


        if (FirebaseInstanceId.getInstance().getToken() != null) {
            Log.d("이준환", "token = " + FirebaseInstanceId.getInstance().getToken());
        }




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










    private class UdpServerThread extends Thread{

        int serverPort;
        DatagramSocket socket;

        boolean running;

        public UdpServerThread(int serverPort) {
            super();
            this.serverPort = serverPort;
        }

        public void setRunning(boolean running){
            this.running = running;
        }

        @Override
        public void run() {

            running = true;

            try {
                updateState("Starting UDP Server");
                socket = new DatagramSocket(serverPort);

                updateState("UDP Server is running");
                Log.e(TAG, "UDP Server is running");

                while(running){
                    byte[] buf = new byte[256];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);     //this code block the program flow

                    // send the response to the client at "address" and "port"
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();

                    updatePrompt("Request from: " + address + ":" + port + "\n");

                    String dString = new Date().toString() + "\n"
                            + "Your address " + address.toString() + ":" + String.valueOf(port);
                    buf = dString.getBytes();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet);

                }

                Log.e(TAG, "UDP Server ended");

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(socket != null){
                    socket.close();
                    Log.e(TAG, "socket.close()");
                }
            }
        }
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
}
