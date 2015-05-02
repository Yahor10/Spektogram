package com.telegram.spektogram.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.telegram.spektogram.callback.NextPageCallback;
import com.telegram.spektogram.fragment.PhoneNumberFragment;
import com.telegram.spektogram.R;


public class SignUpActivity extends ActionBarActivity implements NextPageCallback {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new SignUpPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void nextPage() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
    }

    class SignUpPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments = {
                new PhoneNumberFragment(),
                new Fragment(),
                new Fragment(),
                new Fragment()
        };

        public SignUpPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments[i];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
