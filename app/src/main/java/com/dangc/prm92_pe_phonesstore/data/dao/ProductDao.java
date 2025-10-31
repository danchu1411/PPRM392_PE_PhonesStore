package com.dangc.prm92_pe_phonesstore.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dangc.prm92_pe_phonesstore.data.entity.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products ORDER BY model_name ASC")
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM products WHERE product_id = :productId")
    LiveData<Product> getProductById(int productId);

    @Query("SELECT * FROM products WHERE model_name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%'")
    LiveData<List<Product>> searchProducts(String query);

    @Query("SELECT * FROM products ORDER BY price ASC")
    LiveData<List<Product>> getProductsSortedByPriceAsc();

    @Query("SELECT * FROM products ORDER BY price DESC")
    LiveData<List<Product>> getProductsSortedByPriceDesc();
}