package com.dangc.prm92_pe_phonesstore;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Order;
import com.dangc.prm92_pe_phonesstore.data.entity.OrderItem;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.repository.OrderRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dangc.prm92_pe_phonesstore.ProductDaoTest.getOrAwaitValue;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class OrderRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private OrderRepository orderRepository;

    @Before
    public void createDb() {
        Application application = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(application, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        orderRepository = new OrderRepository(application);
    }

    @After
    public void closeDb() throws IOException {
        AppDatabase.getDatabase(ApplicationProvider.getApplicationContext()).close();
        db.close();
    }

    @Test
    public void insertOrderAndVerifyRevenue() throws InterruptedException {
        // Arrange: Cần insert User và Product trước để không vi phạm khóa ngoại
        db.userDao().insert(new User("test user", "user@test.com", "123")); // id=1
        db.productDao().insert(new Product("Phone A", "Brand", "", 200, "")); // id=1

        // Chuẩn bị Order và OrderItems
        Order order = new Order(1, new Date(), 400.0); // userId=1, total=400
        List<OrderItem> items = new ArrayList<>();
        // orderId sẽ được gán bởi repository
        items.add(new OrderItem(0, 1, 2, 200)); // productId=1, quantity=2

        // Act: Thêm order vào repository
        orderRepository.insertOrder(order, items);

        // Chờ tác vụ ghi hoàn thành
        Thread.sleep(1000);

        // Assert: Lấy tổng doanh thu và kiểm tra
        Double totalRevenue = getOrAwaitValue(orderRepository.getTotalRevenue());
        assertEquals(400.0, totalRevenue, 0.0);
    }
}