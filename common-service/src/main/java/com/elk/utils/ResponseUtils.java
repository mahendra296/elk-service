package com.elk.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {

    public static ResponseEntity<Object> noContentResponse() {
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    public static <E> ResponseEntity<Object> successResponseWithBody(E body) {
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(body, status);
    }
}