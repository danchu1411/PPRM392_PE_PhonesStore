package com.dangc.prm92_pe_phonesstore.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.dangc.prm92_pe_phonesstore.data.dao.UserDao;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.prefs.UserPreferences; // Đảm bảo import này có

import java.util.concurrent.ExecutorService;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executorService;
    private final UserPreferences userPreferences; // Đảm bảo biến này có

    // SỬA CONSTRUCTOR ĐỂ KHỚP VỚI AUTHVIEWMODEL
    public UserRepository(Context context, UserDao userDao, ExecutorService executorService, UserPreferences userPreferences) {
        this.userDao = userDao;
        this.executorService = executorService;
        this.userPreferences = userPreferences;
    }

    public long register(User user) { // ĐẢM BẢO TRẢ VỀ long
        return userDao.insert(user);
    }

    public User login(String email, String password) {
        return userDao.findByEmailAndPassword(email, password);
    }

    public void updateUser(User user) {
        executorService.execute(() -> userDao.update(user));
    }

    public User findByEmail(String email) { // THÊM PHƯƠNG THỨC NÀY
        return userDao.findByEmail(email);
    }

    public User getUserByIdSync(int userId) { // THÊM PHƯƠNG THỨC NÀY
        return userDao.getUserByIdSync(userId);
    }

    // Quản lý session thông qua UserPreferences
    public void saveCurrentUser(int userId) {
        userPreferences.saveCurrentUser(userId);
    }

    public void clearCurrentUser() {
        userPreferences.clearCurrentUser();
    }

    public int getCurrentUserId() {
        return userPreferences.getCurrentUserId();
    }

    public boolean getRememberMeStatus() { // THÊM PHƯƠNG THỨC NÀY
        return userPreferences.getRememberMeStatus();
    }

    public void setRememberMeStatus(boolean rememberMe) { // THÊM PHƯƠNG THỨC NÀY
        userPreferences.setRememberMeStatus(rememberMe);
    }
}