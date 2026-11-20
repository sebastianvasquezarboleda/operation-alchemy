package com.flash.alchemy.infrastructure.web.dto;

import java.util.List;

/**
 * JSON body to register, replace or update one device.
 * The name comes in the URL.
 */
public class DeviceRequest {

    private Double distance;
    private List<String> message;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }
}
