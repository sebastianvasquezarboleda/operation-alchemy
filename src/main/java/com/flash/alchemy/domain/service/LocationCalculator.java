package com.flash.alchemy.domain.service;

import com.flash.alchemy.domain.exception.NotEnoughInformationException;
import com.flash.alchemy.domain.model.Device;
import com.flash.alchemy.domain.model.Position;

import java.util.List;


public class LocationCalculator {

    public Position getLocation(List<Device> devices) {
        if (devices.size() != 3) {
            throw new NotEnoughInformationException(
                    "Trilateration needs the information of the three devices");
        }

        double x1 = devices.get(0).getPosition().getX();
        double y1 = devices.get(0).getPosition().getY();
        double r1 = devices.get(0).getDistance();

        double x2 = devices.get(1).getPosition().getX();
        double y2 = devices.get(1).getPosition().getY();
        double r2 = devices.get(1).getDistance();

        double x3 = devices.get(2).getPosition().getX();
        double y3 = devices.get(2).getPosition().getY();
        double r3 = devices.get(2).getDistance();

        // Equation 1: a*x + b*y = c
        double a = 2 * (x2 - x1);
        double b = 2 * (y2 - y1);
        double c = r1 * r1 - r2 * r2 - x1 * x1 + x2 * x2 - y1 * y1 + y2 * y2;

        // Equation 2: d*x + e*y = f
        double d = 2 * (x3 - x1);
        double e = 2 * (y3 - y1);
        double f = r1 * r1 - r3 * r3 - x1 * x1 + x3 * x3 - y1 * y1 + y3 * y3;

        double determinant = a * e - b * d;
        if (Math.abs(determinant) < 0.000001) {
            throw new NotEnoughInformationException(
                    "The devices are aligned, the location can not be calculated");
        }

        double x = (c * e - b * f) / determinant;
        double y = (a * f - c * d) / determinant;
        Position result = new Position(x, y);


        for (Device device : devices) {
            double realDistance = result.distanceTo(device.getPosition());
            double tolerance = Math.max(1.0, device.getDistance() * 0.01);
            if (Math.abs(realDistance - device.getDistance()) > tolerance) {
                throw new NotEnoughInformationException(
                        "The distances are inconsistent, the location can not be determined");
            }
        }

        return result;
    }
}
