package com.telegram.spektogram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.telegram.spektogram.activity.ChatRoomActivity;
import com.telegram.spektogram.activity.SignInActivity;
import com.telegram.spektogram.preferences.PreferenceUtils;


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
