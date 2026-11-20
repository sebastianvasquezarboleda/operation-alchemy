package com.flash.alchemy.application.port.out;

import com.flash.alchemy.domain.model.Device;

import java.util.List;

/**
 * Output port: what the application needs to store the devices.
 * The application only knows this interface, not the real implementation.
 */
public interface DeviceRepository {

    void save(Device device);

    /** Returns the device or null if it does not exist. */
    Device findByName(String name);

    List<Device> findAll();

    void delete(String name);
}
