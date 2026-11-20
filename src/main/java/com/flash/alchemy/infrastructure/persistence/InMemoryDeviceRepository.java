package com.flash.alchemy.infrastructure.persistence;

import com.flash.alchemy.application.port.out.DeviceRepository;
import com.flash.alchemy.domain.model.Device;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class InMemoryDeviceRepository implements DeviceRepository {

    private final Map<String, Device> devices = new LinkedHashMap<>();

    @Override
    public synchronized void save(Device device) {
        devices.put(device.getName(), device.copy());
    }

    @Override
    public synchronized Device findByName(String name) {
        Device device = devices.get(name);
        if (device == null) {
            return null;
        }
        return device.copy();
    }

    @Override
    public synchronized List<Device> findAll() {
        List<Device> result = new ArrayList<>();
        for (Device device : devices.values()) {
            result.add(device.copy());
        }
        return result;
    }

    @Override
    public synchronized void delete(String name) {
        devices.remove(name);
    }
}
