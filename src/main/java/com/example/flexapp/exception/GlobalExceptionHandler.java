package com.example.flexapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "error", "Not Found",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "error", "Bad Request",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "The requested operation violates a database integrity rule.";

        if (isDuplicateUserAndWorkDateViolation(ex)) {
            message = "A record already exists for this user and work date.";
        }

        log.warn("Database integrity violation", ex);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "error", "Conflict",
                        "message", message
                )
        );
    }

    @ExceptionHandler(com.example.flexapp.exception.AccessDeniedException.class)
    public ResponseEntity<?> handleCustomAccessDenied(com.example.flexapp.exception.AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "error", "Forbidden",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleSpringSecurityAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "error", "Forbidden",
                        "message", "You do not have permission to perform this action."
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "error", "Validation Failed",
                        "message", message
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "error", "Internal Server Error",
                        "message", "An unexpected error occurred. Please try again later."
                )
        );
    }

    private boolean isDuplicateUserAndWorkDateViolation(DataIntegrityViolationException ex) {
        Throwable current = ex;

        while (current != null) {
            String message = current.getMessage();

            if (message != null && (
                    message.contains("uk_work_schedules_user_id_work_date")
                            || message.contains("uk_time_entries_user_id_work_date")
                            || (message.contains("user_id") && message.contains("work_date") && message.contains("duplicate"))
                            || (message.contains("user_id") && message.contains("work_date") && message.contains("Unique index or primary key violation"))
                            || (message.contains("user_id") && message.contains("work_date") && message.contains("unique constraint"))
            )) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }
}