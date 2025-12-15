package com.example.dogtrainingtracker.repository;

import com.example.dogtrainingtracker.entities.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DogRepository extends JpaRepository <Dog, Integer>{
    List<Dog> findByOwnerId(Integer ownerId);
    Optional<Dog> findByIdAndOwnerId(Integer id, Integer ownerId);
}
