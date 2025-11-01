package com.dangc.prm92_pe_phonesstore;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.repository.UserRepository;
import com.dangc.prm92_pe_phonesstore.util.LiveDataTestUtil;
import com.dangc.prm92_pe_phonesstore.viewmodel.AuthViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AuthViewModelComponentTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private AuthViewModel authViewModel;

    @Before
    public void createDb() {
        Application application = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(application, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        
        UserRepository userRepository = new UserRepository(db.userDao(), application, db.databaseWriteExecutor);
        authViewModel = new AuthViewModel(application, userRepository, db.databaseWriteExecutor);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void login_success_whenUserExists() throws InterruptedException {
        // Arrange
        User user = new User("Test User", "test@example.com", "password");
        long userId = db.userDao().insert(user);
        assertTrue(userId > 0);

        // Act
        authViewModel.login("test@example.com", "password", false);
        
        // Assert: LiveData bây giờ sẽ được cập nhật một cách đáng tin cậy
        User loggedInUser = LiveDataTestUtil.getOrAwaitValue(authViewModel.loggedInUser);
        assertNotNull(loggedInUser);
        assertEquals("Test User", loggedInUser.getFullName());
    }
}