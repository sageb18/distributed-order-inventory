package com.sageb18.distributedorderinventory;

import com.sageb18.distributedorderinventory.model.Order;
import com.sageb18.distributedorderinventory.model.OrderStatus;
import com.sageb18.distributedorderinventory.model.Product;
import com.sageb18.distributedorderinventory.repository.ProductRepository;
import com.sageb18.distributedorderinventory.service.OrderService;
import com.sageb18.distributedorderinventory.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class OrderConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testConcurrentOrders() throws InterruptedException {
        Product product = new Product();

        product.setName("Limited Sweatpants");
        product.setPrice(new BigDecimal("180.00"));
        product.setStock(10);

        product = productService.createProduct(product);

        String productId = product.getProductId();

        // creating thread pool to test concurrent requests
        // conceptually: give me 50 worker threads so I
        // can run a bunch of order attempts at the same time.
        ExecutorService executor = Executors.newFixedThreadPool(50);

        // counts successful orders across multiple threads
        AtomicInteger confirmedOrders = new AtomicInteger(0);

        // submit 100 order attempts
        // 100 customers each trying to buy 1
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                Order order = new Order();
                order.setProductId(productId);
                order.setQuantity(1);

                Order result = orderService.createOrder(order);

                if (result.getStatus() == OrderStatus.CONFIRMED) {
                    confirmedOrders.incrementAndGet();
                }
            });
        }

        // stop accepting new jobs AND wait for all current jobs to finish
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Product finalProduct = productRepository.findById(productId);

        System.out.println("Starting stock: 10");
        System.out.println("Confirmed orders: " + confirmedOrders.get());
        System.out.println("Final stock: " + finalProduct.getStock());

    }

}
