package com.dangc.prm92_pe_phonesstore.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dangc.prm92_pe_phonesstore.data.entity.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User findByEmailAndPassword(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);
    
    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    LiveData<User> getUserById(int userId);
}