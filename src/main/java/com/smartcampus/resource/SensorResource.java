/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * Author: Akshya Prakkash Vanitha
 * Student ID: w2083025
 */
package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.CampusStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
 * SensorResource handles all HTTP requests related to sensors.
 * It supports listing sensors, retrieving one sensor, creating a sensor,
 * updating sensor status, and routing to sensor reading history.
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        CampusStore.seedDataIfEmpty();

        Map<String, Sensor> sensors = CampusStore.getSensors();

        if (type == null || type.trim().isEmpty()) {
            return new ArrayList<>(sensors.values());
        }

        List<Sensor> result = new ArrayList<>();
        String requestedType = type.trim();

        for (Sensor sensor : sensors.values()) {
            if (sensor.getType() != null
                    && sensor.getType().trim().equalsIgnoreCase(requestedType)) {
                result.add(sensor);
            }
        }

        return result;
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        CampusStore.seedDataIfEmpty();

        Sensor sensor = CampusStore.getSensors().get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found.")
                    .build();
        }

        return Response.ok(sensor).build();
    }

    @POST
    public Response createSensor(Sensor sensor) {
        CampusStore.seedDataIfEmpty();

        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sensor ID is required.")
                    .build();
        }

        if (sensor.getType() == null || sensor.getType().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sensor type is required.")
                    .build();
        }

        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sensor status is required.")
                    .build();
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room ID is required for sensor assignment.")
                    .build();
        }

        if (CampusStore.getSensors().containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A sensor with this ID already exists.")
                    .build();
        }

        Room room = CampusStore.getRooms().get(sensor.getRoomId());

        if (room == null) {
            throw new LinkedResourceNotFoundException(
                    "Cannot create sensor because the target room does not exist."
            );
        }

        CampusStore.getSensors().put(sensor.getId(), sensor);
        CampusStore.getSensorReadings().put(sensor.getId(), new ArrayList<>());

        if (!room.getSensorIds().contains(sensor.getId())) {
            room.getSensorIds().add(sensor.getId());
        }

        return Response.created(URI.create("/api/v1/sensors/" + sensor.getId()))
                .entity(sensor)
                .build();
    }

    @PUT
    @Path("/{sensorId}/status")
    public Response updateSensorStatus(@PathParam("sensorId") String sensorId, String newStatus) {
        CampusStore.seedDataIfEmpty();

        Sensor sensor = CampusStore.getSensors().get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found.")
                    .build();
        }

        if (newStatus == null || newStatus.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("New sensor status is required.")
                    .build();
        }

        String cleanedStatus = newStatus.trim();
        if (cleanedStatus.startsWith("\"") && cleanedStatus.endsWith("\"") && cleanedStatus.length() >= 2) {
            cleanedStatus = cleanedStatus.substring(1, cleanedStatus.length() - 1);
        }

        if (cleanedStatus.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("New sensor status is required.")
                    .build();
        }

        sensor.setStatus(cleanedStatus);

        return Response.ok(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}