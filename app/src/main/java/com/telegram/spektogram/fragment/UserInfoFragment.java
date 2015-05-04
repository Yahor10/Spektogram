package com.telegram.spektogram.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.telegram.spektogram.R;
import com.telegram.spektogram.application.ApplicationSpektogram;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class UserInfoFragment extends Fragment {

    private EditText firstNameField;

    private EditText lastNameField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_info, container, false);
        firstNameField = (EditText) v.findViewById(R.id.first_name_field);
        lastNameField = (EditText) v.findViewById(R.id.last_name_field);
        v.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setName();
            }
        });
        return v;
    }

    private void setName() {
        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        final ApplicationSpektogram application = ApplicationSpektogram.getApplication(getActivity());
        final TdApi.AuthSetName function = new TdApi.AuthSetName(firstName, lastName);
        application.sendFunction(function, new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                Log.e("TAG", object.toString());
                if (object instanceof TdApi.AuthStateOk) {
                    Toast.makeText(getActivity(), "Authorized", Toast.LENGTH_SHORT).show();
                    Handler mainHandler = new Handler(getActivity().getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().finish();
                        }
                    });
                }
            }
        });
    }

}
