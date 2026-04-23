package org.example.exception;

public class UpdatePasswordException extends RuntimeException {
    public UpdatePasswordException(String message) {
        super(message);
    }
}
