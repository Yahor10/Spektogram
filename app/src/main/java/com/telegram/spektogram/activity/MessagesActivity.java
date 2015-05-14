package com.telegram.spektogram.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
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
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.lamerman.FileDialog;
import com.telegram.spektogram.R;
import com.telegram.spektogram.adapters.MessagesAdapter;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.preferences.PreferenceUtils;
import com.telegram.spektogram.views.PopupMenu;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class MessagesActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, PopupMenu.OnItemSelectedListener {

    private static final int SEND_PHOTO = 111;
    private static final int SEND_VIDEO = 113;
    private static final int SEND_GEO_LOCATION = 114;
    private static final int SEND_FILE = 115;

    public static final String KEY_EXTRA_CHAT_ID = "key_chat";

    private static Bitmap attach_Image;

    private ImageView ivPhoto;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    MessagesAdapter adapter;
    ListView list;

    ArrayList<TdApi.Message> messages;
    ArrayList<Integer> id_users = new ArrayList<Integer>();
    TdApi.Chat chat = null;


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
        return new Intent(context, MessagesActivity.class);
    }

    private EditText messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startActivity(SignInActivity.buildStartIntent(this));

        messages = new ArrayList<TdApi.Message>();
        list = (ListView) findViewById(R.id.list_message);
        adapter = new MessagesAdapter(getLayoutInflater(), getBaseContext());
        adapter.setId_owner_user(PreferenceUtils.getMyUserId(this));


        list.setAdapter(adapter);

        chat = ApplicationSpektogram.chat;

        if (chat != null) {


            if (chat.type instanceof TdApi.PrivateChatInfo) {
                id_users.add(((TdApi.PrivateChatInfo) chat.type).user.id);
//                id_users.add(PreferenceUtils.getMyUserId(getApplicationContext()));
                getMessagesByIdUsers(id_users, chat.id);

            } else if (chat.type instanceof TdApi.GroupChatInfo) {
                ApplicationSpektogram.getApplication(getBaseContext()).sendFunction(new TdApi.GetGroupChatFull(((TdApi.GroupChatInfo) chat.type).groupChat.id), new Client.ResultHandler() {

                    @Override
                    public void onResult(TdApi.TLObject object) {

                        TdApi.GroupChatFull chatFull = (TdApi.GroupChatFull) object;


//                        for (TdApi.ChatParticipant participant : chatFull.participants) {
                        id_users.add(chatFull.participants[0].user.id);
//                        }
                        getMessagesByIdUsers(id_users, chat.id);


                    }
                });
            }

            getSupportActionBar().setTitle("");
            messageText = (EditText) findViewById(R.id.message);
            messageText.addTextChangedListener(textWatcher);

            send = (ImageView) findViewById(R.id.send);
            attach = (ImageView) findViewById(R.id.attach);
            send.setVisibility(View.GONE);

            send.setOnClickListener(MessagesActivity.this);
            attach.setOnClickListener(MessagesActivity.this);

            buildGoogleApiClient();

        }

    }


    public void getMessagesByIdUsers(ArrayList<Integer> users, long chat_id) {

        for (int id : id_users)
            ApplicationSpektogram.getApplication(getBaseContext()).sendFunction(new TdApi.GetChatHistory(chat_id, id, 0, 50), new Client.ResultHandler() {

                @Override
                public void onResult(TdApi.TLObject object) {
                    TdApi.Messages mes = (TdApi.Messages) object;

                    if (mes != null && mes.messages != null) {
                        messages.addAll(Arrays.asList(mes.messages));


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setMessages(messages);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            //            Log.d(TAG, Double.toString(latitude));
            //            Log.d(TAG, Double.toString(longitude));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient connection has failed");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

                ApplicationSpektogram.getApplication(this).sendChatMessageFunction(chat.id, inputMessageText, null);
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
                startCameraActivityPhoto();
                break;
            case SEND_VIDEO:
                startCameraActivityVideo();
                break;
            case SEND_FILE:
                startFileActivity();
                break;
            case SEND_GEO_LOCATION:
                getGeoLocation();
                break;
        }
    }

    String TAG = "debug: ";
    Uri attachImageUri;
    Uri attachVideoUri;

    private void startCameraActivityPhoto() {
        File root = new File(Environment.getExternalStorageDirectory()

                + File.separator + "Spektogram" + File.separator);
        root.mkdirs();


        File sdImageMainDirectory = new File(root, "myPicName.jpg");


        Log.d(TAG, "fileName = " + sdImageMainDirectory);

        Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);
        attachImageUri = outputFileUri;
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, SEND_PHOTO);
    }

    private void startCameraActivityVideo() {
        File root = new File(Environment.getExternalStorageDirectory()
                + File.separator + "Spektogram" + File.separator);
        root.mkdirs();

        File sdImageMainDirectory = new File(root, "myVideoName.mp4");

        Log.d(TAG, "fileName = " + sdImageMainDirectory);

        Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);
        attachVideoUri = outputFileUri;
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, SEND_VIDEO);
    }


    private void startFileActivity() {
        Intent intent = new Intent(getBaseContext(), FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, "/sdcard");

        //can user select directories or not
        intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

        //alternatively you can set file filter
        //intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

        startActivityForResult(intent, SEND_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEND_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Log.d(TAG, "Intent is null");
                } else {
                    Log.d(TAG, "Photo uri: " + data.getData());
                    Bundle bndl = data.getExtras();
                    if (bndl != null) {
                        Object obj = data.getExtras().get("data");
                        if (obj instanceof Bitmap) {
                            Bitmap bitmap = (Bitmap) obj;
                            Log.d(TAG, "bitmap " + bitmap.getWidth() + " x "
                                    + bitmap.getHeight());
                            ivPhoto.setImageBitmap(bitmap);
                        }
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }
        }

        if (requestCode == SEND_VIDEO) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Log.d(TAG, "Intent is null");
                } else {
                    Log.d(TAG, "Video uri: " + data.getData());
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }
        }
    }

    //user location
    double latitude = 0;
    double longitude = 0;

    private void getGeoLocation() {

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }

        Log.d(TAG, Double.toString(latitude));
        Log.d(TAG, Double.toString(longitude));

    }


}
