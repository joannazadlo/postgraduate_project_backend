package io.github.joannazadlo.recipedash.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

import javax.naming.SizeLimitExceededException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorDetails> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ErrorDetails.builder()
                        .status(status.value())
                        .error(status.name())
                        .errorMessage(message)
                        .timestamp(java.time.LocalDateTime.now())
                        .build());
    }

    private ResponseEntity<ErrorDetails> buildErrorResponse(BaseException baseException) {
        return ResponseEntity.status(baseException.getErrorStatus()).body(baseException.toErrorDetails());
    }

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ErrorDetails> handleBaseException(BaseException ex, HttpServletRequest request) {
        log.error("Unexpected error on {} {}: {} {}", request.getMethod(), request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
        return buildErrorResponse(ex);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorDetails> handleJsonProcessingException(JsonProcessingException ex) {
        log.error("Error: {} thrown with message {}", ex.getClass().getSimpleName(), ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.error("Error: {} thrown with message {}", ex.getClass().getSimpleName(), ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Error: {} thrown with message {}", ex.getClass().getSimpleName(), ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDenied(AccessDeniedException ex) {
        log.error("Error: {} thrown with message {}", ex.getClass().getSimpleName(), ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGeneric(Exception ex) {
        log.error("Error: {} thrown with message {}", ex.getClass().getSimpleName(), ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorDetails> handleMultipartException(MultipartException ex) {
        Throwable rootCause = ex.getCause();
        log.error("Error: {} thrown with message {}", ex.getClass().getSimpleName(), ex.getMessage());

        if (rootCause instanceof SizeLimitExceededException ||
                rootCause != null && rootCause.getMessage().contains("SizeLimitExceededException")) {
            return buildErrorResponse("Image file is too large. Maximum allowed size is 5MB.",
                    HttpStatus.PAYLOAD_TOO_LARGE
            );
        }
        return buildErrorResponse(
                "There was a problem processing your file upload.",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation failed on {} ", ex.getMessage());
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Validation failed on {} ", ex.getMessage());
        var errors = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
