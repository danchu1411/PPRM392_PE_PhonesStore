package com.dangc.prm92_pe_phonesstore.data.repository;

import com.dangc.prm92_pe_phonesstore.data.dao.UserDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.User;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService databaseWriteExecutor;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
        this.databaseWriteExecutor = AppDatabase.databaseWriteExecutor;
    }

    public void register(User user) {
        databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    public Future<User> login(String email, String password) {
        Callable<User> callable = () -> userDao.findByEmailAndPassword(email, password);
        return databaseWriteExecutor.submit(callable);
    }

    public Future<User> findByEmail(String email) {
        Callable<User> callable = () -> userDao.findByEmail(email);
        return databaseWriteExecutor.submit(callable);
    }
}