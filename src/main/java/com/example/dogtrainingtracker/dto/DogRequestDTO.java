package com.example.dogtrainingtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record DogRequestDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Breed is required")
        String breed,

        @NotNull(message = "Birthdate is required")
        @Past(message = "Birthdate must be in the past")
        LocalDate birthdate
) {
}
