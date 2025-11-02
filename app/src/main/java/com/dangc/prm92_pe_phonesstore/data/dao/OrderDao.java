package com.dangc.prm92_pe_phonesstore.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.dangc.prm92_pe_phonesstore.data.entity.Order;
import com.dangc.prm92_pe_phonesstore.data.entity.OrderItem;
import com.dangc.prm92_pe_phonesstore.data.entity.OrderWithItem;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Order order);

    @Query("SELECT SUM(total_amount) FROM orders")
    LiveData<Double> getTotalRevenue();

    @Insert
    void insertALlOrderItem(List<OrderItem> orderItems);

    @Transaction
    @Query("SELECT * FROM orders WHERE order_id = :orderId")
    LiveData<OrderWithItem> getOrderWithItems(long orderId);

    @Query("SELECT SUM(total_amount) FROM orders WHERE STRFTIME('%Y-%m-%d', order_date / 1000, 'unixepoch') = :date")
    LiveData<Double> getDailyRevenue(String date);

    @Query("SELECT SUM(total_amount) FROM orders WHERE STRFTIME('%Y-%m', order_date / 1000, 'unixepoch') = :month")
    LiveData<Double> getMonthlyRevenue(String month);

    @Query("SELECT SUM(total_amount) FROM orders WHERE STRFTIME('%Y', order_date / 1000, 'unixepoch') = :year")
    LiveData<Double> getYearlyRevenue(String year);
}