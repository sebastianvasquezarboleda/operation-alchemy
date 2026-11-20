package com.flash.alchemy.application.service;

import com.flash.alchemy.application.port.in.DeviceCommand;
import com.flash.alchemy.application.port.in.InterceptionUseCase;
import com.flash.alchemy.application.port.out.DeviceRepository;
import com.flash.alchemy.domain.exception.DeviceAlreadyRegisteredException;
import com.flash.alchemy.domain.exception.DeviceNotFoundException;
import com.flash.alchemy.domain.exception.InvalidDataException;
import com.flash.alchemy.domain.model.DecodeResult;
import com.flash.alchemy.domain.model.Device;
import com.flash.alchemy.domain.service.TransmissionDecoder;

import java.util.ArrayList;
import java.util.List;


public class InterceptionService implements InterceptionUseCase {

    private final DeviceRepository repository;
    private final TransmissionDecoder decoder;

    public InterceptionService(DeviceRepository repository, TransmissionDecoder decoder) {
        this.repository = repository;
        this.decoder = decoder;
    }

    // F1
    @Override
    public DecodeResult processComplete(List<DeviceCommand> commands) {
        if (commands == null) {
            throw new InvalidDataException("The field 'devices' is required");
        }
        List<Device> devices = new ArrayList<>();
        for (DeviceCommand command : commands) {
            if (command == null) {
                throw new InvalidDataException("A device can not be null");
            }
            devices.add(new Device(command.getName(), command.getDistance(), command.getMessage()));
        }
        return decoder.decode(devices);
    }

    // F2
    @Override
    public synchronized Device registerDevice(DeviceCommand command) {
        String name = Device.validateName(command.getName());
        if (repository.findByName(name) != null) {
            throw new DeviceAlreadyRegisteredException("The device '" + name
                    + "' is already registered. Use replace (PUT) or update (PATCH).");
        }
        Device device = new Device(name, command.getDistance(), command.getMessage());
        repository.save(device);
        return device;
    }

    // F3
    @Override
    public synchronized List<Device> getAllDevices() {
        return repository.findAll();
    }

    // F3
    @Override
    public synchronized Device getDevice(String name) {
        return findExisting(name);
    }

    // F3
    @Override
    public synchronized DecodeResult processStored() {
        return decoder.decode(repository.findAll());
    }

    // F4
    @Override
    public synchronized Device replaceDevice(DeviceCommand command) {
        Device device = findExisting(command.getName());
        device.replace(command.getDistance(), command.getMessage());
        repository.save(device);
        return device;
    }

    // F5
    @Override
    public synchronized Device updateDevice(DeviceCommand command) {
        Device device = findExisting(command.getName());
        device.partialUpdate(command.getDistance(), command.getMessage());
        repository.save(device);
        return device;
    }

    // F6
    @Override
    public synchronized void deleteDevice(String name) {
        Device device = findExisting(name);
        repository.delete(device.getName());
    }

    private Device findExisting(String name) {
        String cleanName = Device.validateName(name);
        Device device = repository.findByName(cleanName);
        if (device == null) {
            throw new DeviceNotFoundException(
                    "The device '" + cleanName + "' has no information registered");
        }
        return device;
    }
}
