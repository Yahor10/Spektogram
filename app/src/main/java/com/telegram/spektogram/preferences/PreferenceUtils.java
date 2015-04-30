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

    public static void setPhoneCodeHash(Context context,
                                   String code) {
        SharedPreferences.Editor pEditor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        pEditor.putString(PreferenceKeys.PHONE_CODE_HASH,
                code);
        pEditor.commit();
    }

    public static String getPhoneCodeHash(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(
                PreferenceKeys.PHONE_CODE_HASH, "user");
    }


    public static void setUserAuth(Context context,
                                        boolean auth) {
        SharedPreferences.Editor pEditor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        pEditor.putBoolean(PreferenceKeys.AUTH,
                auth);
        pEditor.commit();
    }

    public static boolean isUserAuth(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(
                PreferenceKeys.AUTH, false);
    }

}
