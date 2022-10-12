package com.example.media.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SPUtils {

    private static final String                                       SP_NAME_DEFAULT = "sp_default";
    private static       SPUtils sInstance;
    private              SharedPreferences                            mSharedPreferences;

    public static SPUtils get(Context context) {
        if (sInstance == null) {
            synchronized (SPUtils.class) {
                if (sInstance == null) {
                    sInstance = new SPUtils(context);
                }
            }
        }
        return sInstance;
    }

    private SPUtils(Context context) {
        mSharedPreferences = mSharedPreferences = context.getSharedPreferences(SP_NAME_DEFAULT,
                Context.MODE_PRIVATE);
    }

    public void put(@NonNull final String key, final String value) {
        put(key, value, false);
    }

    public void put(@NonNull final String key, final String value, final boolean isCommit) {
        if (isCommit) {
            mSharedPreferences.edit().putString(key, value).commit();
        } else {
            mSharedPreferences.edit().putString(key, value).apply();
        }
    }

    public String getString(@NonNull final String key) {
        return getString(key, "");
    }

    public String getString(@NonNull final String key, final String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

}
