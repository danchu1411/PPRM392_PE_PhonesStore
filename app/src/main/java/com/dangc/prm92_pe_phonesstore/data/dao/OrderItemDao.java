package com.dangc.prm92_pe_phonesstore.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.dangc.prm92_pe_phonesstore.data.entity.OrderItem;

@Dao
public interface OrderItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OrderItem orderItem);
}