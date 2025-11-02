package com.dangc.prm92_pe_phonesstore.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Order;
import com.dangc.prm92_pe_phonesstore.data.entity.OrderItem;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.data.repository.OrderRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;

    private final MutableLiveData<Map<Product, Integer>> _cartItems = new MutableLiveData<>(new HashMap<>());
    public final LiveData<Map<Product, Integer>> cartItems = _cartItems;

    private final MutableLiveData<Double> _totalPrice = new MutableLiveData<>(0.0);
    public final LiveData<Double> totalPrice = _totalPrice;

    public CartViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        this.orderRepository = new OrderRepository(
                db.orderDao(),
                db.orderItemDao(),
                db.databaseWriteExecutor
        );
    }

    public CartViewModel(@NonNull Application application, @NonNull OrderRepository orderRepository) {
        super(application);
        this.orderRepository = orderRepository;
    }

    public void addProductToCart(Product product) {
        Map<Product, Integer> currentCart = _cartItems.getValue();
        if (currentCart == null) currentCart = new HashMap<>();

        // TẠO MỘT MAP MỚI ĐỂ ĐẢM BẢO LiveData NHẬN RA SỰ THAY ĐỔI
        Map<Product, Integer> newCart = new HashMap<>(currentCart);

        int quantity = newCart.containsKey(product) ? newCart.get(product) + 1 : 1;
        newCart.put(product, quantity);
        _cartItems.setValue(newCart); // Đặt Map MỚI
        updateTotalPrice();
    }

    public void removeProductFromCart(Product product) {
        Map<Product, Integer> currentCart = _cartItems.getValue();
        if (currentCart != null && currentCart.containsKey(product)) {
            // TẠO MỘT MAP MỚI
            Map<Product, Integer> newCart = new HashMap<>(currentCart);
            newCart.remove(product);
            _cartItems.setValue(newCart); // Đặt Map MỚI
            updateTotalPrice();
        }
    }

    public void checkout(int userId) {
        Map<Product, Integer> currentCart = _cartItems.getValue();
        if (currentCart == null || currentCart.isEmpty() || userId == -1) {
            return;
        }

        double total = _totalPrice.getValue() != null ? _totalPrice.getValue() : 0;
        Order order = new Order(userId, new Date(), total);

        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : currentCart.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            orderItems.add(new OrderItem(0, product.getProductId(), quantity, product.getPrice()));
        }

        orderRepository.insertOrder(order, orderItems);
        _cartItems.setValue(new HashMap<>()); // Xóa giỏ hàng bằng một Map MỚI rỗng
        updateTotalPrice();
    }

    public void decreaseProductQuantity(Product product) {
        Map<Product, Integer> currentCart = _cartItems.getValue();
        if (currentCart == null) currentCart = new HashMap<>();

        if (currentCart.containsKey(product)) {
            // TẠO MỘT MAP MỚI
            Map<Product, Integer> newCart = new HashMap<>(currentCart);
            int quantity = newCart.get(product);
            if (quantity > 1) {
                newCart.put(product, quantity - 1);
            } else {
                newCart.remove(product);
            }
            _cartItems.setValue(newCart); // Đặt Map MỚI
            updateTotalPrice();
        }
    }

    private void updateTotalPrice() {
        Map<Product, Integer> currentCart = _cartItems.getValue();
        double total = 0;
        if (currentCart != null) {
            for (Map.Entry<Product, Integer> entry : currentCart.entrySet()) {
                total += entry.getKey().getPrice() * entry.getValue();
            }
        }
        _totalPrice.setValue(total);
    }
}