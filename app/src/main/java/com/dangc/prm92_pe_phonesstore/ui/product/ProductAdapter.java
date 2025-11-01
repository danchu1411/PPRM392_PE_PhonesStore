package com.dangc.prm92_pe_phonesstore.ui.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.dangc.prm92_pe_phonesstore.R;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;

public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {

    public ProductAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getProductId() == newItem.getProductId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getModelName().equals(newItem.getModelName()) &&
                   oldItem.getPrice() == newItem.getPrice();
        }
    };

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = getItem(position);
        holder.textViewName.setText(currentProduct.getModelName());
        holder.textViewBrand.setText(currentProduct.getBrand());
        holder.textViewPrice.setText(String.format("$%.2f", currentProduct.getPrice()));
        // TODO: Tải hình ảnh bằng thư viện như Glide hoặc Picasso
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewBrand;
        private final TextView textViewPrice;
        private final ImageView imageViewProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewBrand = itemView.findViewById(R.id.textViewBrand);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
        }
    }
}