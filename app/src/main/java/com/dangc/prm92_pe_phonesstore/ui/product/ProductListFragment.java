package com.dangc.prm92_pe_phonesstore.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.dangc.prm92_pe_phonesstore.R;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.ui.auth.AuthActivity;
import com.dangc.prm92_pe_phonesstore.viewmodel.AuthViewModel;
import com.dangc.prm92_pe_phonesstore.viewmodel.CartViewModel;
import com.dangc.prm92_pe_phonesstore.viewmodel.ProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductListFragment extends Fragment implements ProductAdapter.OnProductActionClickListener {

    private static final String TAG = "ProductListFragment";

    private ProductViewModel productViewModel;
    private AuthViewModel authViewModel;
    private CartViewModel cartViewModel;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private FloatingActionButton fabAddProduct;
    private MaterialToolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        progressBar = view.findViewById(R.id.progressBar);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);
        toolbar = view.findViewById(R.id.toolbar);

        adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnProductActionClickListener(this);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        setupMenu();

        productViewModel.products.observe(getViewLifecycleOwner(), products -> {
            progressBar.setVisibility(View.GONE);
            adapter.submitList(products);
            if (products == null || products.isEmpty()) {
                textViewEmpty.setVisibility(View.VISIBLE);
            } else {
                textViewEmpty.setVisibility(View.GONE);
            }
        });

        fabAddProduct.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_productListFragment_to_addEditProductFragment);
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLogoutConfirmationDialog();
            }
        });
    }

    private void setupMenu() {
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_sort) {
                productViewModel.toggleSortOrder();
                return true;
            } else if (itemId == R.id.action_view_cart) {
                Navigation.findNavController(getView()).navigate(R.id.action_productListFragment_to_cartFragment);
                return true;
            } else if (itemId == R.id.action_view_revenue) {
                Navigation.findNavController(getView()).navigate(R.id.action_productListFragment_to_revenueFragment);
                return true;
            }
            return false;
        });

        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productViewModel.searchProducts(newText);
                return true;
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        // ... (không đổi)
    }

    @Override
    public void onProductClick(Product product) {
        // ... (không đổi)
    }

    @Override
    public void onAddToCartClick(Product product) {
        // ... (không đổi)
    }

    @Override
    public void onEditClick(Product product) {
        // ... (không đổi)
    }

    @Override
    public void onDeleteClick(Product product) {
        // ... (không đổi)
    }
}