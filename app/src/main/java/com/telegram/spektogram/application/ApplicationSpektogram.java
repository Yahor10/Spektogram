package com.telegram.spektogram.application;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.telegram.spektogram.telegram.Client;
import com.telegram.spektogram.telegram.TG;
import com.telegram.spektogram.telegram.TdApi;

import org.telegram.api.TLAbsUpdates;
import org.telegram.api.TLConfig;
import org.telegram.api.TLDcOption;
import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.mtproto.state.AbsMTProtoState;
import org.telegram.mtproto.state.ConnectionInfo;
import org.telegram.mtproto.state.KnownSalt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ychabatarou on 27.04.2015.
 */
public class ApplicationSpektogram extends android.app.Application implements AbsApiState,ApiCallback {

    private int primerId = 1;
    private static HashMap<Integer, org.telegram.mtproto.state.ConnectionInfo[]> connections = new HashMap<Integer, ConnectionInfo[]>();
    public static Context applicationContext;

    private HashMap<Integer, Boolean> isAuth = new HashMap<Integer, Boolean>();
    private HashMap<Integer, byte[]> keys = new HashMap<Integer, byte[]>();
    private TelegramApi api;

    @Override
    public void onCreate() {
        super.onCreate();

        connections.put(1, new ConnectionInfo[]{
                new ConnectionInfo(1, 0, true ? Constants.Test_Configuration_Server : Constants.Production_Configuration_Server, 443)
        });

        try {
            startTelegramApi();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPrimaryDc() {
        return primerId;
    }

    @Override
    public void setPrimaryDc(int i) {
        primerId = i;
    }

    @Override
    public boolean isAuthenticated(int dcId) {
        if (isAuth.containsKey(dcId)) {
            return isAuth.get(dcId);
        }
        return false;
    }

    @Override
    public void setAuthenticated(int dcId, boolean auth) {
        isAuth.put(dcId, auth);

    }

    @Override
    public void updateSettings(TLConfig tlConfig) {
// TODO Auto-generated method stub
        connections.clear();
        HashMap<Integer, ArrayList<org.telegram.mtproto.state.ConnectionInfo>> tConnections = new HashMap<Integer, ArrayList<org.telegram.mtproto.state.ConnectionInfo>>();
        int id = 0;

        for (TLDcOption option : tlConfig.getDcOptions()) {
            if (!tConnections.containsKey(option.getId())) {
                tConnections.put(option.getId(), new ArrayList<ConnectionInfo>());
            }
            tConnections.get(option.getId()).add(new ConnectionInfo(id++, 0, option.getIpAddress(), option.getPort()));
        }

        for (Integer dc : tConnections.keySet()) {
            connections.put(dc, tConnections.get(dc).toArray(new ConnectionInfo[0]));
        }
    }

    @Override
    public byte[] getAuthKey(int dcId) {
        // TODO Auto-generated method stub
        return keys.get(dcId);
    }

    @Override
    public void putAuthKey(int dcId, byte[] key) {
        // TODO Auto-generated method stub
        keys.put(dcId, key);

    }

    @Override
    public ConnectionInfo[] getAvailableConnections(int dcId) {
        // TODO Auto-generated method stub
        if (!connections.containsKey(dcId)) {
            return new ConnectionInfo[0];
        }

        return connections.get(dcId);
    }

    @Override
    public AbsMTProtoState getMtProtoState(final int dcId) {
        return new AbsMTProtoState() {
            private KnownSalt[] knownSalts = new KnownSalt[0];

            @Override
            public byte[] getAuthKey() {
                return ApplicationSpektogram.this.getAuthKey(dcId);
            }

            @Override
            public ConnectionInfo[] getAvailableConnections() {
                return ApplicationSpektogram.this.getAvailableConnections(dcId);
            }

            @Override
            public KnownSalt[] readKnownSalts() {
                return knownSalts;
            }

            @Override
            protected void writeKnownSalts(KnownSalt[] salts) {
                knownSalts = salts;
            }
        };
    }

    @Override
    public void resetAuth() {
        isAuth.clear();
    }

    @Override
    public void reset() {
        isAuth.clear();
        keys.clear();
    }


    private void startTelegramApi() throws IOException {
//        AppInfo appInfo = new AppInfo(Constants.API_ID, "android", "1", "1", "en");
//        if(api == null) {
//            api = new TelegramApi(this, appInfo, this);
//        }
//
//        applicationContext = getApplicationContext();
//        String name = PhoneFormat.getInstance().format("+375293886590");
//
//        Log.v(null,"name" + name);
//
//        TLRequestAuthSendCode method = new TLRequestAuthSendCode(name, 0, 34993, "9866dc29b504cedb40a86bb03bbe8c93", "en");
////
//        TLSentCode doRpcCallNonAuth = api.doRpcCallNonAuth(method);
//        final String phoneCodeHash = doRpcCallNonAuth.getPhoneCodeHash();
//        Log.v(null,"reg" + doRpcCallNonAuth.getPhoneRegistered());
//        Log.v(null,"phoneCodeHash:" + phoneCodeHash);
//        PreferenceUtils.setPhoneCodeHash(this, phoneCodeHash);

//        TLRequestAuthSignUp signUp = new TLRequestAuthSignUp(name, PreferenceUtils.getPhoneCodeHash(this),"78730","Egor","Chebotarev");
//        Log.v(null,"hash" + PreferenceUtils.getPhoneCodeHash(this));
//        final TLAuthorization tlAuthorization = api.doRpcCallNonAuth(signUp);
//
//        final TLAbsUser user = tlAuthorization.getUser();//
//        Log.v(null,"user id " + user.getId());

        Client.ResultHandler handler = new Client.ResultHandler() {
            public void onResult(TdApi.TLObject object) {}
        };

        TG.setUpdatesHandler(handler);
        TG.setDir(Environment.getDataDirectory().getAbsolutePath()+"/tdb");

        Client client = TG.getClientInstance();
        TdApi.TLFunction func = new TdApi.AuthGetState();

    }

    public  TelegramApi getTelegramApi(){
        AppInfo appInfo = new AppInfo(Constants.API_ID, "android", "1", "1", "en");
        if(api == null){
            api = new TelegramApi(this, appInfo,this);
        }
        return api;
    }

    public void onUpdatesInvalidated(TelegramApi api) {
        // TODO Auto-generated method stub
        Log.v(null,"on onUpdatesInvalidated");
    }

    @Override
    public void onUpdate(TLAbsUpdates updates) {
        // TODO Auto-generated method stub
        Log.v(null, "onUpdate");

    }

    @Override
    public void onAuthCancelled(TelegramApi api) {
        // TODO Auto-generated method stub
        Log.v(null,"onAuthCancelled");
    }
    public static ApplicationSpektogram getApplication(Context context) {
        return (ApplicationSpektogram) context.getApplicationContext();
    }
}
