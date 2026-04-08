package com.voyagrr.tripservice.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.voyagrr.common.dto.ErrorResponse;
import com.voyagrr.common.exception.AccessDeniedException;
import com.voyagrr.common.exception.EntityNotFoundException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleValidationErrors(MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        exception.getBindingResult().getGlobalErrors()
                .forEach(error -> errors.put(error.getObjectName(), error.getDefaultMessage()));

        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validation Error")
                .error(errors)
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {

        Throwable cause = exception.getCause();

        if (cause instanceof InvalidFormatException ife)
            if (ife.getTargetType().isEnum()) {
                String fieldName = ife.getPath().stream()
                        .map(ref -> ref.getFieldName())
                        .filter(Objects::nonNull)
                        .reduce((first, second) -> second)
                        .orElse("unknown");

                String allowedValues = Arrays.toString(ife.getTargetType().getEnumConstants());
                return ErrorResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Validation Error")
                        .error("Invalid value for '" + fieldName + "'. Allowed values: " + allowedValues)
                        .build();
            }

        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Malformed JSON request")
                .error(exception.getMessage())
                .build();
    }

}
