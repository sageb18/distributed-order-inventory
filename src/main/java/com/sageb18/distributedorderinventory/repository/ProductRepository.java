package com.sageb18.distributedorderinventory.repository;

import com.sageb18.distributedorderinventory.model.Product;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Repository
public class ProductRepository {

    private final DynamoDbTable<Product> productTable;

    public ProductRepository(DynamoDbEnhancedClient enhancedClient) {
        this.productTable = enhancedClient.table(
                "products",
                TableSchema.fromBean(Product.class)
        );
    }

    public Product save(Product product) {
        productTable.putItem(product);
        return product;
    }

    public Product findById(String productId) {
        Key key = Key.builder()
                .partitionValue(productId)
                .build();

        return productTable.getItem(key);
    }

    public Collection<Product> findAll() {
        List<Product> products = new ArrayList<>();

        productTable.scan()
                .items()
                .forEach(products::add);

        return products;
    }
}
