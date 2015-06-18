package com.telegram.spektogram.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.telegram.spektogram.R;
import com.telegram.spektogram.activity.SignInActivity;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.callback.NextPageCallback;
import com.telegram.spektogram.phoneFormat.PhoneFormat;
import com.telegram.spektogram.views.NothingSelectedSpinnerAdapter;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class PhoneNumberFragment extends Fragment implements  AdapterView.OnItemSelectedListener {

    private Spinner countryCodeField;

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

        countryCodeField = (Spinner) v.findViewById(R.id.country_code_field);
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
        final SignInActivity activity = (SignInActivity) getActivity();
        final boolean networkConnected = activity.isNetworkConnected();
        if(!networkConnected){
            Toast.makeText(activity,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
            return;
        }

        final String phoneNumber =  phoneNumberField.getText().toString();

        final PhoneFormat instance = PhoneFormat.getInstance();
        final boolean phoneNumberValid = instance.isPhoneNumberValid(phoneNumber);

        if(!phoneNumberValid ){
            activity.showToast(getString(R.string.not_valid_phone));
            return;
        }

        application.sendFunction(new TdApi.AuthSetPhoneNumber(phoneNumber), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                Log.d("TAG", object.toString());

                if (object instanceof TdApi.Error) {
                    activity.showToast(getString(R.string.server_error));
                    return;
                }

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

    @Override
    public void onStop() {
        super.onStop();
        final Editable text = phoneNumberField.getText();
        final FragmentActivity activity = getActivity();
        if(activity != null) {
//            PreferenceUtils.setPhoneNumber(activity, text.toString());
        }
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
                StringBuilder builder = new StringBuilder();
                final String result = builder.append("(").append(elements[0]).append(")").append(" ").append(elements[2]).toString();
                countriesArray.add(result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity(), R.layout.country_spinner_item, countriesArray);
        adapter.setDropDownViewResource(R.layout.country_spinner_item_dropdown);

        countryCodeField.setOnItemSelectedListener(this);

        final FragmentActivity activity = getActivity();

        countryCodeField.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        activity));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        final String phoneNumber = phoneNumberField.getText().toString();
        final String selectedItem = (String) countryCodeField.getSelectedItem();

        if(selectedItem == null){
            return;
        }
        final String substring = selectedItem.substring(selectedItem.indexOf("(")+ 1,selectedItem.indexOf(")"));

        if(!TextUtils.isEmpty(phoneNumber)) {
            phoneNumberField.setText( "+" + phoneNumber + substring);
        }else{
            phoneNumberField.setText("+" + substring);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private static class CustomArrayAdapter extends ArrayAdapter<String> {

        private List<String> objects;
        private Context context;

        public CustomArrayAdapter(Context context, int resourceId,
                                  List<String> objects) {
            super(context, resourceId, objects);
            this.objects = objects;
            this.context = context;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView row = (TextView) inflater.inflate(R.layout.country_spinner_item, parent, false);
            final String text = objects.get(position);
            final int start = text.indexOf(")");
            final String substring = text.substring(start + 1 , text.length());
            row.setSingleLine(true);
            row.setText(substring);
            return row;
        }
    }
}
