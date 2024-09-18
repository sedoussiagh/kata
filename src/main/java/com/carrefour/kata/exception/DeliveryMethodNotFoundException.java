package com.carrefour.kata.exception;

public class DeliveryMethodNotFoundException extends RuntimeException {
    public DeliveryMethodNotFoundException(String message) {
        super(message);
    }
}