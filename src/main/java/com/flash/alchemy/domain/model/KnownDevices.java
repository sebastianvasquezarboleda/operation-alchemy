package com.flash.alchemy.domain.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class KnownDevices {

    public static final String STAR_LABS = "star_labs";
    public static final String CCPD = "ccpd";
    public static final String STAR_SATELLITE = "star_satellite";

    private static final Map<String, Position> POSITIONS = new LinkedHashMap<>();

    static {
        POSITIONS.put(STAR_LABS, new Position(-500, -200));
        POSITIONS.put(CCPD, new Position(100, -100));
        POSITIONS.put(STAR_SATELLITE, new Position(500, 100));
    }

    private KnownDevices() {
    }

    public static boolean exists(String name) {
        return POSITIONS.containsKey(name);
    }

    public static Position getPosition(String name) {
        return POSITIONS.get(name);
    }

    public static List<String> getNames() {
        return new ArrayList<>(POSITIONS.keySet());
    }
}
