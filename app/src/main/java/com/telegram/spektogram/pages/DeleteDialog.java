package com.telegram.spektogram.pages;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.telegram.spektogram.R;


public class DeleteDialog extends DialogFragment implements OnClickListener {

    DialogExitListener dialogExitListener;
    String message;

    public void setListener(DialogExitListener exitListener) {
        dialogExitListener = exitListener;
    }

    public void setMessage(String message) {
        this.message = message;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.dialog_delete, null);
        ((TextView) v.findViewById(R.id.text_delete)).setText(message);
        v.setOnClickListener(this);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return v;
    }

    @Override
    public void onClick(View v) {
        dialogExitListener.exitTest();
        dismiss();


    }

}
