package com.example.dogtrainingtracker.controller;

import com.example.dogtrainingtracker.dto.DogTrainingRequestDTO;
import com.example.dogtrainingtracker.dto.DogTrainingResponseDTO;
import com.example.dogtrainingtracker.service.DogTrainingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/dogtraining")
public class DogTrainingController {

    private final DogTrainingService dogTrainingService;

    public DogTrainingController(DogTrainingService dogTrainingService) {
        this.dogTrainingService = dogTrainingService;
    }

    // Returns a list of all dog training sessions
    // Admins see all dog training sessions; users see only their own
    @GetMapping
    public List<DogTrainingResponseDTO> getAll(Authentication auth) {
        return dogTrainingService.getAllTrainings(auth);
    }

    // Returns details of a specific dog training session by its ID
    @GetMapping("/{id}")
    public DogTrainingResponseDTO getById(@PathVariable Integer id, Authentication auth) {
        return dogTrainingService.getTrainingById(id, auth);
    }

    // Creates a new dog training session
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DogTrainingResponseDTO> create(@Valid @RequestBody DogTrainingRequestDTO dto, Authentication auth) {
        DogTrainingResponseDTO response = dogTrainingService.createTraining(dto, auth);
        return ResponseEntity.created(URI.create("/api/dogtraining/" + response.id())).body(response);
    }

    // Deletes a specific dog training session by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Authentication auth) {
        dogTrainingService.deleteTraining(id, auth);
        return ResponseEntity.noContent().build();
    }
}
