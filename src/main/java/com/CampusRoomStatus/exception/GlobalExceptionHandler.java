package com.CampusRoomStatus.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status",    errorCode.getHttpStatus().value(),
                        "code",      errorCode.name(),
                        "message",   ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status",    errorCode.getHttpStatus().value(),
                        "code",      errorCode.name(),
                        "message",   errorCode.getMessage()
                ));
    }
}