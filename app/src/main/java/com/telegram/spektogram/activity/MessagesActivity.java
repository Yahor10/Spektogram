package com.telegram.spektogram.activity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.lamerman.FileDialog;
import com.telegram.spektogram.R;
import com.telegram.spektogram.adapters.MessagesAdapter;
import com.telegram.spektogram.application.ApplicationSpektogram;
import com.telegram.spektogram.application.Constants;
import com.telegram.spektogram.pages.DeleteDialog;
import com.telegram.spektogram.pages.DialogExitListener;
import com.telegram.spektogram.preferences.PreferenceUtils;
import com.telegram.spektogram.views.PopupMenu;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class MessagesActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, AdapterView.OnItemLongClickListener, PopupMenu.OnItemSelectedListener {

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

    public Uri uriPhoto;
    public static final int RESULT_CAMERA = 111;


    TdApi.Message longClickMessage = null;

    public int MaxIdMessage = 0;


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


        list = (ListView) findViewById(R.id.list_message);
        adapter = new MessagesAdapter(getLayoutInflater(), getBaseContext());
        adapter.setId_owner_user(PreferenceUtils.getMyUserId(this));


        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.empty_view_message));


        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                DeleteDialog deleteDialog = new DeleteDialog();
                deleteDialog.setMessage(" Удалить сообщение");


                MessagesAdapter.ViewHolder holder = (MessagesAdapter.ViewHolder) view.getTag(R.id.TAG_HOLDER_VIEW);
                longClickMessage = holder.message;

                deleteDialog.setListener(new DialogExitListener() {
                    @Override
                    public void exitTest() {
                        Toast.makeText(MessagesActivity.this, "Message delete id = " + longClickMessage.id, Toast.LENGTH_SHORT).show();
                        int[] id_delete_messages = new int[1];
                        id_delete_messages[0] = longClickMessage.id;
                        ApplicationSpektogram.getApplication(getBaseContext()).sendFunction(new TdApi.DeleteMessages(longClickMessage.chatId, id_delete_messages), new Client.ResultHandler() {

                            @Override
                            public void onResult(TdApi.TLObject object) {


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.removeMessageById(longClickMessage.id);
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                            }
                        });
                    }
                });

                deleteDialog.show(getFragmentManager(), "");
                return true;
            }
        });


        chat = ApplicationSpektogram.chat;

        loadMessages(chat, false);


        messageText = (EditText) findViewById(R.id.message);
        messageText.addTextChangedListener(textWatcher);

        send = (ImageView) findViewById(R.id.send);
        attach = (ImageView) findViewById(R.id.attach);
        send.setVisibility(View.GONE);

        send.setOnClickListener(MessagesActivity.this);
        attach.setOnClickListener(MessagesActivity.this);

        restoreActionBar();
        buildGoogleApiClient();


