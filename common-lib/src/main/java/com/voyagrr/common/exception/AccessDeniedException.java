package com.voyagrr.common.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException() {
        super("Access denied to the resource");
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
