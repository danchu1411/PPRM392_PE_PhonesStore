package com.dangc.prm92_pe_phonesstore;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.dao.OrderDao;
import com.dangc.prm92_pe_phonesstore.data.dao.UserDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Order;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.repository.OrderRepository;
import com.dangc.prm92_pe_phonesstore.util.LiveDataTestUtil;
import com.dangc.prm92_pe_phonesstore.viewmodel.RevenueViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RevenueViewModelComponentTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private RevenueViewModel revenueViewModel;
    
    @Before
    public void createDb() {
        Application application = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(application, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        
        OrderRepository orderRepository = new OrderRepository(db.orderDao(), db.orderItemDao(), db.databaseWriteExecutor);
        revenueViewModel = new RevenueViewModel(application, orderRepository);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void totalRevenue_reflectsDatabaseChanges() throws InterruptedException {
        // Arrange
        UserDao userDao = db.userDao();
        OrderDao orderDao = db.orderDao();
        
        userDao.insert(new User("Test User", "test@user.com", "pass")); // userId=1
        orderDao.insert(new Order(1, new Date(), 100.0));
        orderDao.insert(new Order(1, new Date(), 250.0));
        
        // Act & Assert
        Double totalRevenue = LiveDataTestUtil.getOrAwaitValue(revenueViewModel.totalRevenue);
        assertEquals(350.0, totalRevenue, 0.0);
    }
}