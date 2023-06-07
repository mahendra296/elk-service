package com.elk.exceptions;

import lombok.Getter;

import static java.lang.String.format;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(final Class resourceClazz, final String resource) {
        super(format("Resource %s not found for %s", resourceClazz.getSimpleName(), resource));
    }

    public ResourceNotFoundException(final String resource) {
        super(format("Resource not found : %s", resource));
    }
}
