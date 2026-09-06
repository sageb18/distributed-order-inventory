package com.sageb18.distributedorderinventory.service;

import com.sageb18.distributedorderinventory.model.Order;
import com.sageb18.distributedorderinventory.model.OrderStatus;
import com.sageb18.distributedorderinventory.model.Product;
import com.sageb18.distributedorderinventory.repository.OrderRepository;
import com.sageb18.distributedorderinventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(Order order) {

        // every order gets a unique ID regardless of failed or not
        order.setOrderId(UUID.randomUUID().toString());

        Product product = productRepository.findById(order.getProductId());

        // check if product exists
        if (product == null) {
            order.setStatus(OrderStatus.FAILED_PRODUCT_CHECK);
            return orderRepository.save(order);
        }

        // check if order quantity > 0
        if (order.getQuantity() <= 0) {
            order.setStatus(OrderStatus.FAILED_ORDER_QUANTITY);
            return orderRepository.save(order);
        }

        // verify enough stock
        if (product.getStock() < order.getQuantity()) {
            order.setStatus(OrderStatus.FAILED_STOCK_CHECK);
            return orderRepository.save(order);
        }

        /*
        if everything checks out,
        1. reduce stock
        2. persist the new product
        2. confirm order
        3. persist
         */

        // reduce stock & update product in db
        product.setStock(product.getStock() - order.getQuantity());
        productRepository.save(product);

        // confirm order
        order.setStatus(OrderStatus.CONFIRMED);

        // persist order
        return orderRepository.save(order);
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public Collection<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
