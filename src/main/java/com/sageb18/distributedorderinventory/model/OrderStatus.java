package com.sageb18.distributedorderinventory.model;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    FAILED_ORDER_QUANTITY,
    FAILED_PRODUCT_CHECK,
    FAILED_STOCK_CHECK
}
