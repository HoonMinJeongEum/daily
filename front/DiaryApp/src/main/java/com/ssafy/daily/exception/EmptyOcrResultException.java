package com.ssafy.daily.exception;

public class EmptyOcrResultException extends RuntimeException {
    public EmptyOcrResultException(String message) {
        super(message);
    }
}
