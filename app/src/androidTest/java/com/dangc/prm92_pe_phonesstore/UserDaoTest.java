package com.dangc.prm92_pe_phonesstore;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.dao.UserDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class UserDaoTest {
    private AppDatabase db;
    private UserDao userDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // Sử dụng in-memory database để test, dữ liệu sẽ bị xóa sau khi test kết thúc
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        userDao = db.userDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void insertAndGetUser() throws Exception {
        // Arrange: Chuẩn bị đối tượng User
        User user = new User("Dang C", "test@example.com", "password123");

        // Act: Thực hiện hành động insert vào database
        userDao.insert(user);
        
        // Tìm lại user vừa insert bằng email
        User foundUser = userDao.findByEmail("test@example.com");

        // Assert: Kiểm tra kết quả
        assertNotNull(foundUser); // Đảm bảo tìm thấy user
        assertEquals(user.getFullName(), foundUser.getFullName());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }
}
