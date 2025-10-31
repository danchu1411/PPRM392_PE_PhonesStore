package com.dangc.prm92_pe_phonesstore;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.dao.UserDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.repository.UserRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class UserRepositoryTest {

    private AppDatabase db;
    private UserRepository userRepository;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        UserDao userDao = db.userDao();
        userRepository = new UserRepository(userDao);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void registerAndLoginUser() throws ExecutionException, InterruptedException {
        // Arrange
        User user = new User("Test User", "test@user.com", "password");

        // Act
        userRepository.register(user);
        Thread.sleep(500); // Wait for async operation
        Future<User> future = userRepository.login("test@user.com", "password");
        User loggedInUser = future.get();

        // Assert
        assertNotNull(loggedInUser);
        assertEquals("Test User", loggedInUser.getFullName());
    }
}