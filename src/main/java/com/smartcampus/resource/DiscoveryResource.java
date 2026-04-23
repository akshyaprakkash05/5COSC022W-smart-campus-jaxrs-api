/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/*
 * DiscoveryResource provides a lightweight entry point that documents
 * the main API resources available in the Smart Campus system.
 */
@Path("")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getApiInfo() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("api", "Smart Campus Sensor & Room Management API");
        response.put("version", "v1");
        response.put("contact", "admin@smartcampus.local");

        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");

        response.put("resources", resources);

        return response;
    }
}