package com.dangc.prm92_pe_phonesstore;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.dao.ProductDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.data.repository.ProductRepository;
import com.dangc.prm92_pe_phonesstore.util.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

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
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        ProductDao productDao = db.productDao();
        productRepository = new ProductRepository(productDao, db.databaseWriteExecutor);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void insertAndGetAllProducts() throws InterruptedException {
        // Arrange
        Product product = new Product("Test Phone", "Test Brand", "", 100, "");
        
        // Act
        productRepository.insert(product);
        Thread.sleep(500); // Wait for async operation
        List<Product> productList = LiveDataTestUtil.getOrAwaitValue(productRepository.getAllProducts());

        // Assert
        assertFalse(productList.isEmpty());
        assertEquals("Test Phone", productList.get(0).getModelName());
    }
}