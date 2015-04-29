package com.telegram.spektogram.gcm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class GCMMessageReceiver extends BroadcastReceiver {

    public static String GCM_PREF_FILE = "gcm_preferences";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.v(null, "GCM ACTION:" + action);

        intent.setComponent(new ComponentName(context, GCMHandleIntent.class));
        if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {


        }

        context.startService(intent);
    }


}
