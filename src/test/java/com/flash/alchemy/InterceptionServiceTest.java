package com.flash.alchemy;

import com.flash.alchemy.application.port.in.DeviceCommand;
import com.flash.alchemy.application.service.InterceptionService;
import com.flash.alchemy.domain.exception.DeviceAlreadyRegisteredException;
import com.flash.alchemy.domain.exception.DeviceNotFoundException;
import com.flash.alchemy.domain.exception.InvalidDataException;
import com.flash.alchemy.domain.exception.NotEnoughInformationException;
import com.flash.alchemy.domain.exception.UnknownDeviceException;
import com.flash.alchemy.domain.model.DecodeResult;
import com.flash.alchemy.domain.model.Device;
import com.flash.alchemy.domain.service.LocationCalculator;
import com.flash.alchemy.domain.service.MessageDecoder;
import com.flash.alchemy.domain.service.TransmissionDecoder;
import com.flash.alchemy.infrastructure.persistence.InMemoryDeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests of the use cases F1 to F6 (plain Java, without Spring).
 * The laboratory used in the tests is at (-100, 300).
 */
class InterceptionServiceTest {

    private static final List<String> STAR_LABS_MESSAGE =
            Arrays.asList("the", "", "is", "", "the", "", "factory");
    private static final List<String> CCPD_MESSAGE =
            Arrays.asList("", "laboratory", "", "under", "", "old", "");
    private static final List<String> SATELLITE_MESSAGE =
            Arrays.asList("", "the", "", "is", "under", "", "old", "factory");
    private static final String EXPECTED_MESSAGE = "the laboratory is under the old factory";

    private InterceptionService service;

    @BeforeEach
    void setUp() {
        TransmissionDecoder decoder = new TransmissionDecoder(new LocationCalculator(), new MessageDecoder());
        service = new InterceptionService(new InMemoryDeviceRepository(), decoder);
    }

    private DeviceCommand starLabs() {
        return new DeviceCommand("star_labs", 640.31, STAR_LABS_MESSAGE);
    }

    private DeviceCommand ccpd() {
        return new DeviceCommand("ccpd", 447.21, CCPD_MESSAGE);
    }

    private DeviceCommand satellite() {
        return new DeviceCommand("star_satellite", 632.46, SATELLITE_MESSAGE);
    }

    // ---------- Message decoder ----------

    @Test
    void messageIgnoresEmptyWordsAndLag() {
        MessageDecoder decoder = new MessageDecoder();
        String result = decoder.getMessage(Arrays.asList(
                Arrays.asList("", "this", "", "", "message"),
                Arrays.asList("this", "", "a", ""),
                Arrays.asList("", "is", "", "message")));
        assertEquals("this is a message", result);
    }

    @Test
    void messageWithMissingWordFails() {
        MessageDecoder decoder = new MessageDecoder();
        assertThrows(NotEnoughInformationException.class, () -> decoder.getMessage(Arrays.asList(
                Arrays.asList("this", "", "message"),
                Arrays.asList("this", "", ""))));
    }

    @Test
    void messageWithDifferentWordsFails() {
        MessageDecoder decoder = new MessageDecoder();
        assertThrows(NotEnoughInformationException.class, () -> decoder.getMessage(Arrays.asList(
                Arrays.asList("hello"),
                Arrays.asList("bye"))));
    }

    // ---------- F1 ----------

    @Test
    void processCompleteReturnsLocationAndMessage() {
        DecodeResult result = service.processComplete(Arrays.asList(starLabs(), ccpd(), satellite()));
        assertEquals(-100, result.getPosition().getX(), 1.0);
        assertEquals(300, result.getPosition().getY(), 1.0);
        assertEquals(EXPECTED_MESSAGE, result.getMessage());
    }

    @Test
    void processCompleteWorksInAnyOrder() {
        DecodeResult result = service.processComplete(Arrays.asList(satellite(), starLabs(), ccpd()));
        assertEquals(EXPECTED_MESSAGE, result.getMessage());
    }

    @Test
    void processCompleteWithTwoDevicesFails() {
        assertThrows(NotEnoughInformationException.class,
                () -> service.processComplete(Arrays.asList(starLabs(), ccpd())));
    }

    @Test
    void processCompleteWithRepeatedDeviceFails() {
        assertThrows(NotEnoughInformationException.class,
                () -> service.processComplete(Arrays.asList(starLabs(), starLabs(), ccpd())));
    }

