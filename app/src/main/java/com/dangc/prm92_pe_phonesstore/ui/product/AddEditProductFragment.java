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
import androidx.navigation.NavController;
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
    private MaterialToolbar toolbar; // Thêm Toolbar

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

        toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        buttonSave.setOnClickListener(v -> saveProduct());
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

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Product newProduct = new Product(modelName, brand, description, price, imageUrl);
        productViewModel.insert(newProduct);
        
        Toast.makeText(getContext(), "Product saved successfully", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(getView()).navigateUp();
    }
}