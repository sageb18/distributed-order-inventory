package com.sageb18.distributedorderinventory.service;

import com.sageb18.distributedorderinventory.model.Product;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService {

    public Product createProduct(Product product) {
        product.setProductId(UUID.randomUUID().toString());

        return product;
    }
}
