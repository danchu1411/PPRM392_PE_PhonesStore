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
        userRepository = new UserRepository(userDao, context, db.databaseWriteExecutor);
    }

    @After
    public void closeDb() throws IOException {
        context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE).edit().clear().apply();
        db.close();
    }

    @Test
    public void loginAndManageSession() {
        // Arrange
        User user = new User("Test User", "test@user.com", "password");
        db.userDao().insert(user); // Insert trực tiếp và đồng bộ

        // Act 1
        User loggedInUser = userRepository.login("test@user.com", "password");
        assertNotNull(loggedInUser);
        loggedInUser.setUserId(1); 

        // Act 2
        userRepository.saveLoginSession(loggedInUser);
        
        // Assert 1
        assertTrue(userRepository.isLoggedIn());
        assertEquals(1, userRepository.getCurrentUserId());

        // Act 3
        userRepository.clearLoginSession();
        
        // Assert 2
        assertFalse(userRepository.isLoggedIn());
        assertEquals(UserPreferences.NO_USER_LOGGED_IN, userRepository.getCurrentUserId());
    }
}