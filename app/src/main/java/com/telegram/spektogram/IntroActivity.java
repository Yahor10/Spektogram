package com.telegram.spektogram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.telegram.spektogram.activity.ChatRoomActivity;
import com.telegram.spektogram.activity.SignInActivity;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.db.SpectrDBHandler;
import com.telegram.spektogram.preferences.PreferenceUtils;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.Arrays;


public class IntroActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        if (handler == null) {
            handler = new Handler();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, SPLASH_TIME_OUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    Handler handler;
    final Runnable runnable = new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

        @Override
        public void run() {
            // This method will be executed once the timer is over
            // Start your app main activity
            final Context baseContext = getBaseContext();
            Intent i = null;
            if(!PreferenceUtils.isUserAuth(baseContext)) {
                i = SignInActivity.buildStartIntent(baseContext);
            }else{


                ApplicationSpektogram.getApplication(getBaseContext()).sendFunction(new TdApi.GetContacts(), new Client.ResultHandler() {
                    @Override
                    public void onResult(TdApi.TLObject object) {
                        Log.v("CONTACTS", "TLObject onResult GetChat:" + object.toString());
                        TdApi.User[] contacts = ((TdApi.Contacts)object).users;
                        ArrayList<TdApi.User> users = new ArrayList<TdApi.User>(Arrays.asList(contacts));
                        SpectrDBHandler spectrDBHandler = new SpectrDBHandler(getApplicationContext());
                        spectrDBHandler.addUsers(users);

                    }
                });



                i = ChatRoomActivity.buildStartIntent(baseContext);
            }
//            startActivity(SettingsActivity.buildStartIntent(baseContext));
            startActivity(i);
            // close this activity
            finish();
        }
    };
    private final static int SPLASH_TIME_OUT = 3000;
}
