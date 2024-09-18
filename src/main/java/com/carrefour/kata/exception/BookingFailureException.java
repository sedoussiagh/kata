package com.carrefour.kata.exception;

public class BookingFailureException extends RuntimeException {
    public BookingFailureException(String message) {
        super(message);
    }
}