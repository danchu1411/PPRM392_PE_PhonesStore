package com.dangc.prm92_pe_phonesstore;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.repository.OrderRepository;
import com.dangc.prm92_pe_phonesstore.util.LiveDataTestUtil;
import com.dangc.prm92_pe_phonesstore.viewmodel.CartViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CartViewModelComponentTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private CartViewModel cartViewModel;

    @Before
    public void createDb() {
        Application application = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(application, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        
        OrderRepository orderRepository = new OrderRepository(db.orderDao(), db.orderItemDao(), db.databaseWriteExecutor);
        cartViewModel = new CartViewModel(application, orderRepository);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void addProductAndCheckout_updatesDatabase() throws InterruptedException {
        // Arrange
        db.userDao().insert(new User("Test User", "test@user.com", "pass")); // userId = 1
        db.productDao().insert(new Product("Phone A", "Brand", "", 150.50, ""));
        
        List<Product> products = LiveDataTestUtil.getOrAwaitValue(db.productDao().getAllProducts());
        Product p1 = products.get(0);


        // Act 1
        cartViewModel.addProductToCart(p1);
        cartViewModel.addProductToCart(p1); // Add twice

        // Act 2
        cartViewModel.checkout(1); // Checkout for userId = 1

        Thread.sleep(500);

        // Assert
        Double totalRevenue = LiveDataTestUtil.getOrAwaitValue(db.orderDao().getTotalRevenue());
        assertEquals(301.0, totalRevenue, 0.0); // 150.50 * 2

        assertTrue(cartViewModel.cartItems.getValue().isEmpty());
    }
}