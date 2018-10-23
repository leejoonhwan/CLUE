package com.example.a1002732.clue;

import android.app.Application;
import android.content.Context;

/**
 * Created by Jagrut on 17-Apr-16.
 * To create application context which can be used from POJO and initiate a global ActivityLifeCycleCallBack
 */
public class ClueApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        ClueApplication.context = getApplicationContext();
        registerActivityLifecycleCallbacks(new ActivityLifeCycleHandler());
    }

    public static Context getAppContext() {
        return ClueApplication.context;
    }
}
