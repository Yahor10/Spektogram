package com.telegram.spektogram.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.telegram.spektogram.R;
import com.telegram.spektogram.adapters.SignInPagerAdapter;
import com.telegram.spektogram.callback.NextPageCallback;


public class SignInActivity extends ActionBarActivity implements NextPageCallback {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new SignInPagerAdapter(getSupportFragmentManager()));

    }

    @Override
    public void nextPage() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
    }

    public static Intent buildStartIntent(Context context){
        return new Intent(context, SignInActivity.class);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
