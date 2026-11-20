package com.flash.alchemy.infrastructure.web;

import com.flash.alchemy.application.port.in.DeviceCommand;
import com.flash.alchemy.application.port.in.InterceptionUseCase;
import com.flash.alchemy.domain.exception.InvalidDataException;
import com.flash.alchemy.domain.model.DecodeResult;
import com.flash.alchemy.infrastructure.web.dto.DecodeResponse;
import com.flash.alchemy.infrastructure.web.dto.FullDeviceRequest;
import com.flash.alchemy.infrastructure.web.dto.ProcessRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/process")
public class ProcessController {

    private final InterceptionUseCase useCase;

    public ProcessController(InterceptionUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public DecodeResponse processComplete(@RequestBody ProcessRequest request) {
        if (request.getDevices() == null) {
            throw new InvalidDataException("The field 'devices' is required");
        }

        // Convert the HTTP data (DTO) into simple application data
        List<DeviceCommand> commands = new ArrayList<>();
        for (FullDeviceRequest device : request.getDevices()) {
            if (device == null) {
                throw new InvalidDataException("A device can not be null");
            }
            commands.add(new DeviceCommand(device.getName(), device.getDistance(), device.getMessage()));
        }

        DecodeResult result = useCase.processComplete(commands);
        return new DecodeResponse(result);
    }

    @GetMapping
    public DecodeResponse processStored() {
        return new DecodeResponse(useCase.processStored());
    }
}
