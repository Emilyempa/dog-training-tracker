package com.example.dogtrainingtracker.repository;

import com.example.dogtrainingtracker.entities.DogTraining;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DogTrainingRepository extends JpaRepository<DogTraining, Integer> {

    List<DogTraining> findByDogId(Integer dogId);
    List<DogTraining> findByDogIdAndActivity(Integer dogId, String activity);
    List<DogTraining> findByDogOwnerId(Integer ownerId);
    Optional<DogTraining> findByIdAndDogOwnerId(Integer id, Integer ownerId);
}
