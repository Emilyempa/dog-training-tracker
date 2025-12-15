package com.example.dogtrainingtracker.errorhandling;

public class DogNotFoundException extends RuntimeException {
    public DogNotFoundException(Integer id) {
        super("Dog with id " + id + " not found");
    }
}
