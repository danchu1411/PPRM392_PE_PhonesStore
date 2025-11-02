package com.dangc.prm92_pe_phonesstore.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Order;
import com.dangc.prm92_pe_phonesstore.data.repository.OrderRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RevenueViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;

    public enum RevenueFilterType {
        TOTAL,
        DAILY,
        MONTHLY,
        YEARLY
    }

    private final MutableLiveData<RevenueFilterType> _filterType = new MutableLiveData<>(RevenueFilterType.TOTAL);
    private final MutableLiveData<String> _filterDate = new MutableLiveData<>();
    private final MutableLiveData<Double> _revenue = new MutableLiveData<>();

    public final LiveData<Double> revenue = _revenue;

    public RevenueViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        this.orderRepository = new OrderRepository(
                db.orderDao(),
                db.orderItemDao(),
                db.databaseWriteExecutor
        );

        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        _filterDate.setValue(sdf.format(new Date()));
        _filterType.observeForever(filterType ->  updateRevenue());
        _filterDate.observeForever(filterDate -> updateRevenue());

    }
    
    public RevenueViewModel(@NonNull Application application, @NonNull OrderRepository orderRepository) {
        super(application);
        this.orderRepository = orderRepository;

        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        _filterDate.setValue(sdf.format(new Date()));
        _filterType.observeForever(filterType ->  updateRevenue());
        _filterDate.observeForever(filterDate -> updateRevenue());
    }

    private void updateRevenue() {
        RevenueFilterType currentType = _filterType.getValue();
        String currentDate = _filterDate.getValue();
        LiveData<Double> sourceRevenue = null;

        if(currentType == null) {
            currentType = RevenueFilterType.TOTAL;
        }

        switch(currentType) {
            case DAILY:
                if(currentDate != null && !currentDate.isEmpty()) {
                    sourceRevenue = orderRepository.getDailyRevenue(currentDate);
                }
                else {
                    _revenue.setValue(0.0);
                }
                break;
            case MONTHLY:
                if(currentDate != null && !currentDate.isEmpty()) {
                    sourceRevenue = orderRepository.getMonthlyRevenue(currentDate);
                } else {
                    _revenue.setValue(0.0);
                }
                break;
            case YEARLY:
                if(currentDate != null && !currentDate.isEmpty()) {
                    sourceRevenue = orderRepository.getYearlyRevenue(currentDate);
                    } else {
                    _revenue.setValue(0.0);
                }
                break;
            case TOTAL:
            default:
                sourceRevenue = orderRepository.getTotalRevenue();
                break;
        }
        if(sourceRevenue != null) {
            sourceRevenue.observeForever(value -> _revenue.setValue(value != null ? value : 0.0));
        }
    }

    public void setFilterType(RevenueFilterType type) {
        RevenueFilterType currentFilterType = _filterType.getValue();
        if(currentFilterType == null && type == null) {
            return;
        }
        if(currentFilterType == null || !currentFilterType.equals(type)) {
            _filterType.setValue(type);
        }
    }

    public void setFilterDate(String date) {
        String currentFilterDate = _filterDate.getValue();
        if (currentFilterDate == null && date == null) {
            return;
        }
        if (currentFilterDate == null || !currentFilterDate.equals(date)) {
            _filterDate.setValue(date);
        }
    }

    public RevenueFilterType getCurrentFilterType() {
        return _filterType.getValue();
    }
}