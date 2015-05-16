package com.telegram.spektogram.application;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.telegram.spektogram.R;
import com.telegram.spektogram.activity.MessagesActivity;
import com.telegram.spektogram.notifications.NotificationUtils;
import com.telegram.spektogram.preferences.PreferenceUtils;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TG;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;

/**
 * Created by ychabatarou on 27.04.2015.
 */
public class ApplicationSpektogram extends android.app.Application implements Client.ResultHandler {

    private LruCache<String, Bitmap> mMemoryCache;

    private Client client;

    public static Context appContext;

    final static public String BROADCAST_UPDATE_USER_NUMBER = "BROADCAST_UPDATE_USER_NUMBER";
    final static public String BROADCAST_UPDATE_USER_PHOTO = "BROADCAST_UPDATE_USER_PHOTO";
    final static public String BROADCAST_UPDATE_USER_NAME = "BROADCAST_UPDATE_USER_NAME";
    final static public String BROADCAST_UPDATE_USER_STATUS = "BROADCAST_UPDATE_USER_STATUS";
    final static public String BROADCAST_UPDATE_FILE_PROGGRESS = "BROADCAST_UPDATE_FILE_PROGGRESS";
    final static public String BROADCAST_UPDATE_FILE_DOWNLOADED = "BROADCAST_UPDATE_FILE_DOWNLOADED";
    final static public String BROADCAST_UPDATE_NEW_MESSAGE = "BROADCAST_UPDATE_NEW_MESSAGE";

    final static public String BROADCAST_UPDATE_USER_TYPING = "BROADCAST_UPDATE_USER_TYPING";


    public static TdApi.Chat chat; // kostil'

