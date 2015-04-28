package com.telegram.spektogram.application;


import android.content.Context;
import android.util.Log;

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

    private HashMap<Integer, Boolean> isAuth = new HashMap<Integer, Boolean>();
    private HashMap<Integer, byte[]> keys = new HashMap<Integer, byte[]>();
    private TelegramApi api;

    @Override
    public void onCreate() {
        super.onCreate();
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
        AppInfo appInfo = new AppInfo(Constants.API_ID, "android", "1", "1", "en");
        api = new TelegramApi(this, appInfo,this);
//        TLRequestAuthSendCode method = new TLRequestAuthSendCode("+375293886590", 0, 34993, "9866dc29b504cedb40a86bb03bbe8c93", "en");
//        TLSentCode doRpcCallNonAuth = api.doRpcCallNonAuth(method);
//
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
