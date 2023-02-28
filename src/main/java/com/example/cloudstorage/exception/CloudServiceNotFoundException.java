package com.example.cloudstorage.exception;

public class CloudServiceNotFoundException extends RuntimeException{
    public CloudServiceNotFoundException(String message) {
        super(message);
    }
}
