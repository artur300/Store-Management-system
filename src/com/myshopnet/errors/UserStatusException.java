package com.myshopnet.errors;

public class UserStatusException extends RuntimeException {
    public UserStatusException(String message) {
        super(message);
    }
}