    // ---------- F2 ----------

    @Test
    void registerDevice() {
        Device device = service.registerDevice(new DeviceCommand("CCPD", 447.21, CCPD_MESSAGE));
        assertEquals("ccpd", device.getName());
        assertEquals(1, service.getAllDevices().size());
    }

    @Test
    void registerTwiceFails() {
        service.registerDevice(ccpd());
        assertThrows(DeviceAlreadyRegisteredException.class, () -> service.registerDevice(ccpd()));
    }

    @Test
    void registerUnknownDeviceFails() {
        assertThrows(UnknownDeviceException.class,
                () -> service.registerDevice(new DeviceCommand("batcave", 10.0, CCPD_MESSAGE)));
    }

    @Test
    void registerNegativeDistanceFails() {
        assertThrows(InvalidDataException.class,
                () -> service.registerDevice(new DeviceCommand("ccpd", -5.0, CCPD_MESSAGE)));
    }

    @Test
    void registerWithoutMessageFails() {
        assertThrows(InvalidDataException.class,
                () -> service.registerDevice(new DeviceCommand("ccpd", 10.0, null)));
    }

    // ---------- F3 ----------

    @Test
    void processStoredWithAllDevices() {
        service.registerDevice(satellite());
        service.registerDevice(ccpd());
        service.registerDevice(starLabs());
        assertEquals(EXPECTED_MESSAGE, service.processStored().getMessage());
    }

    @Test
    void processStoredWithoutEnoughData() {
        service.registerDevice(ccpd());
        assertThrows(NotEnoughInformationException.class, () -> service.processStored());
    }

    // ---------- F4 ----------

    @Test
    void replaceDevice() {
        service.registerDevice(ccpd());
        service.registerDevice(starLabs());
        service.replaceDevice(new DeviceCommand("ccpd", 100.0, Arrays.asList("new")));

        Device ccpd = service.getDevice("ccpd");
        assertEquals(100.0, ccpd.getDistance());
        assertEquals(Arrays.asList("new"), ccpd.getMessage());
        // The other device did not change
        assertEquals(640.31, service.getDevice("star_labs").getDistance());
    }

    @Test
    void replaceNeedsAllFields() {
        service.registerDevice(ccpd());
        assertThrows(InvalidDataException.class,
                () -> service.replaceDevice(new DeviceCommand("ccpd", 100.0, null)));
    }

    @Test
    void replaceNotRegisteredFails() {
        assertThrows(DeviceNotFoundException.class, () -> service.replaceDevice(ccpd()));
    }

    // ---------- F5 ----------

    @Test
    void partialUpdateOnlyDistance() {
        service.registerDevice(ccpd());
        service.updateDevice(new DeviceCommand("ccpd", 50.0, null));
        Device ccpd = service.getDevice("ccpd");
        assertEquals(50.0, ccpd.getDistance());
        assertEquals(CCPD_MESSAGE, ccpd.getMessage());
    }

    @Test
    void partialUpdateOnlyMessage() {
        service.registerDevice(ccpd());
        service.updateDevice(new DeviceCommand("ccpd", null, Arrays.asList("hi")));
        Device ccpd = service.getDevice("ccpd");
        assertEquals(447.21, ccpd.getDistance());
        assertEquals(Arrays.asList("hi"), ccpd.getMessage());
    }

    @Test
    void invalidPartialUpdateKeepsOldData() {
        service.registerDevice(ccpd());
        assertThrows(InvalidDataException.class,
                () -> service.updateDevice(new DeviceCommand("ccpd", 50.0, Arrays.asList())));
        assertEquals(447.21, service.getDevice("ccpd").getDistance());
    }

    @Test
    void emptyPartialUpdateFails() {
        service.registerDevice(ccpd());
        assertThrows(InvalidDataException.class,
                () -> service.updateDevice(new DeviceCommand("ccpd", null, null)));
    }

    // ---------- F6 ----------

    @Test
    void deleteDevice() {
        service.registerDevice(ccpd());
        service.registerDevice(starLabs());
        service.deleteDevice("ccpd");
        assertThrows(DeviceNotFoundException.class, () -> service.getDevice("ccpd"));
        assertEquals(1, service.getAllDevices().size());
    }

    @Test
    void deleteNotRegisteredFails() {
        assertThrows(DeviceNotFoundException.class, () -> service.deleteDevice("ccpd"));
    }
}
