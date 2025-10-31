package com.dangc.prm92_pe_phonesstore.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String PREFS_NAME = "session_prefs";
    private static final String KEY_LOGGED_IN_USER_ID = "logged_in_user_id";
    public static final int NO_USER_LOGGED_IN = -1;

    private final SharedPreferences sharedPreferences;

    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveCurrentUser(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_LOGGED_IN_USER_ID, userId);
        editor.apply();
    }

    public void clearCurrentUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_LOGGED_IN_USER_ID);
        editor.apply();
    }

    public int getCurrentUserId() {
        return sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, NO_USER_LOGGED_IN);
    }
}