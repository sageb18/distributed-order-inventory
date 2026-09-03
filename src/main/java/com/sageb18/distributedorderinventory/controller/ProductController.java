package com.sageb18.distributedorderinventory.controller;


import com.sageb18.distributedorderinventory.model.Product;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/products")
public class ProductController {

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
