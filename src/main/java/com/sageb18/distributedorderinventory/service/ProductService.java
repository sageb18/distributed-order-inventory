package com.sageb18.distributedorderinventory.service;

import com.sageb18.distributedorderinventory.model.Product;
import com.sageb18.distributedorderinventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        product.setProductId(UUID.randomUUID().toString());

        return productRepository.save(product);
    }

    public Product getProductById(String productId) {
        return productRepository.findById(productId);
    }

    public Collection<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
