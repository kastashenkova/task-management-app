package org.example.exception;

public class EmailMessageException extends RuntimeException {
    public EmailMessageException(String message) {
        super(message);
    }
}
