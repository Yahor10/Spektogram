package com.telegram.spektogram.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.telegram.spektogram.R;

public  class NotificationUtils {


    public static void buildSimpleNotification(Context context,String title,String text){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_drawer)
                        .setContentTitle(title)
                        .setContentText(text);
        final Notification build = mBuilder.build();
        NotificationManager mNotifyMgr =
                (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        int mNotificationId = 001;
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
