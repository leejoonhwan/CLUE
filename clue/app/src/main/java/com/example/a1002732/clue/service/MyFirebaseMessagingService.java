package com.example.a1002732.clue.service;

import com.example.a1002732.clue.R;
import com.example.a1002732.clue.util.NotiUtil;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Created by Joo on 2017. 12. 19.
 */

import android.app.Notification;
import android.util.Log;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    // 메시지 수신
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived");

        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String messagae = data.get("content");

        NotiUtil notiUtil = new NotiUtil(getApplicationContext());
        notiUtil.presentHeadsUpNotification(Notification.VISIBILITY_PUBLIC, R.drawable.iotc_icon, title, messagae);
    }


}
