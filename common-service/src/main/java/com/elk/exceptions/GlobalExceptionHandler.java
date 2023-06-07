package com.elk.exceptions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Handling resource not found exception : ", ex);

        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.NOT_FOUND, "Resource not found.", ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InternalServerException.class)
    public final ResponseEntity<Object> handleInternalServerException(InternalServerException ex) {
        log.error("Internal server exception : ", ex);

        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.", ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public final ResponseEntity<Object> handleInvalidRequestException(InvalidRequestException ex) {
        log.error("Request not valid exception : ", ex);

        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request.", ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AuthorizationException.class)
    public final ResponseEntity<Object> handleUnAuthorizationException(AuthorizationException ex) {
        log.error("Request not valid exception : ", ex);

        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized.", ex.getMessage());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiErrorResponse apiError) {
        return new ResponseEntity<>(apiError, apiError.getCode());
    }

    private String getStackTrace(Exception ex) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] st = ex.getStackTrace();
        sb.append(ex.getClass().getName()).append(": ").append(ex.getMessage()).append("\n");
        for (StackTraceElement stackTraceElement : st) {
            sb.append("\t at ").append(stackTraceElement.toString()).append("\n");
        }
        return sb.toString();
    }
}