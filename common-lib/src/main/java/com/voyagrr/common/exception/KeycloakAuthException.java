package com.voyagrr.common.exception;

public class KeycloakAuthException extends RuntimeException {

    public KeycloakAuthException() {
        super("Keycloak Authenticaion Error");
    }

    public KeycloakAuthException(String message) {
        super(message);
    }
}
