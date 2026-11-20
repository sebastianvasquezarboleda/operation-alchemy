package com.flash.alchemy.infrastructure.web;

import com.flash.alchemy.domain.exception.DeviceAlreadyRegisteredException;
import com.flash.alchemy.domain.exception.DeviceNotFoundException;
import com.flash.alchemy.domain.exception.InvalidDataException;
import com.flash.alchemy.domain.exception.NotEnoughInformationException;
import com.flash.alchemy.domain.exception.UnknownDeviceException;
import com.flash.alchemy.infrastructure.web.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(InvalidDataException error) {
        return build(HttpStatus.BAD_REQUEST, error.getMessage());
    }

    @ExceptionHandler(UnknownDeviceException.class)
    public ResponseEntity<ErrorResponse> handleUnknownDevice(UnknownDeviceException error) {
        return build(HttpStatus.BAD_REQUEST, error.getMessage());
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(DeviceNotFoundException error) {
        return build(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler(DeviceAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyRegistered(DeviceAlreadyRegisteredException error) {
        return build(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(NotEnoughInformationException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughInformation(NotEnoughInformationException error) {
        return build(HttpStatus.NOT_FOUND, error.getMessage());
    }

    // The JSON is missing, badly written or has a wrong type (for example "distance": "far")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadJson(HttpMessageNotReadableException error) {
        return build(HttpStatus.BAD_REQUEST, "The request body is missing or is not a valid JSON");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(message));
    }
}
