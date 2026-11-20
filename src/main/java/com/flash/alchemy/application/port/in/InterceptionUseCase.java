package com.flash.alchemy.application.port.in;

import com.flash.alchemy.domain.model.DecodeResult;
import com.flash.alchemy.domain.model.Device;

import java.util.List;


public interface InterceptionUseCase {


    DecodeResult processComplete(List<DeviceCommand> devices);


    Device registerDevice(DeviceCommand command);


    List<Device> getAllDevices();


    Device getDevice(String name);


    DecodeResult processStored();


    Device replaceDevice(DeviceCommand command);


    Device updateDevice(DeviceCommand command);


    void deleteDevice(String name);
}
