package com.myshopnet.errors;

public class InsufficientPermissionsException extends RuntimeException {
    public InsufficientPermissionsException(String message) {
        super("Insufficient permissions: " + message);
    }
}
