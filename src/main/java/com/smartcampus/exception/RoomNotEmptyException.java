/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * Author: Akshya Prakkash Vanitha
 * Student ID: w2083025
 */
package com.smartcampus.exception;

/*
 * Thrown when a room cannot be deleted because it still has sensors assigned.
 */
public class RoomNotEmptyException extends RuntimeException {

    public RoomNotEmptyException(String message) {
        super(message);
    }
}
