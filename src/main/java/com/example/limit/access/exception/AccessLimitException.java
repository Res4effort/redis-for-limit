package com.example.limit.access.exception;

public class AccessLimitException extends RuntimeException{

    public AccessLimitException(){
        super();
    }

    public AccessLimitException(String message) {
        super(message);
    }
}
