package com.sageb18.distributedorderinventory.repository;

import com.sageb18.distributedorderinventory.model.Product;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ProductRepository {

    private final Map<String, Product> products = new HashMap<>();

    public Product save(Product product) {
        products.put(product.getProductId(), product);
        return product;
    }

    public Product findById(String productId) {
        return products.get(productId);
    }

    public Collection<Product> findAll() {
        return products.values();
    }
}
