package com.returnlive.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.returnlive.app.bean.User;

/**
 * 对用户信息本地保存
 */
public class SharePreferencesUtils {

    private static final String NAME = SharePreferencesUtils.class.getSimpleName();
    private static final String KEY_STATE = "state";
    private static final String KEY_UID = "uid";
    private static final String KEY_Z_SESSION_ID = "z_session_id";
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    private SharePreferencesUtils() {
    }

    //在Application中初始化
    @SuppressLint("CommitPrefEdits")
    public static void init(Context context) {
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static void clearAllData() {
        editor.clear();
        editor.apply();//editor.commit()一样
    }

    public static void setUser(User user) {
        editor.putString(KEY_STATE, user.getState());
        editor.putString(KEY_UID, user.getId());
        editor.putString(KEY_Z_SESSION_ID, user.getZ_session_id());
        editor.apply();
    }

    public static User getUser() {
        User user = new User();
        user.setState(preferences.getString(KEY_STATE, null));
        user.setId(preferences.getString(KEY_UID, null));
        user.setZ_session_id(preferences.getString(KEY_Z_SESSION_ID, null));
        return user;
    }

}
