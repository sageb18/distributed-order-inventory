package com.sageb18.distributedorderinventory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class OrderConcurrencyTest {

    // initialize 50 worker threads to manage 100 concurrent test requests
    ExecutorService executor = Executors.newFixedThreadPool(50);

}
