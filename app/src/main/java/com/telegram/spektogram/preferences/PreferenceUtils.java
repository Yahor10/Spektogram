package com.telegram.spektogram.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ychabatarou on 28.04.2015.
 */
public class PreferenceUtils {

    public static void setUserName(Context context,
                                 String userName) {
        SharedPreferences.Editor pEditor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        pEditor.putString(PreferenceKeys.USER_NAME,
                userName);
        pEditor.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(
                PreferenceKeys.USER_NAME, "user");
    }

}
