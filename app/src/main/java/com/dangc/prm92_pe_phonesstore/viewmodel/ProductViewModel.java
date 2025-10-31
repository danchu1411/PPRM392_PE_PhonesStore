package com.dangc.prm92_pe_phonesstore.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.dangc.prm92_pe_phonesstore.data.database.AppDatabase;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.data.repository.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    private final ProductRepository productRepository;
    
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<SortType> sortType = new MutableLiveData<>(SortType.NONE);

    public final LiveData<List<Product>> products;

    public enum SortType {
        NONE,
        PRICE_ASC,
        PRICE_DESC
    }
    
    public ProductViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        this.productRepository = new ProductRepository(db.productDao(), db.databaseWriteExecutor);
        this.products = createProductsLiveData();
    }

    public ProductViewModel(@NonNull Application application, @NonNull ProductRepository productRepository) {
        super(application);
        this.productRepository = productRepository;
        this.products = createProductsLiveData();
    }

    private LiveData<List<Product>> createProductsLiveData() {
        return Transformations.switchMap(searchQuery, query -> 
            Transformations.switchMap(sortType, sort -> {
                if (query != null && !query.isEmpty()) {
                    return productRepository.searchProducts(query);
                } else {
                    switch (sort) {
                        case PRICE_ASC:
                            return productRepository.getProductsSortedByPriceAsc();
                        case PRICE_DESC:
                            return productRepository.getProductsSortedByPriceDesc();
                        default:
                            return productRepository.getAllProducts();
                    }
                }
            })
        );
    }

    public void searchProducts(String query) {
        searchQuery.setValue(query);
    }

    public void sortProducts(SortType type) {
        sortType.setValue(type);
    }
    
    public void insert(Product product) {
        productRepository.insert(product);
    }
    
    public void update(Product product) {
        productRepository.update(product);
    }
    
    public void delete(Product product) {
        productRepository.delete(product);
    }
}