    final static public String EXTRA_UPDATE_USER_ID = "EXTRA_UPDATE_USER_ID";
    final static public String EXTRA_UPDATE_FILE_ID = "EXTRA_UPDATE_FILE_ID";
    final static public String EXTRA_UPDATE_FILE_SIZE = "EXTRA_UPDATE_FILE_SIZE";

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        createLruCache();
        startTelegramApi();

    }

    private void createLruCache() {
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private void startTelegramApi() {
        Log.v(Constants.LOG_TAG, "init spektogram app...");

        File f = null;
        String path = "";
        try {
            final PackageManager packageManager = getPackageManager();
            f = new File(packageManager
                    .getPackageInfo(getPackageName(), 0)
                    .applicationInfo.dataDir + "/tdb/");
            if (!f.exists()) {
                f.mkdir();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (!PreferenceUtils.isTGinit(this)) {
            path = f.getAbsolutePath();
            TG.setUpdatesHandler(this);
            TG.setDir(path);
            PreferenceUtils.setTGinit(this, true);
        }

        if (client == null) {
            client = TG.getClientInstance();
        }
    }


    @Override
    public void onResult(TdApi.TLObject object) {
        Log.i(Constants.LOG_TAG, "ApplicationSpektogram onResult update:" + object);

        if (object instanceof TdApi.UpdateNewMessage) {
            TdApi.UpdateNewMessage newMessage = (TdApi.UpdateNewMessage) object;
//            updateNewMessage(newMessage);

            Intent intent = new Intent(BROADCAST_UPDATE_NEW_MESSAGE);
            intent.putExtra(MessagesActivity.KEY_EXTRA_CHAT_ID, newMessage.message.chatId);
            sendBroadcast(intent);

        } else if (object instanceof TdApi.UpdateUserAction) {

        } else if (object instanceof TdApi.UpdateUserStatus) {

        } else if (object instanceof TdApi.UpdateChatTitle) {

        } else if (object instanceof TdApi.UpdateDeleteMessages) {

        } else if (object instanceof TdApi.UpdateUserPhoneNumber) {
            TdApi.UpdateUserPhoneNumber number = (TdApi.UpdateUserPhoneNumber) object;
            updateUserNumber(number);
        } else if (object instanceof TdApi.UpdateUserName) {
            TdApi.UpdateUserName userName = (TdApi.UpdateUserName) object;
            updateUserName(userName);
        } else if (object instanceof TdApi.UpdateUserStatus) {
            TdApi.UpdateUserStatus status = (TdApi.UpdateUserStatus) object;
            updateUserStatus(status);
        } else if (object instanceof TdApi.UpdateNewAuthorization) {
            TdApi.UpdateNewAuthorization newAuthorization = (TdApi.UpdateNewAuthorization) object;
            updateNewAuth(newAuthorization);
        } else if (object instanceof TdApi.UpdateFile) {
            TdApi.UpdateFile file = (TdApi.UpdateFile) object;
            Log.v(Constants.LOG_TAG, "UpdateFile");
            sendBroadcast(new Intent(BROADCAST_UPDATE_FILE_DOWNLOADED));
        } else if (object instanceof TdApi.UpdateFileProgress) {
            Log.v(Constants.LOG_TAG, "UpdateFileProgress");
            TdApi.UpdateFileProgress progress = (TdApi.UpdateFileProgress) object;
            int id = progress.fileId;
            int percent = progress.ready / progress.size;
        } else if (object instanceof TdApi.UpdateUserPhoto) {
            TdApi.UpdateUserPhoto photo = (TdApi.UpdateUserPhoto) object;
            sendBroadcast(new Intent(BROADCAST_UPDATE_USER_PHOTO));
        }
    }

    private void updateUserNumber(final TdApi.UpdateUserPhoneNumber number) {
        final int userId = number.userId;

        sendFunction(new TdApi.GetMe(), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                TdApi.User user = (TdApi.User) object;
                if (user.id != userId) {
                    // change another user
                }
            }
        });

        sendBroadcast(new Intent(BROADCAST_UPDATE_USER_NUMBER));
    }


    private void updateFile(final TdApi.UpdateFile file) {
        file.toString();
    }

    private void updateUserName(final TdApi.UpdateUserName name) {
        final int userId = name.userId;

        sendFunction(new TdApi.GetMe(), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                TdApi.User user = (TdApi.User) object;
                if (user.id != userId) {
                    // change another user
                    final Intent intent = new Intent(BROADCAST_UPDATE_USER_NAME);
                    intent.putExtra(EXTRA_UPDATE_USER_ID, userId);
                    sendBroadcast(intent);
                }
            }
        });

    }

    private void updateUserStatus(final TdApi.UpdateUserStatus status) {
        final int userId = status.userId;

        sendFunction(new TdApi.GetMe(), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                TdApi.User user = (TdApi.User) object;
                if (user.id != userId) {
                    final Intent intent = new Intent(BROADCAST_UPDATE_USER_STATUS);
                    intent.putExtra(EXTRA_UPDATE_USER_ID, userId);
                    sendBroadcast(intent);
                }
            }
        });

    }

    private void updateNewMessage(TdApi.UpdateNewMessage newMessage) {
        final TdApi.Message message = newMessage.message;
        final int fromId = message.fromId;
        final TdApi.MessageContent content = message.message;

        if (content instanceof TdApi.MessageText) {
            updateNewMessageText(fromId, (TdApi.MessageText) content);
        } else if (content instanceof TdApi.MessagePhoto) {

        } else if (content instanceof TdApi.MessageDocument) {

        }
    }

    private void updateNewMessageText(int fromId, TdApi.MessageText content) {
        final TdApi.MessageText text = content;
        client.send(new TdApi.GetUser(fromId), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                TdApi.User user = (TdApi.User) object;
                String name = user.firstName;
                NotificationUtils.buildSimpleNotification(ApplicationSpektogram.this, name, text.text);
            }
        });
    }

    private void updateNewAuth(TdApi.UpdateNewAuthorization newAuthorization) {
        NotificationUtils.buildSimpleNotification(ApplicationSpektogram.this, getString(R.string.app_name), "Detected loggin from " + newAuthorization.device);
    }

    public static ApplicationSpektogram getApplication(Context context) {
        return (ApplicationSpektogram) context.getApplicationContext();
    }

    public Client getClient() {
        if (client == null) {
            try {
                File f = new File(getPackageManager()
                        .getPackageInfo(getPackageName(), 0)
                        .applicationInfo.dataDir + "/tdb/");
                if (f.exists()) {
                    final String absolutePath = f.getAbsolutePath();
                    TG.setDir(absolutePath);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            client = TG.getClientInstance();
        }
        return client;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void sendFunction(TdApi.TLFunction func, Client.ResultHandler handler) {
        if (!PreferenceUtils.isOfflineMode(this)) {
            client = getClient();
            client.send(func, handler);
        } else {
            // TODO send message about offline;
        }
    }

    public void sendChatMessageFunction(long chatId, TdApi.InputMessageContent inputMessageContent, Client.ResultHandler handler) {
        if (!PreferenceUtils.isOfflineMode(this)) {
//            client = getClient();
            final TdApi.SendMessage function = new TdApi.SendMessage(chatId, inputMessageContent);
            client.send(function, handler);
        } else {
            // TODO send message about offline;
        }
    }
}
