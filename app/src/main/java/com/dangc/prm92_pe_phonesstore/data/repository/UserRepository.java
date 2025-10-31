package com.dangc.prm92_pe_phonesstore.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.dangc.prm92_pe_phonesstore.data.dao.UserDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.prefs.UserPreferences;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class UserRepository {

    private final UserDao userDao;
    private final UserPreferences userPreferences;
    private final ExecutorService databaseWriteExecutor;

    public UserRepository(UserDao userDao, Context context) {
        this.userDao = userDao;
        this.userPreferences = new UserPreferences(context);
        this.databaseWriteExecutor = AppDatabase.databaseWriteExecutor;
    }

    // --- Database Operations ---
    public void register(User user) {
        databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    public Future<User> login(String email, String password) {
        Callable<User> callable = () -> userDao.findByEmailAndPassword(email, password);
        return databaseWriteExecutor.submit(callable);
    }
    
    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }


    // --- Session (SharedPreferences) Operations ---
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