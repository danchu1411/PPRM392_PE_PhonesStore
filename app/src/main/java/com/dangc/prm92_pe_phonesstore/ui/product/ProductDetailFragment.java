package com.dangc.prm92_pe_phonesstore.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.dangc.prm92_pe_phonesstore.R;
import com.dangc.prm92_pe_phonesstore.viewmodel.ProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class ProductDetailFragment extends Fragment {

    private ProductViewModel productViewModel;
    private int productId;

    private ImageView imageViewProductDetail;
    private TextView labelModelName, labelBrand, labelPrice, labelDescription;
    private TextView textViewNameDetail, textViewBrandDetail, textViewPriceDetail, textViewDescriptionDetail;
    private MaterialToolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getInt("productId", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo Views
        toolbar = view.findViewById(R.id.toolbar);
        imageViewProductDetail = view.findViewById(R.id.imageViewProductDetail);
        labelModelName = view.findViewById(R.id.labelModelName);
        labelBrand = view.findViewById(R.id.labelBrand);
        labelPrice = view.findViewById(R.id.labelPrice);
        labelDescription = view.findViewById(R.id.labelDescription);
        textViewNameDetail = view.findViewById(R.id.textViewNameDetail);
        textViewBrandDetail = view.findViewById(R.id.textViewBrandDetail);
        textViewPriceDetail = view.findViewById(R.id.textViewPriceDetail);
        textViewDescriptionDetail = view.findViewById(R.id.textViewDescriptionDetail);

        // Thiết lập sự kiện cho Toolbar
        toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        if (productId != -1) {
            productViewModel.getProductById(productId).observe(getViewLifecycleOwner(), product -> {
                if (product != null) {
                    // Cập nhật tiêu đề Toolbar với tên sản phẩm
                    toolbar.setTitle(product.getModelName());
                    
                    textViewNameDetail.setText(product.getModelName());
                    textViewBrandDetail.setText(product.getBrand());
                    textViewPriceDetail.setText(String.format("$%.2f", product.getPrice()));
                    textViewDescriptionDetail.setText(product.getDescription());

                    Glide.with(this)
                            .load(product.getImageUrl())
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_broken)
                            .into(imageViewProductDetail);
                }
            });
        } else {
            Toast.makeText(getContext(), "Error: Product ID not found", Toast.LENGTH_SHORT).show();
        }
    }
}