package com.example.dogtrainingtracker.controller;

import com.example.dogtrainingtracker.dto.DogResponseDTO;
import com.example.dogtrainingtracker.service.DogService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dogs")
public class DogViewController {

    private final DogService dogService;

    public DogViewController(DogService dogService) {
        this.dogService = dogService;
    }

    @GetMapping
    public String listDogs(Model model, Authentication auth) {
        List<DogResponseDTO> dogs = dogService.getAllDogs(auth);
        model.addAttribute("dogs", dogs);
        return "dogs"; // points to dogs.html in templates
    }

    @GetMapping("/{id}")
    public String dogDetails(@PathVariable Integer id, Model model, Authentication auth) {
        DogResponseDTO dog = dogService.getDogById(id, auth);
        model.addAttribute("dog", dog);
        return "dog-details"; // points at dog-details.html
    }
}

