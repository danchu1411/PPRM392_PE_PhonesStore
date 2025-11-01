package com.dangc.prm92_pe_phonesstore.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.dangc.prm92_pe_phonesstore.R;
import com.dangc.prm92_pe_phonesstore.viewmodel.ProductViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductListFragment extends Fragment {

    private ProductViewModel productViewModel;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private FloatingActionButton fabAddProduct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo Views
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        progressBar = view.findViewById(R.id.progressBar);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);

        // Thiết lập Adapter
        adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);

        // Lấy ViewModel
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        // Quan sát dữ liệu sản phẩm từ ViewModel
        productViewModel.products.observe(getViewLifecycleOwner(), products -> {
            progressBar.setVisibility(View.GONE);
            if (products != null && !products.isEmpty()) {
                textViewEmpty.setVisibility(View.GONE);
                adapter.submitList(products);
            } else {
                textViewEmpty.setVisibility(View.VISIBLE);
            }
        });

        // Xử lý sự kiện click cho FAB
        fabAddProduct.setOnClickListener(v -> {
            // TODO: Điều hướng đến màn hình AddEditProductFragment
        });
    }
}