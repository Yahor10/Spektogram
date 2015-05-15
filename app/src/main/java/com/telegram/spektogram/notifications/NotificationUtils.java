package com.telegram.spektogram.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.telegram.spektogram.R;
import com.telegram.spektogram.activity.ChatRoomActivity;
import com.telegram.spektogram.application.Constants;
import com.telegram.spektogram.preferences.PreferenceUtils;


public  class NotificationUtils {


    public static void buildSimpleNotification(Context context,String title,String text){
        Intent intent = new Intent(context, ChatRoomActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        final Drawable drawable = context.getDrawable(R.mipmap.ic_launcher);
        BitmapDrawable bitmapDrawable= (BitmapDrawable) drawable;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setFullScreenIntent(pi,true)
                        .setShowWhen(true)
                        .setLargeIcon(bitmapDrawable.getBitmap())
                        .setContentTitle(title)
                        .setContentText(text).setContentIntent(pi);

        if(PreferenceUtils.isVibrate(context)) {
            mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }

        final Notification build = mBuilder.build();

        NotificationManager mNotifyMgr =
                (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        int mNotificationId = 1232132;
        mNotifyMgr.notify(mNotificationId, build);
        Log.v(Constants.LOG_TAG, "buildSimpleNotification ");
    }
}
