package com.sageb18.distributedorderinventory.repository;

import com.sageb18.distributedorderinventory.model.Order;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class OrderRepository {
    private final DynamoDbTable<Order> orderTable;

    public OrderRepository(DynamoDbEnhancedClient enhancedClient) {
        this.orderTable = enhancedClient.table(
                "orders",
                TableSchema.fromBean(Order.class)
        );
    }

    public Order save(Order order) {
        orderTable.putItem(order);
        return order;
    }

    public Order findById(String orderId) {
        Key key = Key.builder()
                .partitionValue(orderId)
                .build();

        return orderTable.getItem(key);
    }

    public Collection<Order> findAll() {
        List<Order> orders = new ArrayList<>();

        orderTable.scan()
                .items()
                .forEach(orders::add);

        return orders;
    }
}
