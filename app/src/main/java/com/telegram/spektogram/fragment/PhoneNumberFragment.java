package com.telegram.spektogram.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.callback.NextPageCallback;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class PhoneNumberFragment extends Fragment {

    private AutoCompleteTextView countryCodeField;

    private EditText phoneNumberField;

    private ApplicationSpektogram application;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_phone_number, container, false);
        application = ApplicationSpektogram.getApplication(getActivity());

//      DEBUG
        application.sendFunction(new TdApi.AuthReset(), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                // DON'T REALLY CARE
            }
        });

        countryCodeField = (AutoCompleteTextView) v.findViewById(R.id.country_code_field);
        phoneNumberField = (EditText) v.findViewById(R.id.phone_number_field);
        initAutocomplete();

        v.findViewById(R.id.send_code_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPhoneNumber();
            }
        });
        return v;
    }

    private void setPhoneNumber () {
        String phoneNumber = countryCodeField.getText().toString() + phoneNumberField.getText().toString();
        application.sendFunction(new TdApi.AuthSetPhoneNumber(phoneNumber), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                Log.d("TAG", object.toString());
                if (object instanceof TdApi.AuthStateWaitSetCode) {
                    Handler mainHandler = new Handler(getActivity().getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ((NextPageCallback) getActivity()).nextPage();
                        }
                    });
                }
            }
        });
    }

    private void initAutocomplete() {
        ArrayList<String> countriesArray = new ArrayList<>();
        try {
            InputStream is = getActivity().getAssets().open("countries.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String rawCountries = new String(buffer);

            String[] countries = rawCountries.split("\n");
            for (String country : countries) {
                String[] elements = country.split(";");
                countriesArray.add(elements[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        countryCodeField.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, countriesArray));
    }
}
