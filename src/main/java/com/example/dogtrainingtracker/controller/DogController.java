package com.example.dogtrainingtracker.controller;

import com.example.dogtrainingtracker.dto.DogRequestDTO;
import com.example.dogtrainingtracker.dto.DogResponseDTO;
import com.example.dogtrainingtracker.dto.DogTrainingRequestDTO;
import com.example.dogtrainingtracker.dto.DogTrainingResponseDTO;
import com.example.dogtrainingtracker.service.DogService;
import com.example.dogtrainingtracker.service.DogTrainingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/dogs")
public class DogController {

    private final DogService dogService;
    private final DogTrainingService dogTrainingService;

    public DogController(DogService dogService, DogTrainingService dogTrainingService) {
        this.dogService = dogService;
        this.dogTrainingService = dogTrainingService;
    }

    // Returns a list of all dogs accessible to the authenticated user
    // Admins see all dogs; users see only their own
    @GetMapping
    public List<DogResponseDTO> getDogs(Authentication auth) {
        return dogService.getAllDogs(auth);
    }

    // Returns details of a specific dog by its ID
    @GetMapping("/{id}")
    public DogResponseDTO getById(@PathVariable Integer id, Authentication auth) {
        return dogService.getDogById(id, auth);
    }

    // Creates a new dog (accessible to users with USER or ADMIN roles)
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DogResponseDTO> create(@Valid @RequestBody DogRequestDTO dto, Authentication auth) {
        DogResponseDTO response = dogService.createDog(dto, auth);
        return ResponseEntity.created(URI.create("/api/dogs/" + response.id())).body(response);
    }

    // Updates an existing dog entry by its ID
    @PutMapping("/{id}")
    public DogResponseDTO update(@PathVariable Integer id, @Valid @RequestBody DogRequestDTO dto, Authentication auth) {
        return dogService.updateDog(id, dto, auth);
    }

    // Deletes a dog by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Authentication auth) {
        dogService.deleteDog(id, auth);
        return ResponseEntity.noContent().build();
    }

    // Get all trainings for a specific dog or query for a specific activity
    @GetMapping("/{dogId}/trainings")
    public List<DogTrainingResponseDTO> getTrainingsForDog(
            @PathVariable Integer dogId,
            @RequestParam(required = false) String activity,
            Authentication auth) {

        if (activity != null) {
            // Filter by activity if a parameter is present
            return dogTrainingService.getTrainingsByDogIdAndActivity(dogId, activity, auth);
        }

        // Otherwise get all trainings for this dog
        return dogTrainingService.getTrainingsByDogId(dogId, auth);
    }

    // Add a new training for a specific dog
    @PostMapping("/{dogId}/trainings")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DogTrainingResponseDTO> addTrainingForDog(
            @PathVariable Integer dogId,
            @Valid @RequestBody DogTrainingRequestDTO dto,
            Authentication auth) {

        DogTrainingResponseDTO response = dogService.addTrainingForDog(dogId, dto, auth);
        return ResponseEntity.created(URI.create("/api/dogs/" + dogId + "/trainings/" + response.id())).body(response);
    }
}
