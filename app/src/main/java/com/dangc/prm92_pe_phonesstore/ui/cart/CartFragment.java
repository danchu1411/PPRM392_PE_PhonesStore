package com.dangc.prm92_pe_phonesstore.ui.cart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dangc.prm92_pe_phonesstore.R;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.viewmodel.AuthViewModel;
import com.dangc.prm92_pe_phonesstore.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.Map;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemActionListener {

    private CartViewModel cartViewModel;
    private AuthViewModel authViewModel;
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private TextView textViewEmptyCart;
    private TextView textViewTotalPrice;
    private Button buttonCheckout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nếu bạn muốn Toolbar chung hiển thị nút back và tiêu đề "Shopping Cart"
        // thì không cần setHasOptionsMenu(true) ở đây.
        // Title sẽ được lấy từ nav_graph.xml label.
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Khởi tạo Views
        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        textViewEmptyCart = view.findViewById(R.id.textViewEmptyCart);
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);
        buttonCheckout = view.findViewById(R.id.buttonCheckout);

        // 2. Thiết lập RecyclerView
        cartAdapter = new CartAdapter();
        recyclerViewCart.setAdapter(cartAdapter);
        cartAdapter.setOnCartItemActionListener(this);

        // 3. Khởi tạo ViewModels (LẤY TỪ requireActivity() ĐỂ DÙNG CHUNG INSTANCE)
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class); // KHẮC PHỤC LỖI NÀY

        // 4. Đặt tiêu đề cho Toolbar chung của MainActivity
        requireActivity().setTitle("Shopping Cart");

        // 5. Quan sát LiveData từ CartViewModel
        cartViewModel.cartItems.observe(getViewLifecycleOwner(), cartItemsMap -> {
            if (cartItemsMap != null && !cartItemsMap.isEmpty()) {
                cartAdapter.submitList(new ArrayList<>(cartItemsMap.entrySet()));
                textViewEmptyCart.setVisibility(View.GONE); // Dùng View.GONE
                buttonCheckout.setEnabled(true); // Kích hoạt nút checkout
            } else {
                cartAdapter.submitList(new ArrayList<>()); // Xóa danh sách
                textViewEmptyCart.setVisibility(View.VISIBLE); // Dùng View.VISIBLE
                buttonCheckout.setEnabled(false); // Vô hiệu hóa nút checkout
            }
        });

        cartViewModel.totalPrice.observe(getViewLifecycleOwner(), totalPrice -> {
            textViewTotalPrice.setText(String.format("$%.2f", totalPrice)); // THÊM KÝ HIỆU TIỀN TỆ
        });

        // 6. Xử lý sự kiện Checkout
        buttonCheckout.setOnClickListener(v -> {
            int currentUserId = authViewModel.getCurrentUserId();
            if (currentUserId != -1) {
                cartViewModel.checkout(currentUserId);
                Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigateUp(); // Quay lại màn hình trước
            } else {
                Toast.makeText(getContext(), "Please log in to checkout.", Toast.LENGTH_SHORT).show();
                // Có thể điều hướng đến màn hình đăng nhập nếu muốn
            }
        });
    }

    // --- Implement các phương thức của OnCartItemActionListener --- (Đã được triển khai)
    @Override
    public void onDecreaseQuantity(Product product) {
        cartViewModel.decreaseProductQuantity(product);
    }

    @Override
    public void onIncreaseQuantity(Product product) {
        cartViewModel.addProductToCart(product);
    }

    @Override
    public void onRemoveItem(Product product) {
        cartViewModel.removeProductFromCart(product);
    }
}
