package com.telegram.spektogram.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.telegram.spektogram.preferences.PreferenceUtils;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        final int connectivityStatus = NetworkUtil.getConnectivityStatus(context);
        if(connectivityStatus == NetworkUtil.TYPE_NOT_CONNECTED){
            PreferenceUtils.setOfflineMode(context,true);
        }else{
            PreferenceUtils.setOfflineMode(context,false);
        }
    }
}