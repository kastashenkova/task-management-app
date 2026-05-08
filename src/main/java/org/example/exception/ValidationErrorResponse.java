package org.example.exception;

import java.util.Map;

public record ValidationErrorResponse(long code, String message, Map<String, String> errors){
}
