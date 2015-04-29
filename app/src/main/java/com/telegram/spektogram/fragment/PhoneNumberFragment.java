package com.telegram.spektogram.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.telegram.spektogram.R;
import com.telegram.spektogram.callback.NextPageCallback;


public class PhoneNumberFragment extends Fragment {

    private EditText phoneNumberField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_phone_number, container, false);
        phoneNumberField = (EditText) v.findViewById(R.id.phone_number_field);
        return v;
    }

    public void sendCode (View v) {
        String phoneNumber = phoneNumberField.getText().toString();

        ((NextPageCallback) getActivity()).nextPage();
    }

}
