/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * Author: Akshya Prakkash Vanitha
 * Student ID: w2083025
 */
package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.CampusStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
 * SensorReadingResource handles the reading history for one specific sensor.
 * It supports retrieving historical readings and appending new readings.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {
        CampusStore.seedDataIfEmpty();

        Sensor sensor = CampusStore.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found.")
                    .build();
        }

        List<SensorReading> readings = CampusStore.getSensorReadings().get(sensorId);
        if (readings == null) {
            readings = new ArrayList<>();
            CampusStore.getSensorReadings().put(sensorId, readings);
        }

        return Response.ok(readings).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        CampusStore.seedDataIfEmpty();

        Sensor sensor = CampusStore.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found.")
                    .build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor is currently under maintenance and cannot accept new readings."
            );
        }

        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Reading body is required.")
                    .build();
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        if (reading.getTimestamp() <= 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        List<SensorReading> readings = CampusStore.getSensorReadings().get(sensorId);
        if (readings == null) {
            readings = new ArrayList<>();
            CampusStore.getSensorReadings().put(sensorId, readings);
        }

        readings.add(reading);

        sensor.setCurrentValue(reading.getValue());

        return Response.created(URI.create("/api/v1/sensors/" + sensorId + "/readings/" + reading.getId()))
                .entity(reading)
                .build();
    }
}