package com.elk.exceptions;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private HttpStatus code;

    private String key;

    private String description;

    public ApiErrorResponse(HttpStatus statusCode) {
        this.code = statusCode;
        this.key = "Unexpected error";
        this.description = "No error description provided";
    }

    public ApiErrorResponse(HttpStatus statusCode, String message, String description) {
        this.code = statusCode;
        this.key = message;
        this.description = description;
    }

    @JsonGetter("statusCode")
    public Integer getStatusCodeValue() {
        return code.value();
    }

    @JsonSetter("statusCode")
    public void setStatusCodeValue(Integer statusCodeValue) {
        code = HttpStatus.valueOf(statusCodeValue);
    }
}
