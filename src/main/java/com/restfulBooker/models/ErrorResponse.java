package com.restfulBooker.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {

    private String error;

    // Constructors, getters, and setters
    public ErrorResponse() {}

    public ErrorResponse(String error) {
        this.error = error;
    }

}
