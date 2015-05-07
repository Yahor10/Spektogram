package com.telegram.spektogram.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.telegram.spektogram.R;
import com.telegram.spektogram.activity.ChatRoomActivity;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.callback.NextPageCallback;
import com.telegram.spektogram.preferences.PreferenceUtils;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class CodeFragment extends Fragment {

    private ApplicationSpektogram application;

    private EditText codeField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_code, container, false);
        application = ApplicationSpektogram.getApplication(getActivity());
        codeField = (EditText) v.findViewById(R.id.code_field);
        v.findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCode(codeField.getText().toString());
            }
        });

        getActivity().registerReceiver(new IncomingSMSReceiver(), new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        return v;
    }

    private void checkCode(String code) {
        final TdApi.AuthSetCode function = new TdApi.AuthSetCode(code);
        application.sendFunction(function, new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                Log.e("TAG", object.toString());
                checkState();
            }
        });
    }

    private void checkState() {
        final TdApi.AuthGetState function = new TdApi.AuthGetState();
        application.sendFunction(function, new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                Log.e("TAG", object.toString());
                if (object instanceof TdApi.AuthStateOk) {
                    Handler mainHandler = new Handler(getActivity().getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Authorized", Toast.LENGTH_SHORT).show();
                            PreferenceUtils.setUserAuth(getActivity(),true);

//                            ApplicationSpektogram.getApplication(getActivity().getBaseContext()).sendFunction(new TdApi.GetContacts(),new Client.ResultHandler() {
//                                @Override
//                                public void onResult(TdApi.TLObject object) {
//                                    Log.v("CONTACTS", "TLObject onResult GetChat:" + object.toString());
//                                }
//                            });

                            startActivity(ChatRoomActivity.buildStartIntent(getActivity()));
                        }
                    });
                    getActivity().finish();
                } else if (object instanceof TdApi.AuthStateWaitSetName) {
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

    class IncomingSMSReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();

            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (Object pduObject : pdusObj) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pduObject);

                        String message = currentMessage.getDisplayMessageBody();

                        if (message.contains("Telegram")) {
                            message = message.substring(message.length() - 5, message.length());
                            checkCode(message);
                        }
                    }
                }

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" +e);

            }
        }
    }


}
