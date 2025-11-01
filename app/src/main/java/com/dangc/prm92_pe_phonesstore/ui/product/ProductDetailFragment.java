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

import com.bumptech.glide.Glide;
import com.dangc.prm92_pe_phonesstore.R;
import com.dangc.prm92_pe_phonesstore.viewmodel.ProductViewModel;

public class ProductDetailFragment extends Fragment {

    private ProductViewModel productViewModel;
    private int productId;

    private ImageView imageViewProductDetail;
    private TextView textViewNameDetail, textViewBrandDetail, textViewPriceDetail, textViewDescriptionDetail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lấy productId từ arguments
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

        imageViewProductDetail = view.findViewById(R.id.imageViewProductDetail);
        textViewNameDetail = view.findViewById(R.id.textViewNameDetail);
        textViewBrandDetail = view.findViewById(R.id.textViewBrandDetail);
        textViewPriceDetail = view.findViewById(R.id.textViewPriceDetail);
        textViewDescriptionDetail = view.findViewById(R.id.textViewDescriptionDetail);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        if (productId != -1) {
            productViewModel.getProductById(productId).observe(getViewLifecycleOwner(), product -> {
                if (product != null) {
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