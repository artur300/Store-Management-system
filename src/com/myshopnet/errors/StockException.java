package com.myshopnet.errors;

public class StockException extends RuntimeException {
    public StockException(String productName) {
        super("The product " + productName + " is not available in stock.");
    }
}
