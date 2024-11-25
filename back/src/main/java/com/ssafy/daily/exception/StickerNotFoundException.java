package com.ssafy.daily.exception;

public class StickerNotFoundException extends RuntimeException {
    public StickerNotFoundException(String message) {
        super(message);
    }
}

