package com.flash.alchemy.domain.service;

import com.flash.alchemy.domain.exception.NotEnoughInformationException;
import com.flash.alchemy.domain.model.DecodeResult;
import com.flash.alchemy.domain.model.Device;
import com.flash.alchemy.domain.model.KnownDevices;
import com.flash.alchemy.domain.model.Position;

import java.util.ArrayList;
import java.util.List;


public class TransmissionDecoder {

    private final LocationCalculator locationCalculator;
    private final MessageDecoder messageDecoder;

    public TransmissionDecoder(LocationCalculator locationCalculator, MessageDecoder messageDecoder) {
        this.locationCalculator = locationCalculator;
        this.messageDecoder = messageDecoder;
    }

    public DecodeResult decode(List<Device> devices) {
        // We need exactly one device of each type
        List<String> names = new ArrayList<>();
        for (Device device : devices) {
            if (names.contains(device.getName())) {
                throw new NotEnoughInformationException(
                        "The device '" + device.getName() + "' is repeated");
            }
            names.add(device.getName());
        }

        List<String> missing = new ArrayList<>();
        for (String knownName : KnownDevices.getNames()) {
            if (!names.contains(knownName)) {
                missing.add(knownName);
            }
        }
        if (!missing.isEmpty()) {
            throw new NotEnoughInformationException(
                    "Not enough information. Missing devices: " + String.join(", ", missing));
        }

        Position position = locationCalculator.getLocation(devices);

        List<List<String>> messages = new ArrayList<>();
        for (Device device : devices) {
            messages.add(device.getMessage());
        }
        String message = messageDecoder.getMessage(messages);

        return new DecodeResult(position, message);
    }
}