//        list.setOnItemLongClickListener(this);
//        list.setMultiChoiceModeListener();

    }


    public void loadMessages(final TdApi.Chat chat_for_load, final boolean flag_new_message) {
        if (chat_for_load != null) {

//            if (!flag_new_message){
            messages = new ArrayList<TdApi.Message>();
//            }else{
//                if(messages==null){
//                    messages = new ArrayList<TdApi.Message>();
//                }
//            }


            id_users = new ArrayList<Integer>();

            if (chat_for_load.type instanceof TdApi.PrivateChatInfo) {
                id_users.add(((TdApi.PrivateChatInfo) chat_for_load.type).user.id);
                getMessagesByIdUsers(id_users, chat_for_load.id, flag_new_message);

            } else if (chat_for_load.type instanceof TdApi.GroupChatInfo) {
                ApplicationSpektogram.getApplication(getBaseContext()).sendFunction(new TdApi.GetGroupChatFull(((TdApi.GroupChatInfo) chat_for_load.type).groupChat.id), new Client.ResultHandler() {

                    @Override
                    public void onResult(TdApi.TLObject object) {

                        TdApi.GroupChatFull chatFull = (TdApi.GroupChatFull) object;
                        id_users.add(chatFull.participants[0].user.id);
                        getMessagesByIdUsers(id_users, chat.id, flag_new_message);

                    }
                });
            }
        }
    }


    public void getMessagesByIdUsers(ArrayList<Integer> users, long chat_id, final boolean flag_new_message) {

        int count_load_message = 1;
        if (!flag_new_message) {
            count_load_message = 100;
        }

        ApplicationSpektogram.getApplication(getBaseContext()).sendFunction(new TdApi.GetChatHistory(chat_id, users.get(0), 0, count_load_message), new Client.ResultHandler() {

            @Override
            public void onResult(TdApi.TLObject object) {
                TdApi.Messages mes = (TdApi.Messages) object;

                if (mes != null && mes.messages != null) {
                    messages.addAll(Arrays.asList(mes.messages));


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (flag_new_message) {
                                adapter.addMessages(messages);
                            } else {
                                adapter.setMessages(messages);
                            }


                            adapter.notifyDataSetChanged();


                        }
                    });

                    for (TdApi.Message m : mes.messages) {
                        if (m.id > MaxIdMessage) {
                            MaxIdMessage = m.id;
                        }
                    }
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

    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setTitle("Maria One Two");
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        final ColorDrawable drawable = new ColorDrawable(getResources().getColor(R.color.transparent_half));
        getSupportActionBar().setBackgroundDrawable(drawable);
        actionBar.setCustomView(R.layout.ab_single_chat);
        final View customView = actionBar.getCustomView();
        TextView chatName = (TextView) customView.findViewById(R.id.chatName);
        TextView chatStatus = (TextView) customView.findViewById(R.id.chatStatus);

        chatStatus.setVisibility(View.GONE);

        if (chat.type instanceof TdApi.GroupChatInfo) {
            chatName.setText(((TdApi.GroupChatInfo) chat.type).groupChat.title);
        } else if (chat.type instanceof TdApi.PrivateChatInfo) {
            chatName.setText(((TdApi.PrivateChatInfo) chat.type).user.firstName);
        }


    }


    private final BroadcastReceiver updateNewMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            long chat_id = intent.getLongExtra(KEY_EXTRA_CHAT_ID, 0);

            if (chat_id == chat.id) {
                loadMessages(chat, true);
            } else {

            }
        }
    };

    private final BroadcastReceiver updateFileDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
