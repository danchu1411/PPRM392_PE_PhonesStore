package com.dangc.prm92_pe_phonesstore;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.dao.OrderDao;
import com.dangc.prm92_pe_phonesstore.data.dao.OrderItemDao;
import com.dangc.prm92_pe_phonesstore.data.dao.ProductDao;
import com.dangc.prm92_pe_phonesstore.data.dao.UserDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Order;
import com.dangc.prm92_pe_phonesstore.data.entity.OrderItem;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.data.entity.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.dangc.prm92_pe_phonesstore.ProductDaoTest.getOrAwaitValue;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class OrderDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private UserDao userDao;
    private ProductDao productDao;
    private OrderDao orderDao;
    private OrderItemDao orderItemDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();
        productDao = db.productDao();
        orderDao = db.orderDao();
        orderItemDao = db.orderItemDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void insertOrderAndGetRevenue() throws InterruptedException {
        // 1. Create dependencies
        userDao.insert(new User("test user", "user@test.com", "123"));
        productDao.insert(new Product("Phone A", "Brand A", "", 100, ""));
        // User and Product now have ID = 1

        // 2. Create orders
        Order order1 = new Order(1, new Date(), 250); // user_id = 1
        Order order2 = new Order(1, new Date(), 350); // user_id = 1
        orderDao.insert(order1);
        orderDao.insert(order2);

        // 3. Create order items (optional for this test, but good practice)
        // orderId will be 1 and 2
        orderItemDao.insert(new OrderItem(1, 1, 1, 100)); // order_id = 1, product_id = 1
        orderItemDao.insert(new OrderItem(1, 1, 1, 150));
        orderItemDao.insert(new OrderItem(2, 1, 1, 350));

        // 4. Assert
        Double totalRevenue = getOrAwaitValue(orderDao.getTotalRevenue());
        
        // Total should be 250 + 350 = 600
        assertEquals(600.0, totalRevenue, 0.0);
    }
}