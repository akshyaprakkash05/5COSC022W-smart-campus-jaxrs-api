/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * CampusStore acts as the in-memory data layer for the coursework.
 * It stores rooms, sensors, and sensor reading history using Maps and Lists,
 * which matches the coursework requirement to avoid databases.
 */
public final class CampusStore {

    // Stores all rooms using room ID as the key
    private static final Map<String, Room> ROOMS = new LinkedHashMap<>();

    // Stores all sensors using sensor ID as the key
    private static final Map<String, Sensor> SENSORS = new LinkedHashMap<>();

    // Stores reading history for each sensor using sensor ID as the key
    private static final Map<String, List<SensorReading>> SENSOR_READINGS = new LinkedHashMap<>();

    // Private constructor prevents this utility class from being instantiated
    private CampusStore() {
    }

    public static Map<String, Room> getRooms() {
        return ROOMS;
    }

    public static Map<String, Sensor> getSensors() {
        return SENSORS;
    }

    public static Map<String, List<SensorReading>> getSensorReadings() {
        return SENSOR_READINGS;
    }

    /*
     * Adds starter data only once.
     * This helps us test the API quickly and demonstrate relationships
     * between rooms, sensors, and reading collections.
     */
    public static void seedDataIfEmpty() {
        if (!ROOMS.isEmpty()) {
            return;
        }

        Room r1 = new Room("LIB-301", "Library Quiet Study", 60);
        Room r2 = new Room("ENG-201", "Engineering Lab", 30);

        ROOMS.put(r1.getId(), r1);
        ROOMS.put(r2.getId(), r2);

        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 21.5, "LIB-301");
        Sensor s2 = new Sensor("CO2-001", "CO2", "ACTIVE", 480.0, "LIB-301");
        Sensor s3 = new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 0.0, "ENG-201");

        SENSORS.put(s1.getId(), s1);
        SENSORS.put(s2.getId(), s2);
        SENSORS.put(s3.getId(), s3);

        // Link sensor IDs into their parent rooms
        r1.getSensorIds().add(s1.getId());
        r1.getSensorIds().add(s2.getId());
        r2.getSensorIds().add(s3.getId());

        // Create empty reading history lists for each sensor
        SENSOR_READINGS.put(s1.getId(), new ArrayList<>());
        SENSOR_READINGS.put(s2.getId(), new ArrayList<>());
        SENSOR_READINGS.put(s3.getId(), new ArrayList<>());
    }
}
