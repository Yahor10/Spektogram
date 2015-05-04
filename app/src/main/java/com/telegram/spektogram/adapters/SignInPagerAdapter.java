package com.telegram.spektogram.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.telegram.spektogram.fragment.CodeFragment;
import com.telegram.spektogram.fragment.PhoneNumberFragment;
import com.telegram.spektogram.fragment.UserInfoFragment;

public class SignInPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments = {
            new PhoneNumberFragment(),
            new CodeFragment(),
            new UserInfoFragment()
    };

    public SignInPagerAdapter(FragmentManager fm) {
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