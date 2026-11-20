package com.flash.alchemy.infrastructure.web.dto;

/**
 * JSON returned when something goes wrong.
 */
public class ErrorResponse {

    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
