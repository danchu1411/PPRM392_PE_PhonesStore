package com.dangc.prm92_pe_phonesstore.data.repository;

import androidx.lifecycle.LiveData;

import com.dangc.prm92_pe_phonesstore.data.dao.OrderDao;
import com.dangc.prm92_pe_phonesstore.data.dao.OrderItemDao;
import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Order;
import com.dangc.prm92_pe_phonesstore.data.entity.OrderItem;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class OrderRepository {

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final ExecutorService databaseWriteExecutor;

    public OrderRepository(OrderDao orderDao, OrderItemDao orderItemDao) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.databaseWriteExecutor = AppDatabase.databaseWriteExecutor;
    }

    public void insertOrder(Order order, List<OrderItem> items) {
        databaseWriteExecutor.execute(() -> {
            long orderId = orderDao.insert(order);
            for (OrderItem item : items) {
                item.setOrderId((int) orderId);
                orderItemDao.insert(item);
            }
        });
    }

    public LiveData<Double> getTotalRevenue() {
        return orderDao.getTotalRevenue();
    }
}