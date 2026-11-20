package com.flash.alchemy.domain.model;


public class DecodeResult {

    private final Position position;
    private final String message;

    public DecodeResult(Position position, String message) {
        this.position = position;
        this.message = message;
    }

    public Position getPosition() {
        return position;
    }

    public String getMessage() {
        return message;
    }
}
