package com.telegram.spektogram;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import telegram.Client;
import telegram.TG;
import telegram.TdApi;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TG.setDir(Environment.getExternalStorageDirectory() + "/" + "Test3");
        TG.setUpdatesHandler(new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {

            }
        });
        final Client clientInstance = TG.getClientInstance();

        clientInstance.send(new TdApi.AuthSetCode("1231"),new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                Log.v(null,"auth result");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
