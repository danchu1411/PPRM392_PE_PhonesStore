package com.dangc.prm92_pe_phonesstore;

import android.app.Application;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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
        Application application = ApplicationProvider.getApplicationContext();
        // Sử dụng db giả lập trong bộ nhớ
        db = Room.inMemoryDatabaseBuilder(application, AppDatabase.class)
                .allowMainThreadQueries() // Cho phép query trên main thread (chỉ cho test)
                .build();
        userRepository = new UserRepository(application);
    }

    @After
    public void closeDb() throws IOException {
        // Cần phải đóng db được tạo bởi AppDatabase.getDatabase()
        AppDatabase.getDatabase(ApplicationProvider.getApplicationContext()).close();
        db.close();
    }

    @Test
    public void registerAndLoginUser() throws ExecutionException, InterruptedException {
        // Arrange: Chuẩn bị user
        User user = new User("Test User", "test@user.com", "password");

        // Act 1: Đăng ký user. Đây là tác vụ bất đồng bộ "fire-and-forget"
        userRepository.register(user);

        // Chờ một chút để đảm bảo tác vụ ghi đã hoàn thành
        Thread.sleep(1000); 

        // Act 2: Đăng nhập và lấy Future object
        Future<User> future = userRepository.login("test@user.com", "password");

        // Lấy kết quả từ Future. Lệnh .get() sẽ block cho đến khi có kết quả.
        User loggedInUser = future.get();

        // Assert: Kiểm tra
        assertNotNull(loggedInUser);
        assertEquals("Test User", loggedInUser.getFullName());
    }
}