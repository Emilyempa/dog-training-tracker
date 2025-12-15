package com.example.dogtrainingtracker.dto;

import com.example.dogtrainingtracker.entities.Dog;

import java.time.LocalDate;

public record DogResponseDTO(
        Integer id,
        String name,
        String breed,
        LocalDate birthdate,
        String ownerUsername
) {
    // Constructor that converts from Entity to DTO
    public DogResponseDTO(Dog entity) {
        this(
                entity.getId(),
                entity.getName(),
                entity.getBreed(),
                entity.getBirthdate(),
                entity.getOwner() != null ? entity.getOwner().getUsername() : null
        );
    }
}
