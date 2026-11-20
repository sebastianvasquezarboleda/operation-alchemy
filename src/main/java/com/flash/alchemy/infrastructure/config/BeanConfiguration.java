package com.flash.alchemy.infrastructure.config;

import com.flash.alchemy.application.port.in.InterceptionUseCase;
import com.flash.alchemy.application.port.out.DeviceRepository;
import com.flash.alchemy.application.service.InterceptionService;
import com.flash.alchemy.domain.service.LocationCalculator;
import com.flash.alchemy.domain.service.MessageDecoder;
import com.flash.alchemy.domain.service.TransmissionDecoder;
import com.flash.alchemy.infrastructure.persistence.InMemoryDeviceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BeanConfiguration {

    @Bean
    public DeviceRepository deviceRepository() {
        return new InMemoryDeviceRepository();
    }

    @Bean
    public TransmissionDecoder transmissionDecoder() {
        return new TransmissionDecoder(new LocationCalculator(), new MessageDecoder());
    }

    @Bean
    public InterceptionUseCase interceptionUseCase(DeviceRepository deviceRepository,
                                                   TransmissionDecoder transmissionDecoder) {
        return new InterceptionService(deviceRepository, transmissionDecoder);
    }
}
