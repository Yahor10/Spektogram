package com.telegram.spektogram;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TG;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startActivity(SettingsActivity.buildStartIntent(this));

        Client.ResultHandler handler = new Client.ResultHandler() {
            public void onResult(TdApi.TLObject object) {}
        };

        TG.setUpdatesHandler(handler);
        File f = new File (Environment.getDataDirectory().getAbsolutePath() + "/tdb/");
        f.mkdir();
        TG.setDir(Environment.getDataDirectory().getAbsolutePath() + "/tdb/");

        Client client = TG.getClientInstance();
//        TdApi.TLFunction func = new TdApi.AuthGetState();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_char_rooms, menu);
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
