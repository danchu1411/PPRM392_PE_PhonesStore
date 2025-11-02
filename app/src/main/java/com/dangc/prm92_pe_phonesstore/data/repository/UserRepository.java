package com.dangc.prm92_pe_phonesstore.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.dangc.prm92_pe_phonesstore.data.dao.UserDao;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.prefs.UserPreferences;

import java.util.concurrent.ExecutorService;

public class UserRepository {

    private final UserDao userDao;
    private final UserPreferences userPreferences;
    private final ExecutorService databaseWriteExecutor;

    public UserRepository(UserDao userDao, Context context, ExecutorService executorService) {
        this.userDao = userDao;
        this.userPreferences = new UserPreferences(context);
        this.databaseWriteExecutor = executorService;
    }

    // Database Operations
    public void register(User user) {
        databaseWriteExecutor.execute(() -> userDao.insert(user));
    }

    public User login(String email, String password) {
        return userDao.findByEmailAndPassword(email, password);
    }

    public void updateUser(User user) {
        databaseWriteExecutor.execute(() -> userDao.update(user));
    }
    
    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    // Session (SharedPreferences) Operations
    public void saveLoginSession(User user) {
        if (user != null) {
            userPreferences.saveCurrentUser(user.getUserId());
        }
    }

    public void clearLoginSession() {
        userPreferences.clearCurrentUser();
    }

    public int getCurrentUserId() {
        return userPreferences.getCurrentUserId();
    }
    
    public boolean isLoggedIn() {
        return getCurrentUserId() != UserPreferences.NO_USER_LOGGED_IN;
    }
}