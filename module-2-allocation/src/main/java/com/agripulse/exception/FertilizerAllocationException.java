package com.agripulse.exception;

public class FertilizerAllocationException extends RuntimeException {

    public FertilizerAllocationException(String message) {
        super(message);
    }

    public FertilizerAllocationException(String message, Throwable cause) {
        super(message, cause);
    }
}