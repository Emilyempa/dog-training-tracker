package com.example.dogtrainingtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record DogTrainingRequestDTO(
        @NotBlank(message = "Activity is required")
        String activity,

        @NotBlank(message = "Location is required")
        String location,

        @NotNull(message = "Training date is required")
        @PastOrPresent(message = "Training date cannot be in the future")
        LocalDate trainingDate,

        @Positive(message = "Duration must be a positive number")
        int durationMinutes,

        String notes,

        @NotNull(message = "Dog ID is required")
        Integer dogId
) {
}
