package com.flash.alchemy.infrastructure.web.dto;

import com.flash.alchemy.domain.model.DecodeResult;

/**
 * JSON with the location of the laboratory and the original message.
 */
public class DecodeResponse {

    private final PositionResponse position;
    private final String message;

    public DecodeResponse(DecodeResult result) {
        this.position = new PositionResponse(result.getPosition());
        this.message = result.getMessage();
    }

    public PositionResponse getPosition() {
        return position;
    }

    public String getMessage() {
        return message;
    }
}
