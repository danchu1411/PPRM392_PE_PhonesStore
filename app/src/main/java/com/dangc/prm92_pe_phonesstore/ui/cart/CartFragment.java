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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        textViewEmptyCart = view.findViewById(R.id.textViewEmptyCart);
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);
        buttonCheckout = view.findViewById(R.id.buttonCheckout);

        cartAdapter = new CartAdapter();
        recyclerViewCart.setAdapter(cartAdapter);
        cartAdapter.setOnCartItemActionListener(this);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        requireActivity().setTitle("Shopping Cart");

        cartViewModel.cartItems.observe(getViewLifecycleOwner(), cartItemsMap -> {
            if (cartItemsMap != null && !cartItemsMap.isEmpty()) {
                cartAdapter.submitList(new ArrayList<>(cartItemsMap.entrySet()));
                textViewEmptyCart.setVisibility(View.GONE);
                buttonCheckout.setEnabled(true);
            } else {
                cartAdapter.submitList(new ArrayList<>());
                textViewEmptyCart.setVisibility(View.VISIBLE);
                buttonCheckout.setEnabled(false);
            }
        });

        cartViewModel.totalPrice.observe(getViewLifecycleOwner(), totalPrice -> {
            textViewTotalPrice.setText(String.format("$%.2f", totalPrice));
        });

        buttonCheckout.setOnClickListener(v -> {
            int currentUserId = authViewModel.getCurrentUserId();
            if (currentUserId != -1) {
                cartViewModel.checkout(currentUserId);
                Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigateUp();
            } else {
                Toast.makeText(getContext(), "Please log in to checkout.", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
