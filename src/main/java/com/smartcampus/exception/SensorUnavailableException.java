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
 * Thrown when a sensor cannot accept readings due to its current state.
 */
public class SensorUnavailableException extends RuntimeException {

    public SensorUnavailableException(String message) {
        super(message);
    }
}