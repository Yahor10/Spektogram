package com.telegram.spektogram.application;


import android.content.Context;
import android.content.pm.PackageManager;

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

        TG.setUpdatesHandler(this);

        File f = null;
        String path = "";
        try {
            final PackageManager packageManager = getPackageManager();
            f = new File(packageManager
                    .getPackageInfo(getPackageName(), 0)
                    .applicationInfo.dataDir + "/tdb/");
            if(!f.exists()) {
                f.mkdir();
                path = f.getAbsolutePath();
                TG.setDir(path);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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
        return client;
    }
}
