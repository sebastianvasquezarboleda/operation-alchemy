package com.flash.alchemy.domain.model;

import com.flash.alchemy.domain.exception.InvalidDataException;
import com.flash.alchemy.domain.exception.UnknownDeviceException;

import java.util.ArrayList;
import java.util.List;


public class Device {

    private final String name;
    private double distance;
    private List<String> message;

    public Device(String name, Double distance, List<String> message) {
        this.name = validateName(name);
        this.distance = validateDistance(distance);
        this.message = validateMessage(message);
    }



    public void replace(Double newDistance, List<String> newMessage) {
        // Validate first and change after. If something is wrong
        // the device keeps its old data (consistency).
        double validDistance = validateDistance(newDistance);
        List<String> validMessage = validateMessage(newMessage);
        this.distance = validDistance;
        this.message = validMessage;
    }


    public void partialUpdate(Double newDistance, List<String> newMessage) {
        if (newDistance == null && newMessage == null) {
            throw new InvalidDataException(
                    "You must send at least 'distance' or 'message' to update");
        }

        double validDistance = this.distance;
        List<String> validMessage = this.message;

        if (newDistance != null) {
            validDistance = validateDistance(newDistance);
        }
        if (newMessage != null) {
            validMessage = validateMessage(newMessage);
        }

        this.distance = validDistance;
        this.message = validMessage;
    }


    public Device copy() {
        return new Device(name, distance, new ArrayList<>(message));
    }

    public Position getPosition() {
        return KnownDevices.getPosition(name);
    }

    // ---------- getters ----------

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public List<String> getMessage() {
        return new ArrayList<>(message);
    }

    // ---------- validation rules ----------

    public static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("The device name is required");
        }
        String cleanName = name.trim().toLowerCase();
        if (!KnownDevices.exists(cleanName)) {
            throw new UnknownDeviceException("Unknown device '" + name
                    + "'. Valid devices: " + String.join(", ", KnownDevices.getNames()));
        }
        return cleanName;
    }

    private static double validateDistance(Double distance) {
        if (distance == null) {
            throw new InvalidDataException("The field 'distance' is required");
        }
        if (distance.isNaN() || distance.isInfinite()) {
            throw new InvalidDataException("The distance must be a valid number");
        }
        if (distance < 0) {
            throw new InvalidDataException("The distance can not be negative");
        }
        return distance;
    }

    private static List<String> validateMessage(List<String> message) {
        if (message == null) {
            throw new InvalidDataException("The field 'message' is required");
        }
        if (message.isEmpty()) {
            throw new InvalidDataException("The message can not be an empty list");
        }
        List<String> cleanMessage = new ArrayList<>();
        for (String word : message) {
            if (word == null) {
                throw new InvalidDataException("The words of the message can not be null");
            }
            cleanMessage.add(word.trim());
        }
        return cleanMessage;
    }
}
