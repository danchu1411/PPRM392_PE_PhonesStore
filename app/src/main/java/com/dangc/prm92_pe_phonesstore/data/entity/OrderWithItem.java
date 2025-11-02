package com.dangc.prm92_pe_phonesstore.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class OrderWithItem {
    @Embedded
    public Order order;
    @Relation(
            parentColumn = "order_id",
            entityColumn = "order_id"
    )
    public List<OrderItem> items;
}
