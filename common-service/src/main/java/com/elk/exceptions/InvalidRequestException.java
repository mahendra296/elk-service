package com.elk.exceptions;

import org.springframework.validation.ObjectError;

import java.util.List;

public class InvalidRequestException extends RuntimeException {

    public List<ObjectError> validationErrors;
    public String message;

    public InvalidRequestException(List<ObjectError> validationErrors) {
        super("Validation errors : " + validationErrors);
        this.validationErrors = validationErrors;
    }

    public List<ObjectError> getValidationError() {
        return validationErrors;
    }

    public InvalidRequestException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
