package com.myshopnet.errors;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String entityName) {
        super(String.format("Entity '%s' already exists", entityName));
    }
}
