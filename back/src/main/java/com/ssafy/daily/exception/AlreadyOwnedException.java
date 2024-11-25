package com.ssafy.daily.exception;

public class AlreadyOwnedException extends RuntimeException {
    public AlreadyOwnedException(String message) {
        super(message);
    }
}

