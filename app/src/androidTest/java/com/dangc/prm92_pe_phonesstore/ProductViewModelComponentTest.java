package com.dangc.prm92_pe_phonesstore;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dangc.prm92_pe_phonesstore.data.dao.ProductDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.data.repository.ProductRepository;
import com.dangc.prm92_pe_phonesstore.util.LiveDataTestUtil;
import com.dangc.prm92_pe_phonesstore.viewmodel.ProductViewModel;
import com.dangc.prm92_pe_phonesstore.viewmodel.ProductViewModel.SortType;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ProductViewModelComponentTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private ProductViewModel productViewModel;
    private ProductDao productDao;

    @Before
    public void createDb() {
        Application application = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(application, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        productDao = db.productDao();
        
        ProductRepository productRepository = new ProductRepository(productDao, db.databaseWriteExecutor);
        productViewModel = new ProductViewModel(application, productRepository);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void sortProducts_updatesLiveDataCorrectly() throws InterruptedException {
        // Arrange
        productDao.insert(new Product("Phone C", "Brand", "", 300, ""));
        productDao.insert(new Product("Phone A", "Brand", "", 100, ""));
        productDao.insert(new Product("Phone B", "Brand", "", 200, ""));

        // Act
        productViewModel.sortProducts(SortType.PRICE_ASC);
        
        // Assert
        List<Product> sortedProducts = LiveDataTestUtil.getOrAwaitValue(productViewModel.products);
        assertEquals(3, sortedProducts.size());
        assertEquals(100, sortedProducts.get(0).getPrice(), 0.0);
        assertEquals(200, sortedProducts.get(1).getPrice(), 0.0);
        assertEquals(300, sortedProducts.get(2).getPrice(), 0.0);
    }
}