package com.dangc.prm92_pe_phonesstore.data.repository;

import androidx.lifecycle.LiveData;

import com.dangc.prm92_pe_phonesstore.data.dao.OrderDao;
import com.dangc.prm92_pe_phonesstore.data.dao.OrderItemDao;
import com.dangc.prm92_pe_phonesstore.data.entity.Order;
import com.dangc.prm92_pe_phonesstore.data.entity.OrderItem;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class OrderRepository {

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final ExecutorService databaseWriteExecutor;

    public OrderRepository(OrderDao orderDao, OrderItemDao orderItemDao, ExecutorService executorService) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.databaseWriteExecutor = executorService;
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

    public LiveData<Double> getDailyRevenue(String Date) {
        return orderDao.getDailyRevenue(Date);
    }

    public LiveData<Double> getMonthlyRevenue(String Date) {
        return orderDao.getMonthlyRevenue(Date);
    }

    public LiveData<Double> getYearlyRevenue(String Date) {
        return orderDao.getYearlyRevenue(Date);
    }
}