package com.boot.dbskins.Exception;

public class SameUserInDatabaseEmail extends RuntimeException {
    public SameUserInDatabaseEmail(String message) {
        super(message);
    }
}
