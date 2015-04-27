package com.telegram.spektogram.application;


import org.telegram.api.TLConfig;
import org.telegram.api.TLDcOption;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.mtproto.state.AbsMTProtoState;
import org.telegram.mtproto.state.ConnectionInfo;
import org.telegram.mtproto.state.KnownSalt;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ychabatarou on 27.04.2015.
 */
public class Application extends android.app.Application implements AbsApiState {

    private int primerId = 1;
    private static HashMap<Integer, org.telegram.mtproto.state.ConnectionInfo[]> connections = new HashMap<Integer, ConnectionInfo[]>();

    private HashMap<Integer, Boolean> isAuth = new HashMap<Integer, Boolean>();
    private HashMap<Integer, byte[]> keys = new HashMap<Integer, byte[]>();

    @Override
    public void onCreate() {
        super.onCreate();

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
                return Application.this.getAuthKey(dcId);
            }

            @Override
            public ConnectionInfo[] getAvailableConnections() {
                return Application.this.getAvailableConnections(dcId);
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
}
