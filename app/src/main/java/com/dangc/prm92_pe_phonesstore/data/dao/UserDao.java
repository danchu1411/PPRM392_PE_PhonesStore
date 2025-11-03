package com.dangc.prm92_pe_phonesstore.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dangc.prm92_pe_phonesstore.data.entity.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(User user); // SỬA: Đảm bảo trả về long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User findByEmailAndPassword(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    LiveData<User> getUserById(int userId);

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    User getUserByIdSync(int userId); // THÊM PHƯƠNG THỨC NÀY

    @Update
    void update(User user);

    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();
}