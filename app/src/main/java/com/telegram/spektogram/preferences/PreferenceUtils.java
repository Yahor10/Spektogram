package com.telegram.spektogram.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ychabatarou on 28.04.2015.
 */
public class PreferenceUtils {

    public static void setUserFirstName(Context context,
                                 String userName) {
        SharedPreferences.Editor pEditor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        pEditor.putString(PreferenceKeys.USER_FIRST_NAME,
                userName);
        pEditor.commit();
    }

    public static String getUserFistName(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(
                PreferenceKeys.USER_FIRST_NAME, "user");
    }

    public static void setUserLastName(Context context,
                                        String userName) {
        SharedPreferences.Editor pEditor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        pEditor.putString(PreferenceKeys.USER_FIRST_NAME,
                userName);
        pEditor.commit();
    }

    public static String getUserLastName(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(
                PreferenceKeys.USER_FIRST_NAME, "user");
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


    public static void setPhoneNumber(Context context,
                                   String phone) {
        SharedPreferences.Editor pEditor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        pEditor.putString(PreferenceKeys.PHONE_NUMBER,
                phone);
        pEditor.commit();
    }

    public static String getPhoneNumber(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(
                PreferenceKeys.PHONE_NUMBER, "1");
    }

    public static boolean isOfflineMode(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(
                PreferenceKeys.OFFLINE_MODE, false);
    }

    public static void setTGinit(Context context,
                                   boolean init) {
        SharedPreferences.Editor pEditor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        pEditor.putBoolean(PreferenceKeys.TG_INIT,
                init);
        pEditor.commit();
    }

    public static boolean isTGinit(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(
                PreferenceKeys.TG_INIT, false);
    }

}
