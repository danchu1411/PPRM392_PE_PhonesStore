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
import com.dangc.prm92_pe_phonesstore.ui.auth.AuthActivity;
import com.dangc.prm92_pe_phonesstore.viewmodel.AuthViewModel;
import com.dangc.prm92_pe_phonesstore.viewmodel.ProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductListFragment extends Fragment {

    private static final String TAG = "ProductListFragment";

    private ProductViewModel productViewModel;
    private AuthViewModel authViewModel;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private FloatingActionButton fabAddProduct;
    private MaterialToolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

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

        adapter.setOnItemClickListener(product -> {
            int idToSend = product.getProductId();
            Log.d(TAG, "Navigating with productId: " + idToSend);

            Bundle bundle = new Bundle();
            bundle.putInt("productId", idToSend);
            Navigation.findNavController(view).navigate(R.id.action_productListFragment_to_productDetailFragment, bundle);
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
            if (item.getItemId() == R.id.action_sort) {
                productViewModel.toggleSortOrder();
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
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to return to the login screen?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    authViewModel.logout();
                    Intent intent = new Intent(getActivity(), AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}