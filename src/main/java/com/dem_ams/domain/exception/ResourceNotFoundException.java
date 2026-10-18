package com.dem_ams.domain.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message, String id) {
        super(message + " " + id);
    }
}
