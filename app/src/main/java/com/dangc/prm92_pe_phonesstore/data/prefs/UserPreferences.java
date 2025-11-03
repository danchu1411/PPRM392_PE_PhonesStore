package com.dangc.prm92_pe_phonesstore.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String PREFS_NAME = "session_prefs";
    private static final String KEY_LOGGED_IN_USER_ID = "logged_in_user_id";
    private static final String KEY_REMEMBER_ME = "remember_me"; // Đảm bảo key này có
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
        editor.remove(KEY_REMEMBER_ME); // Xóa cả trạng thái remember me khi logout
        editor.apply();
    }

    public int getCurrentUserId() {
        // Chỉ trả về userId nếu "Remember Me" đang bật
        if (getRememberMeStatus()) {
            return sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, NO_USER_LOGGED_IN);
        }
        return NO_USER_LOGGED_IN; // Nếu Remember Me tắt, coi như không có ai đăng nhập
    }

    public void setRememberMeStatus(boolean rememberMe) { // Đảm bảo phương thức này có
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_REMEMBER_ME, rememberMe);
        editor.apply();
    }

    public boolean getRememberMeStatus() { // Đảm bảo phương thức này có
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false); // Mặc định là false
    }
}