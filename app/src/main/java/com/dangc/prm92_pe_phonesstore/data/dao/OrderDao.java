package com.dangc.prm92_pe_phonesstore.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dangc.prm92_pe_phonesstore.data.entity.Order;

@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Order order);

    @Query("SELECT SUM(total_amount) FROM orders")
    LiveData<Double> getTotalRevenue();

    // Có thể thêm các query để lấy lịch sử đơn hàng sau này
}