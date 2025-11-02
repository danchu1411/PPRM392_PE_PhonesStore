package com.dangc.prm92_pe_phonesstore.ui.cart;

import android.annotation.SuppressLint;
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
import java.util.Map;

public class CartAdapter extends ListAdapter<Map.Entry<Product, Integer>, CartAdapter.CartViewHolder> {
    private OnCartItemActionListener listener;
    public CartAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Map.Entry<Product, Integer>> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Map.Entry<Product, Integer>>() {
                @Override
                public boolean areItemsTheSame(@NonNull Map.Entry<Product, Integer> oldItem,
                                               @NonNull Map.Entry<Product, Integer> newItem) {
                    return oldItem.getKey().getProductId() == newItem.getKey().getProductId();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull Map.Entry<Product, Integer> oldItem,
                                                  @NonNull Map.Entry<Product, Integer> newItem) {
                    return oldItem.getKey().equals(newItem.getKey()) &&
                            oldItem.getValue().equals(newItem.getValue());
                }
            };

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Map.Entry<Product, Integer> cartEntry = getItem(position);
        Product product = cartEntry.getKey();
        Integer quantity = cartEntry.getValue();

        holder.textViewCartItemName.setText(product.getModelName());
        holder.textViewCartItemPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.textViewQuantity.setText(String.valueOf(quantity));

        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_broken)
                .into(holder.imageViewCartItem);

        // Gắn listener cho các nút
        holder.buttonDecreaseQuantity.setOnClickListener(v -> {
            if (listener != null) listener.onDecreaseQuantity(product);
        });
        holder.buttonIncreaseQuantity.setOnClickListener(v -> {
            if (listener != null) listener.onIncreaseQuantity(product);
        });
        holder.buttonRemoveItem.setOnClickListener(v -> {
            if (listener != null) listener.onRemoveItem(product);
        });
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageViewCartItem;
        final TextView textViewCartItemName;
        final TextView textViewCartItemPrice;
        final TextView textViewQuantity;
        final ImageButton buttonDecreaseQuantity;
        final ImageButton buttonIncreaseQuantity;
        final ImageButton buttonRemoveItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCartItem = itemView.findViewById(R.id.imageViewCartItem);
            textViewCartItemName = itemView.findViewById(R.id.textViewCartItemName);
            textViewCartItemPrice = itemView.findViewById(R.id.textViewCartItemPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonDecreaseQuantity = itemView.findViewById(R.id.buttonDecreaseQuantity);
            buttonIncreaseQuantity = itemView.findViewById(R.id.buttonIncreaseQuantity);
            buttonRemoveItem = itemView.findViewById(R.id.buttonRemoveItem);
        }
    }

    public interface OnCartItemActionListener {
        void onDecreaseQuantity(Product product);
        void onIncreaseQuantity(Product product);
        void onRemoveItem(Product product);
    }

    public void setOnCartItemActionListener(OnCartItemActionListener listener) {
        this.listener = listener;
    }
}
