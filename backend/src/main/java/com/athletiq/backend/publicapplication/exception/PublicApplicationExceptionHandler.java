package com.athletiq.backend.publicapplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;

import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PublicApplicationExceptionHandler {

    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<Map<String, Object>>
    handleDuplicate(
            DuplicateApplicationException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of(
                                "code",
                                "DUPLICATE_APPLICATION",
                                "message",
                                exception.getMessage()
                        )
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>>
    handleBadRequest(
            IllegalArgumentException exception
    ) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "code",
                                "INVALID_APPLICATION",
                                "message",
                                exception.getMessage()
                        )
                );
    }
}