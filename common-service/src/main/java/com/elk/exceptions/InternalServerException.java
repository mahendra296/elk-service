package com.elk.exceptions;

public class InternalServerException extends RuntimeException {

    public String message;

    public InternalServerException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
