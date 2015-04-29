package com.telegram.spektogram.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Set;

public class GCMHandleIntent extends IntentService {

    public GCMHandleIntent() {
        super("GCMHandleIntent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        dumpMessage(intent);
    }

    private void dumpMessage(Intent receivedIntent) {
        Log.v(null, "---------GCM----------");
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        Log.v(null, "MessageType: " + gcm.getMessageType(receivedIntent));
        Log.v(null, "Action: " + receivedIntent.getAction());
        Set<String> keys = receivedIntent.getExtras().keySet();
        for (String key : keys) {
            Log.v(null, key + ": " + receivedIntent.getExtras().get(key));
        }
        Log.v(null, "---------/GCM----------");
    }
}