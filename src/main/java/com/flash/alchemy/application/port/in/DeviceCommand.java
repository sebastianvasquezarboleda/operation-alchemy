package com.flash.alchemy.application.port.in;

import java.util.List;


public class DeviceCommand {

    private final String name;
    private final Double distance;
    private final List<String> message;

    public DeviceCommand(String name, Double distance, List<String> message) {
        this.name = name;
        this.distance = distance;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public Double getDistance() {
        return distance;
    }

    public List<String> getMessage() {
        return message;
    }
}
