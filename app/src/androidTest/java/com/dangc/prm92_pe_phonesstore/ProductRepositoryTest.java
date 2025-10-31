package com.dangc.prm92_pe_phonesstore;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.data.repository.ProductRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static com.dangc.prm92_pe_phonesstore.ProductDaoTest.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class ProductRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private ProductRepository productRepository;

    @Before
    public void createDb() {
        Application application = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(application, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        // Cần khởi tạo repository với context để nó có thể lấy db
        productRepository = new ProductRepository(application);
    }

    @After
    public void closeDb() throws IOException {
        AppDatabase.getDatabase(ApplicationProvider.getApplicationContext()).close();
        db.close();
    }

    @Test
    public void insertAndGetAllProducts() throws InterruptedException {
        // Arrange
        Product product = new Product("Test Phone", "Test Brand", "", 100, "");
        
        // Act
        productRepository.insert(product);

        // Chờ một chút để tác vụ ghi hoàn thành
        Thread.sleep(1000);

        List<Product> productList = getOrAwaitValue(productRepository.getAllProducts());

        // Assert
        assertFalse(productList.isEmpty());
        assertEquals("Test Phone", productList.get(0).getModelName());
    }
}