package com.example.a1002732.clue.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by 1002732 on 2018. 4. 17..
 */

public class SplashActivity extends Activity {
    private static final String TAG = "AppPermission";
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private final int MY_PERMISSION_REQUEST_LOCATION = 100;
    private final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 101;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // checkPermission();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }





}
