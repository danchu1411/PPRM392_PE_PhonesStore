package com.dangc.prm92_pe_phonesstore;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.dao.ProductDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProductDaoTest {

    // Rule này đảm bảo rằng các tác vụ của Architecture Components được thực thi đồng bộ
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private ProductDao productDao;

    // Helper method to get value from LiveData
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        // Đợi tối đa 2 giây
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }
        //noinspection unchecked
        return (T) data[0];
    }

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                // Cho phép chạy query trên main thread, chỉ dành cho test
                .allowMainThreadQueries()
                .build();
        productDao = db.productDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void insertAndGetProduct() throws InterruptedException {
        Product product = new Product("iPhone 14", "Apple", "A new iPhone", 999.99, "");
        productDao.insert(product);

        // Lấy lại product vừa insert, ID sẽ là 1
        Product foundProduct = getOrAwaitValue(productDao.getProductById(1));

        assertNotNull(foundProduct);
        assertEquals(product.getModelName(), foundProduct.getModelName());
    }

    @Test
    public void getAllProductsSortedByPriceAsc() throws InterruptedException {
        Product p1 = new Product("Phone A", "Brand A", "", 200, "");
        Product p2 = new Product("Phone B", "Brand B", "", 100, "");
        Product p3 = new Product("Phone C", "Brand C", "", 300, "");
        productDao.insert(p1);
        productDao.insert(p2);
        productDao.insert(p3);

        List<Product> sortedList = getOrAwaitValue(productDao.getProductsSortedByPriceAsc());

        assertEquals(3, sortedList.size());
        assertEquals(100, sortedList.get(0).getPrice(), 0.0);
        assertEquals(200, sortedList.get(1).getPrice(), 0.0);
        assertEquals(300, sortedList.get(2).getPrice(), 0.0);
    }
    
    @Test
    public void searchProducts() throws InterruptedException {
        Product p1 = new Product("Samsung Galaxy", "Samsung", "", 800, "");
        Product p2 = new Product("iPhone 15", "Apple", "", 1000, "");
        productDao.insert(p1);
        productDao.insert(p2);

        List<Product> foundList = getOrAwaitValue(productDao.searchProducts("sung"));

        assertEquals(1, foundList.size());
        assertEquals("Samsung Galaxy", foundList.get(0).getModelName());
    }
}