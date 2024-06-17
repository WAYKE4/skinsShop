package com.boot.dbskins.Exception;

public class SameUserInDatabaseLogin extends RuntimeException {
    public SameUserInDatabaseLogin(String message) {
        super(message);
    }
}
