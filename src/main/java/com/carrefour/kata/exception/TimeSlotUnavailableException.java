package com.carrefour.kata.exception;

public class TimeSlotUnavailableException extends RuntimeException {
    public TimeSlotUnavailableException(String message) {
        super(message);
    }
}