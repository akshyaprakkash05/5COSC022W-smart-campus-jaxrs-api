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
 * Thrown when a request references a linked resource that does not exist.
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}