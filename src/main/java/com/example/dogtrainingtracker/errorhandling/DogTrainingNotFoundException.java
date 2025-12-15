package com.example.dogtrainingtracker.errorhandling;

public class DogTrainingNotFoundException extends RuntimeException {
    public DogTrainingNotFoundException(Integer id) {
        super("Dog training with id " + id + " not found");
    }
}
