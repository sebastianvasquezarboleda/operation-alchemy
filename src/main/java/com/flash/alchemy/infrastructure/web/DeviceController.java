package com.flash.alchemy.infrastructure.web;

import com.flash.alchemy.application.port.in.DeviceCommand;
import com.flash.alchemy.application.port.in.InterceptionUseCase;
import com.flash.alchemy.domain.model.Device;
import com.flash.alchemy.infrastructure.web.dto.DeviceRequest;
import com.flash.alchemy.infrastructure.web.dto.DeviceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/devices")
public class DeviceController {

    private final InterceptionUseCase useCase;

    public DeviceController(InterceptionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public List<DeviceResponse> getAll() {
        List<DeviceResponse> response = new ArrayList<>();
        for (Device device : useCase.getAllDevices()) {
            response.add(new DeviceResponse(device));
        }
        return response;
    }

    @GetMapping("/{name}")
    public DeviceResponse getOne(@PathVariable String name) {
        return new DeviceResponse(useCase.getDevice(name));
    }

    @PostMapping("/{name}")
    public ResponseEntity<DeviceResponse> register(@PathVariable String name,
                                                   @RequestBody DeviceRequest request) {
        Device device = useCase.registerDevice(toCommand(name, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(new DeviceResponse(device));
    }

    @PutMapping("/{name}")
    public DeviceResponse replace(@PathVariable String name,
                                  @RequestBody DeviceRequest request) {
        return new DeviceResponse(useCase.replaceDevice(toCommand(name, request)));
    }

    @PatchMapping("/{name}")
    public DeviceResponse update(@PathVariable String name,
                                 @RequestBody DeviceRequest request) {
        return new DeviceResponse(useCase.updateDevice(toCommand(name, request)));
    }

    @DeleteMapping("/{name}")
    public Map<String, String> delete(@PathVariable String name) {
        useCase.deleteDevice(name);
        Map<String, String> response = new HashMap<>();
        response.put("result", "Device '" + name.toLowerCase() + "' deleted");
        return response;
    }

    private DeviceCommand toCommand(String name, DeviceRequest request) {
        return new DeviceCommand(name, request.getDistance(), request.getMessage());
    }
}
