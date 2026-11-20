package com.flash.alchemy.infrastructure.web.dto;

import com.flash.alchemy.domain.model.Device;

import java.util.List;

/**
 * JSON with the stored information of one device.
 */
public class DeviceResponse {

    private final String name;
    private final PositionResponse position;
    private final double distance;
    private final List<String> message;

    public DeviceResponse(Device device) {
        this.name = device.getName();
        this.position = new PositionResponse(device.getPosition());
        this.distance = device.getDistance();
        this.message = device.getMessage();
    }

    public String getName() {
        return name;
    }

    public PositionResponse getPosition() {
        return position;
    }

    public double getDistance() {
        return distance;
    }

    public List<String> getMessage() {
        return message;
    }
}
