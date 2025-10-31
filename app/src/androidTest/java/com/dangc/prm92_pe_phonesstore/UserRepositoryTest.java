package com.dangc.prm92_pe_phonesstore;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.dao.UserDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.repository.UserRepository;
import com.dangc.prm92_pe_phonesstore.data.prefs.UserPreferences;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class UserRepositoryTest {

    private AppDatabase db;
    private UserRepository userRepository;
    private Context context;

    @Before
    public void createDb() {
        context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        UserDao userDao = db.userDao();
        userRepository = new UserRepository(userDao, context);
    }

    @After
    public void closeDb() throws IOException {
        context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE).edit().clear().apply();
        db.close();
    }

    @Test
    public void loginAndManageSession() throws ExecutionException, InterruptedException {
        // Arrange: Đăng ký một user mới
        User user = new User("Test User", "test@user.com", "password");
        userRepository.register(user);
        Thread.sleep(500);

        // Act 1: Đăng nhập
        Future<User> future = userRepository.login("test@user.com", "password");
        User loggedInUser = future.get();
        assertNotNull(loggedInUser);
        // Room sẽ tự gán ID = 1 cho user đầu tiên
        loggedInUser.setUserId(1); 

        // Act 2: Lưu phiên đăng nhập
        userRepository.saveLoginSession(loggedInUser);
        
        // Assert 1: Kiểm tra phiên đăng nhập đã được lưu
        assertTrue(userRepository.isLoggedIn());
        assertEquals(1, userRepository.getCurrentUserId());

        // Act 3: Xóa phiên đăng nhập
        userRepository.clearLoginSession();
        
        // Assert 2: Kiểm tra phiên đăng nhập đã bị xóa
        assertFalse(userRepository.isLoggedIn());
        assertEquals(UserPreferences.NO_USER_LOGGED_IN, userRepository.getCurrentUserId());
    }
}