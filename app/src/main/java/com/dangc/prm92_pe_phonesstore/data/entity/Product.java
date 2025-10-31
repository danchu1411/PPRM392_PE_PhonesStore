package com.dangc.prm92_pe_phonesstore.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    private int productId;

    @ColumnInfo(name = "model_name")
    private String modelName;

    @ColumnInfo(name = "brand")
    private String brand;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    // Constructors
    public Product(String modelName, String brand, String description, double price, String imageUrl) {
        this.modelName = modelName;
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters
    public int getProductId() {
        return productId;
    }

    public String getModelName() {
        return modelName;
    }

    public String getBrand() {
        return brand;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}