package com.learntrad.microservices.shared.exception;

import com.learntrad.microservices.shared.model.response.CommonResponse;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<List<Object>>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Invalid request body: ";
        if (ex.getCause() instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) ex.getCause();
            if (mie.getMessage().contains("No content to map")) {
                message += "Request body is empty or not valid.";
            } else {
                if (mie.getMessage().contains("Required request body is missing")) {
                    message += "Request body is missing or not valid.";
                }
            }
        } else {
            if (ex.getMessage().contains("Required request body is missing")) {
                message += "Request body is missing or not valid.";
            } else {
                String raw = ex.getMessage();
                int idx = raw.indexOf("(through reference chain:");
                if (idx > -1) raw = raw.substring(0, idx).trim();
                message += raw;
            }
        }

        return ResponseEntity.badRequest().body(
            CommonResponse.<List<Object>>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("ERROR! " + message)
                .data(Collections.emptyList())
                .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<List<Object>>> handleValidation(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        String message = (error != null) ? error.getDefaultMessage() : "Validation failed.";
        return ResponseEntity.badRequest().body(
            CommonResponse.<List<Object>>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("VALIDATION_ERROR! " + message)
                .data(Collections.emptyList())
                .build()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<List<Object>>> handleRuntime(RuntimeException ex) {
        String message = ex.getMessage();
        if (message.contains("duplicate key value violates unique constraint")) {
            if (message.contains("user_id")) {
                message = "User ID should be unique.";
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            CommonResponse.<List<Object>>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("RUNTIME_ERROR! " + message)
                .data(Collections.emptyList())
                .build()
        );
    }
}
