package com.dangc.prm92_pe_phonesstore.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_items",
        foreignKeys = {
                @ForeignKey(entity = Order.class,
                        parentColumns = "order_id",
                        childColumns = "order_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Product.class,
                        parentColumns = "product_id",
                        childColumns = "product_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class OrderItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "order_item_id")
    private int orderItemId;

    @ColumnInfo(name = "order_id", index = true)
    private int orderId;

    @ColumnInfo(name = "product_id", index = true)
    private int productId;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "price_per_unit")
    private double pricePerUnit;

    // Constructors
    public OrderItem(int orderId, int productId, int quantity, double pricePerUnit) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    // Getters
    public int getOrderItemId() {
        return orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    // Setters
    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}