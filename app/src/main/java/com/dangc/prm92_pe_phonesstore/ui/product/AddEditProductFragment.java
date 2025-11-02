package com.dangc.prm92_pe_phonesstore.ui.product;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.dangc.prm92_pe_phonesstore.R;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.viewmodel.ProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditProductFragment extends Fragment {

    private ProductViewModel productViewModel;
    private TextInputEditText editTextModelName, editTextBrand, editTextDescription, editTextPrice, editTextImageUrl;
    private Button buttonSave;
    private MaterialToolbar toolbar;

    private int currentProductId = -1;
    private Product currentProduct = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentProductId = getArguments().getInt("productId", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        toolbar = view.findViewById(R.id.toolbar);
        editTextModelName = view.findViewById(R.id.editTextModelName);
        editTextBrand = view.findViewById(R.id.editTextBrand);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextImageUrl = view.findViewById(R.id.editTextImageUrl);
        buttonSave = view.findViewById(R.id.buttonSave);

        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        buttonSave.setOnClickListener(v -> saveProduct());

        if (currentProductId != -1) {
            // Chế độ Edit
            toolbar.setTitle("Edit Product");
            productViewModel.getProductById(currentProductId).observe(getViewLifecycleOwner(), product -> {
                if (product != null) {
                    currentProduct = product;
                    editTextModelName.setText(product.getModelName());
                    editTextBrand.setText(product.getBrand());
                    editTextDescription.setText(product.getDescription());
                    editTextPrice.setText(String.valueOf(product.getPrice()));
                    editTextImageUrl.setText(product.getImageUrl());
                }
            });
        } else {
            // Chế độ Add
            toolbar.setTitle("Add Product");
        }
    }

    private void saveProduct() {
        String modelName = editTextModelName.getText().toString().trim();
        String brand = editTextBrand.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String imageUrl = editTextImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(modelName) || TextUtils.isEmpty(brand) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        if (currentProductId != -1 && currentProduct != null) {
            // Update
            currentProduct.setModelName(modelName);
            currentProduct.setBrand(brand);
            currentProduct.setDescription(description);
            currentProduct.setPrice(price);
            currentProduct.setImageUrl(imageUrl);
            productViewModel.update(currentProduct);
            Toast.makeText(getContext(), "Product updated", Toast.LENGTH_SHORT).show();
        } else {
            // Insert
            Product newProduct = new Product(modelName, brand, description, price, imageUrl);
            productViewModel.insert(newProduct);
            Toast.makeText(getContext(), "Product added", Toast.LENGTH_SHORT).show();
        }

        Navigation.findNavController(getView()).navigateUp();
    }
}