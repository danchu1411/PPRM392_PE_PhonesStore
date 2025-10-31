package com.dangc.prm92_pe_phonesstore.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.dangc.prm92_pe_phonesstore.data.dao.ProductDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ProductRepository {

    private final ProductDao productDao;
    private final ExecutorService databaseWriteExecutor;

    public ProductRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.productDao = db.productDao();
        this.databaseWriteExecutor = AppDatabase.databaseWriteExecutor;
    }

    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAllProducts();
    }

    public LiveData<Product> getProductById(int productId) {
        return productDao.getProductById(productId);
    }

    public LiveData<List<Product>> searchProducts(String query) {
        return productDao.searchProducts(query);
    }

    public LiveData<List<Product>> getProductsSortedByPriceAsc() {
        return productDao.getProductsSortedByPriceAsc();
    }

    public LiveData<List<Product>> getProductsSortedByPriceDesc() {
        return productDao.getProductsSortedByPriceDesc();
    }

    public void insert(Product product) {
        databaseWriteExecutor.execute(() -> {
            productDao.insert(product);
        });
    }

    public void update(Product product) {
        databaseWriteExecutor.execute(() -> {
            productDao.update(product);
        });
    }

    public void delete(Product product) {
        databaseWriteExecutor.execute(() -> {
            productDao.delete(product);
        });
    }
}