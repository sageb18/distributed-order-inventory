package com.sageb18.distributedorderinventory.controller;


import com.sageb18.distributedorderinventory.model.Product;
import com.sageb18.distributedorderinventory.service.ProductService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String getAllProducts() {
        return "Testing getAllProducts";
    }

    @GetMapping("/{productId}")
    public String getProductById(@PathVariable String productId) {
        return "Testing getProductById";
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return product;
    }

}
