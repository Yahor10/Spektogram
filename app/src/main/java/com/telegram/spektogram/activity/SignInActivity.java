package com.telegram.spektogram.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.telegram.spektogram.R;


public class SignInActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    public void login (View v) {

    }

    public void signUp (View v) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
