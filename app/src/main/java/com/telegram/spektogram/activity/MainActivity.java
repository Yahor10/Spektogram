package com.telegram.spektogram.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.telegram.spektogram.R;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.views.PopupMenu;

import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, PopupMenu.OnItemSelectedListener {

    private static final int SEND_PHOTO = 111;



    private static final int SEND_VIDEO = 113;

    private static final int SEND_GEO_LOCATION = 114;

    private static final int SEND_FILE = 115;


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s)) {
                send.setVisibility(View.GONE);
                attach.setVisibility(View.VISIBLE);
            } else {
                send.setVisibility(View.VISIBLE);
                attach.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    private ImageView send;
    private ImageView attach;

    public static Intent buildStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    private EditText messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startActivity(SignInActivity.buildStartIntent(this));

        getSupportActionBar().setTitle("");
        messageText = (EditText) findViewById(R.id.message);
        messageText.addTextChangedListener(textWatcher);

        send = (ImageView) findViewById(R.id.send);
        attach = (ImageView) findViewById(R.id.attach);
        send.setVisibility(View.GONE);

        send.setOnClickListener(this);
        attach.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_char_rooms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                final TdApi.InputMessageText inputMessageText = new TdApi.InputMessageText(messageText.getText().toString());
                ApplicationSpektogram.getApplication(this).sendChatMessageFunction(-1, inputMessageText, null);
                break;
            case R.id.attach:
                PopupMenu menu = new PopupMenu(this);
                Resources resources = getResources();

                menu.setHeaderTitle(resources.getString(R.string.message));

                menu.add(SEND_PHOTO, R.string.take_picture).setIcon(
                        resources.getDrawable(R.drawable.ic_attach_photo));
                menu.add(SEND_VIDEO, R.string.send_video).setIcon(
                        resources.getDrawable(R.drawable.ic_attach_photo));
                menu.add(SEND_FILE, R.string.send_file).setIcon(
                        resources.getDrawable(R.drawable.ic_attach));
                menu.add(SEND_GEO_LOCATION, R.string.send_location).setIcon(
                        resources.getDrawable(R.drawable.ic_drawer));
                menu.show();

                menu.setOnItemSelectedListener(this);
                break;
        }
    }

    @Override
    public void onItemSelected(com.telegram.spektogram.views.MenuItem item) {

        switch (item.getItemId()) {
            case SEND_PHOTO:
                startCameraActivity();
                break;
        }
    }

    private void startCameraActivity() {
        File root = new File(Environment.getExternalStorageDirectory()
                + File.separator + "Your Floder Name"+ File.separator);
        root.mkdirs();
        File sdImageMainDirectory = new File(root, "myPicName.jpg");
        Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, SEND_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == -1) {
            try {
                Uri outputFileUri = data.getData();
//                selectedImagePath = getPath(outputFileUri);
            } catch (Exception ex) {
                Log.v("OnCameraCallBack", ex.getMessage());

            }
        }
    }
}
