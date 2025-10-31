package com.dangc.prm92_pe_phonesstore.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.repository.OrderRepository;

public class RevenueViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;
    public final LiveData<Double> totalRevenue;

    public RevenueViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(
                AppDatabase.getDatabase(application).orderDao(),
                AppDatabase.getDatabase(application).orderItemDao()
        );
        totalRevenue = orderRepository.getTotalRevenue();
    }
}