package com.ssafy.daily.exception;

public class WordMismatchException extends RuntimeException {
    public WordMismatchException(String message) {
        super(message);
    }
}