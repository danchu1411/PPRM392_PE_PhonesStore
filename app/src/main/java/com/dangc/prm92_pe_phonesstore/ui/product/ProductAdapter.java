package com.dangc.prm92_pe_phonesstore.ui.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dangc.prm92_pe_phonesstore.R;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;

public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {

    private OnProductActionClickListener listener;

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
                   oldItem.getPrice() == newItem.getPrice() &&
                   oldItem.getImageUrl().equals(newItem.getImageUrl());
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

        Glide.with(holder.itemView.getContext())
                .load(currentProduct.getImageUrl())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_broken)
                .into(holder.imageViewProduct);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName, textViewBrand, textViewPrice;
        private final ImageView imageViewProduct;
        private final ImageButton buttonAddToCart, buttonEdit, buttonDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewBrand = itemView.findViewById(R.id.textViewBrand);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);

            // Listener cho cả item view (để xem chi tiết)
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onProductClick(getItem(position));
                }
            });

            // Listeners cho các nút hành động
            buttonAddToCart.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onAddToCartClick(getItem(position));
                }
            });

            buttonEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(getItem(position));
                }
            });

            buttonDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getItem(position));
                }
            });
        }
    }

    // Interface mới cho tất cả các hành động
    public interface OnProductActionClickListener {
        void onProductClick(Product product); // Click vào item để xem chi tiết
        void onAddToCartClick(Product product);
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public void setOnProductActionClickListener(OnProductActionClickListener listener) {
        this.listener = listener;
    }
}