//            loadMessages(chat, false);
            int file_id = intent.getIntExtra(ApplicationSpektogram.KEY_UPDATE_FILE_ID, 0);
            TdApi.Message message = adapter.findMessageWithFileId(file_id);
            if (message != null) {
                ApplicationSpektogram.getApplication(getBaseContext()).sendFunction(new TdApi.GetChatHistory(chat.id, message.fromId, MaxIdMessage - message.id, 1), new Client.ResultHandler() {

                    @Override
                    public void onResult(TdApi.TLObject object) {
                        TdApi.Messages mes = (TdApi.Messages) object;

                        if (mes != null && mes.messages != null) {
                            final TdApi.Message m = mes.messages[0];


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    View v = list.findViewWithTag(m.id);
                                    if (v != null) {
                                        MessagesAdapter.ViewHolder holder = (MessagesAdapter.ViewHolder) v.getTag(R.id.TAG_HOLDER_VIEW);
                                        if (holder != null) {
                                            holder.setData(m);
                                        }
                                    }

                                    adapter.replaceMessage(m);
//                                    int index = list.getFirstVisiblePosition();
//                                    View up = list.getChildAt(0);
//                                    int top = (up == null) ? 0 : up.getTop();
                                    adapter.notifyDataSetChanged();
//                                    list.setSelectionFromTop(index, top);
                                }
                            });

                        }


                    }
                });
            }

        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

        registerReceiver(updateNewMessageReceiver, new IntentFilter(ApplicationSpektogram.BROADCAST_UPDATE_NEW_MESSAGE));
        registerReceiver(updateFileDownloadReceiver, new IntentFilter(ApplicationSpektogram.BROADCAST_UPDATE_FILE_DOWNLOADED));
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();

        try {
            unregisterReceiver(updateNewMessageReceiver);
            unregisterReceiver(updateFileDownloadReceiver);

        } catch (Exception e) {
        }


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

        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.clear_history) {
            ApplicationSpektogram.getApplication(this).sendFunction(new TdApi.DeleteChatHistory(-1), new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.TLObject object) {

                }
            });
        }

        if (id == R.id.delete_messages) {
            int[] arr = null;
            final TdApi.DeleteMessages func = new TdApi.DeleteMessages(-1, arr);
            ApplicationSpektogram.getApplication(this).sendFunction(func, new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.TLObject object) {

                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

//    public void addSendingMessageToListView(TdApi.InputMessageText inputMessageText) {
//        TdApi.Message message = new TdApi.Message();
//        message.fromId = PreferenceUtils.getMyUserId(getApplicationContext());
//        message.date = (int) System.currentTimeMillis();
//        message.chatId = chat.id;
//        message.message = new TdApi.MessageText();
//        ((TdApi.MessageText) message.message).text = inputMessageText.text;
//
//        adapter.addMessage(message);
//        adapter.notifyDataSetChanged();
//
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                final TdApi.InputMessageText inputMessageText = new TdApi.InputMessageText(messageText.getText().toString());
                messageText.setText("");

                ApplicationSpektogram.getApplication(this).sendChatMessageFunction(chat.id, inputMessageText, new Client.ResultHandler() {
                    @Override
                    public void onResult(TdApi.TLObject object) {
                    }
                });
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
//                startCameraActivityPhoto();
                getPhotoFromCamera();
                break;
            case SEND_VIDEO:
//                startCameraActivityVideo();

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

//    private void startCameraActivityPhoto() {
//        File root = new File(Environment.getExternalStorageDirectory()
//
//                + File.separator + "Spektogram" + File.separator);
//        root.mkdirs();
//
//
//        File sdImageMainDirectory = new File(root, "myPicName.jpg");
//
//
//        Log.d(TAG, "fileName = " + sdImageMainDirectory);
//
//        Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);
//        attachImageUri = outputFileUri;
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        startActivityForResult(cameraIntent, SEND_PHOTO);
//    }

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

    public void getPhotoFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        uriPhoto = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto);
        startActivityForResult(intent, RESULT_CAMERA);
    }


    private void startFileActivity() {
        Intent intent = new Intent(getBaseContext(), FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, "/sdcard");

        //can user select directories or not
        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

        //alternatively you can set file filter
        //intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

        startActivityForResult(intent, SEND_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CAMERA) {
            if (resultCode == RESULT_OK) {
                String picturePath = "";
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uriPhoto, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

                if(!"".equals(picturePath)){

                    final TdApi.InputMessagePhoto inputMessagePhoto = new TdApi.InputMessagePhoto(picturePath);
                    messageText.setText("");

                    ApplicationSpektogram.getApplication(this).sendChatMessageFunction(chat.id, inputMessagePhoto, new Client.ResultHandler() {
                        @Override
                        public void onResult(TdApi.TLObject object) {
                            object.toString();
                        }
                    });
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

        if (requestCode == SEND_FILE) {
            String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
            Log.v(Constants.LOG_TAG, "file " + filePath);
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


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        startActionMode(multi);

        return false;
    }

    private AbsListView.MultiChoiceModeListener multi = new AbsListView.MultiChoiceModeListener() {

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked) {
            // Here you can do something when items are selected/de-selected,
            // such as update the title in the CAB
            Log.v(Constants.LOG_TAG, "checked");
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // Respond to clicks on the actions in the CAB
            switch (item.getItemId()) {

                default:
                    return false;
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate the menu for the CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.chat_long_click, menu);

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // Here you can make any necessary updates to the activity when
            // the CAB is removed. By default, selected items are deselected/unchecked.
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // Here you can perform updates to the CAB due to
            // an invalidate() request
            return false;
        }

    };

}