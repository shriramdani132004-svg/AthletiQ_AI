package com.athletiq.backend.publicapplication.exception;

public class DuplicateApplicationException
        extends RuntimeException {

    public DuplicateApplicationException(
            String message
    ) {
        super(message);
    }
}