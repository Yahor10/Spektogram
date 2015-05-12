package com.telegram.spektogram.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.HorizontalScrollView;

import com.telegram.spektogram.adapters.SignInPagerAdapter;
import com.telegram.spektogram.callback.NextPageCallback;
import com.telegram.spektogram.fragment.CodeFragment;
import com.telegram.spektogram.fragment.PhoneNumberFragment;
import com.telegram.spektogram.R;


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
}
