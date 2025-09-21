package com.voyagrr.userservice.handler;

import java.util.Map;
import java.util.stream.Collectors;

import com.voyagrr.common.dto.ErrorResponse;
import com.voyagrr.common.exception.AccessDeniedException;
import com.voyagrr.common.exception.EntityNotFoundException;
import com.voyagrr.common.exception.InvalidCredentialsException;
import com.voyagrr.common.exception.EntityAlreadyExistsException;
import com.voyagrr.common.exception.KeycloakAuthException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
        return ErrorResponse
                .builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorResponse handleAccessDeniedException(AccessDeniedException exception) {
        return ErrorResponse.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(value = InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleInvalidCredentialsException(InvalidCredentialsException exception) {
        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(value = KeycloakAuthException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ErrorResponse handleKeycloakAuthException(KeycloakAuthException exception) {
        return ErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(value = EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ErrorResponse handleEntityAlreadyExistsException(EntityAlreadyExistsException exception) {
        return ErrorResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleValidationErrors(MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage,
                        (msg1, msg2) -> msg1));

        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validation Error")
                .error(fieldErrors)
                .build();
    }

}
