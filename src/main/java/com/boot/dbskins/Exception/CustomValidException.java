package com.boot.dbskins.Exception;

public class CustomValidException extends RuntimeException {
    private String message;

    public CustomValidException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Problem with validation! Exception: " + message;
    }
}
