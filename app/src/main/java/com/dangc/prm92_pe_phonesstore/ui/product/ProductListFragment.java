package com.dangc.prm92_pe_phonesstore.ui.product;

import android.view.Menu;
import android.view.MenuInflater;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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

        adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnProductActionClickListener(this);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

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
            Bundle bundle = new Bundle();
            bundle.putInt("productId", -1);
            navController.navigate(R.id.action_productListFragment_to_addEditProductFragment, bundle);
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLogoutConfirmationDialog();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        NavController navController = Navigation.findNavController(requireView());

        if (itemId == R.id.action_sort) {
            productViewModel.toggleSortOrder();
            return true;
        } else if (itemId == R.id.action_view_cart) {
            navController.navigate(R.id.action_productListFragment_to_cartFragment);
            return true;
        } else if (itemId == R.id.action_view_revenue) {
            navController.navigate(R.id.action_productListFragment_to_revenueFragment);
            return true;
        } else if (itemId == R.id.action_view_logout) {
            showLogoutConfirmationDialog();
            return true;
        } else if(itemId == R.id.action_view_profile) {
            navController.navigate(R.id.action_productListFragment_to_profileFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to return to the login screen?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    authViewModel.logout();
                    Intent intent = new Intent(requireActivity(), AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    @Override
    public void onProductClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putInt("productId", product.getProductId());
        Navigation.findNavController(requireView()).navigate(R.id.action_productListFragment_to_productDetailFragment, bundle);
    }

    @Override
    public void onAddToCartClick(Product product) {
        cartViewModel.addProductToCart(product);
        Toast.makeText(getContext(), "Added " + product.getModelName() + " to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putInt("productId", product.getProductId());
        Navigation.findNavController(requireView()).navigate(R.id.action_productListFragment_to_addEditProductFragment, bundle);
    }

    @Override
    public void onDeleteClick(Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete " + product.getModelName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    productViewModel.delete(product);
                    Toast.makeText(getContext(), product.getModelName() + " deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
