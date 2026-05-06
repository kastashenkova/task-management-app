package org.example.exception;

import java.util.List;
import java.util.stream.Stream;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .toList();

        List<String> globalErrors = ex.getBindingResult()
                .getGlobalErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        List<String> allErrors = Stream.concat(
                        fieldErrors.stream(),
                        globalErrors.stream())
                .toList();

        return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedExceptions(AccessDeniedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<String> handleRegistrationException(RegistrationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UpdatePasswordException.class)
    public ResponseEntity<String> handleUpdatePasswordException(UpdatePasswordException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SpecificationNotFoundException.class)
    public ResponseEntity<String> handleSpecificationNotFound(SpecificationNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GoogleCalendarException.class)
    public ResponseEntity<String> handleGoogleCalendarException(GoogleCalendarException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailMessageException.class)
    public ResponseEntity<String> handleEmailMessageException(EmailMessageException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return new ResponseEntity<>("Internal server error: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
