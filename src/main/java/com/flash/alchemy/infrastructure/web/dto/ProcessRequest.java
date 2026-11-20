package com.flash.alchemy.infrastructure.web.dto;

import java.util.List;

/**
 * JSON body of POST /process (the three devices at once).
 */
public class ProcessRequest {

    private List<FullDeviceRequest> devices;

    public List<FullDeviceRequest> getDevices() {
        return devices;
    }

    public void setDevices(List<FullDeviceRequest> devices) {
        this.devices = devices;
    }
}
