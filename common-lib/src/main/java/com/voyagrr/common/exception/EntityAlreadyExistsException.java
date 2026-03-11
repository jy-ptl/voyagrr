package com.voyagrr.common.exception;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException() {
        super("Entity Already Exists");
    }

    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
