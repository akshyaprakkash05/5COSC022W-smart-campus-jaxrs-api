/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.CampusStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
 * RoomResource handles all HTTP requests related to rooms.
 * It supports listing rooms, retrieving one room, creating a room,
 * and deleting a room with safety validation.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public List<Room> getAllRooms() {
        CampusStore.seedDataIfEmpty();
        Map<String, Room> rooms = CampusStore.getRooms();
        return new ArrayList<>(rooms.values());
    }

    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        CampusStore.seedDataIfEmpty();

        Room room = CampusStore.getRooms().get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room not found.")
                    .build();
        }

        return Response.ok(room).build();
    }

    @POST
    public Response createRoom(Room room) {
        CampusStore.seedDataIfEmpty();

        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room ID is required.")
                    .build();
        }

        if (room.getName() == null || room.getName().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room name is required.")
                    .build();
        }

        if (CampusStore.getRooms().containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A room with this ID already exists.")
                    .build();
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        CampusStore.getRooms().put(room.getId(), room);

        return Response.created(URI.create("/api/v1/rooms/" + room.getId()))
                .entity(room)
                .build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        CampusStore.seedDataIfEmpty();

        Room room = CampusStore.getRooms().get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room not found.")
                    .build();
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                    "Room cannot be deleted because it is currently occupied by active hardware."
            );
        }

        CampusStore.getRooms().remove(roomId);

        return Response.ok("Room deleted successfully.").build();
    }
}