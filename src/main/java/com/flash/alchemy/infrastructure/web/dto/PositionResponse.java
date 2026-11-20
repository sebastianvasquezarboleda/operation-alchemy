package com.flash.alchemy.infrastructure.web.dto;

import com.flash.alchemy.domain.model.Position;

/**
 * JSON for a position. The values are rounded to 2 decimals.
 */
public class PositionResponse {

    private final double x;
    private final double y;

    public PositionResponse(Position position) {
        this.x = Math.round(position.getX() * 100.0) / 100.0;
        this.y = Math.round(position.getY() * 100.0) / 100.0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
