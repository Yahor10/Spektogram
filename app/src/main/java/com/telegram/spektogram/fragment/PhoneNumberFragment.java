package com.telegram.spektogram.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.telegram.spektogram.R;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.callback.NextPageCallback;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;


public class PhoneNumberFragment extends Fragment implements Client.ResultHandler, View.OnClickListener {

    private EditText phoneNumberField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_phone_number, container, false);
        phoneNumberField = (EditText) v.findViewById(R.id.phone_number_field);
        v.findViewById(R.id.send_code_button).setOnClickListener(this);
        return v;
    }

    public void sendCode () {
        String phoneNumber = phoneNumberField.getText().toString();
        ((NextPageCallback) getActivity()).nextPage();
        final ApplicationSpektogram application = ApplicationSpektogram.getApplication(getActivity());
//        final TdApi.AuthSetPhoneNumber function = new TdApi.AuthSetPhoneNumber("+375293886590");
//        final TdApi.AuthGetState function = new TdApi.AuthGetState();
//        final TdApi.AuthSetCode function = new TdApi.AuthSetCode("73833");
//        final TdApi.AuthSetName function = new TdApi.AuthSetName("Egor","Chebotarev");
//        application.sendFunction(function,this);

//        startActivity(ChatRoomActivity.buildStartIntent(getActivity()));
    }

    @Override
    public void onResult(TdApi.TLObject object) {
        Log.v(null,"On result:" + object.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_code_button:
                sendCode();
                break;
        }
    }

}
