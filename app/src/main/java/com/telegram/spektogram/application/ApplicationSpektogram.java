package com.telegram.spektogram.application;


import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.telegram.spektogram.preferences.PreferenceUtils;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TG;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;

/**
 * Created by ychabatarou on 27.04.2015.
 */
public class ApplicationSpektogram extends android.app.Application implements Client.ResultHandler {


    private Client client;

    @Override
    public void onCreate() {
        super.onCreate();

            startTelegramApi();

    }

    private void startTelegramApi() {
        Log.v(null, "init spektogram app...");

        File f = null;
        String path = "";
        try {
            final PackageManager packageManager = getPackageManager();
            f = new File(packageManager
                    .getPackageInfo(getPackageName(), 0)
                    .applicationInfo.dataDir + "/tdb/");
            if(!f.exists()) {
                f.mkdir();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(!PreferenceUtils.isTGinit(this)) {
            path = f.getAbsolutePath();
            TG.setUpdatesHandler(this);
            TG.setDir(path);
            PreferenceUtils.setTGinit(this,true);
        }

        if(client == null) {
            client = TG.getClientInstance();
        }
    }


    @Override
    public void onResult(TdApi.TLObject object) {

    }

    public static ApplicationSpektogram getApplication(Context context) {
        return (ApplicationSpektogram) context.getApplicationContext();
    }

    public Client getClient() {
        if(client == null) {
            try {
                File f = new File(getPackageManager()
                        .getPackageInfo(getPackageName(), 0)
                        .applicationInfo.dataDir + "/tdb/");
                if(f.exists()){
                    final String absolutePath = f.getAbsolutePath();
                    TG.setDir(absolutePath);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            client = TG.getClientInstance();
        }
        return client;
    }

    public void sendFunction(TdApi.TLFunction func, Client.ResultHandler handler){
        if(!PreferenceUtils.isOfflineMode(this)) {
            if (client == null) {
                client = getClient();
            }
            client.send(func, handler);
        }else{
            // TODO send message about offline;
        }
    }
}
