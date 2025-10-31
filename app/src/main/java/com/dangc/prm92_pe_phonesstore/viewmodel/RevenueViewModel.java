package com.dangc.prm92_pe_phonesstore.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.repository.OrderRepository;

public class RevenueViewModel extends AndroidViewModel {

    public final LiveData<Double> totalRevenue;

    public RevenueViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        OrderRepository orderRepository = new OrderRepository(
                db.orderDao(),
                db.orderItemDao(),
                db.databaseWriteExecutor
        );
        totalRevenue = orderRepository.getTotalRevenue();
    }
    
    public RevenueViewModel(@NonNull Application application, @NonNull OrderRepository orderRepository) {
        super(application);
        totalRevenue = orderRepository.getTotalRevenue();
    }
}