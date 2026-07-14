package com.mosify.infrastructure.in.controller;

import com.mosify.api.model.WebErrorResponse;
import com.mosify.domain.exception.ErrorCode;
import com.mosify.domain.exception.MosifyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<ErrorCode, HttpStatus> STATUS_MAP = Map.of(
            ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND,
            ErrorCode.INSUFFICIENT_BALANCE, HttpStatus.BAD_REQUEST,
            ErrorCode.TASK_ALREADY_COMPLETED, HttpStatus.BAD_REQUEST,
            ErrorCode.TASK_INACTIVE, HttpStatus.BAD_REQUEST,
            ErrorCode.BUSINESS_VALIDATION_ERROR, HttpStatus.BAD_REQUEST
    );

    @ExceptionHandler(MosifyException.class)
    public ResponseEntity<WebErrorResponse> handleMosifyException(MosifyException ex) {
        HttpStatus status = STATUS_MAP.getOrDefault(ex.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);

        WebErrorResponse error = new WebErrorResponse();
        error.setCode(ex.getErrorCode().name());
        error.setMessage(ex.getMessage());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<WebErrorResponse> handleAuthenticationException(org.springframework.security.core.AuthenticationException ex) {
        WebErrorResponse error = new WebErrorResponse();
        error.setCode("UNAUTHORIZED");
        error.setMessage("Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
