package com.example.dogtrainingtracker.controller;

import com.example.dogtrainingtracker.dto.DogResponseDTO;
import com.example.dogtrainingtracker.dto.DogTrainingRequestDTO;
import com.example.dogtrainingtracker.dto.DogTrainingResponseDTO;
import com.example.dogtrainingtracker.service.DogService;
import com.example.dogtrainingtracker.service.DogTrainingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/dogs")
public class DogViewController {

    private final DogService dogService;
    private final DogTrainingService dogTrainingService;

    public DogViewController(DogService dogService, DogTrainingService dogTrainingService) {
        this.dogService = dogService;
        this.dogTrainingService = dogTrainingService;
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
        List<DogTrainingResponseDTO> trainings =
                dogTrainingService.getTrainingsByDogId(id, auth);

        model.addAttribute("dog", dog);
        model.addAttribute("trainings", trainings);
        model.addAttribute("trainingForm", new DogTrainingRequestDTO(
                "", "", LocalDate.now(), 0, "", id
        ));

        return "dog-details";
    }

    @PostMapping("/{dogId}/trainings")
    public String addTraining(
            @PathVariable Integer dogId,
            @Valid DogTrainingRequestDTO dto,
            BindingResult bindingResult,
            Model model,
            Authentication auth
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("dog", dogService.getDogById(dogId, auth));
            model.addAttribute(
                    "trainings",
                    dogTrainingService.getTrainingsByDogId(dogId, auth)
            );
            model.addAttribute("trainingForm", dto);
            return "dog-details";
        }

        DogTrainingRequestDTO request = new DogTrainingRequestDTO(
                dto.activity(),
                dto.location(),
                dto.trainingDate(),
                dto.durationMinutes(),
                dto.notes(),
                dogId
        );

        dogService.addTrainingForDog(dogId, request, auth);

        // Reload data for view
        model.addAttribute("dog", dogService.getDogById(dogId, auth));
        model.addAttribute(
                "trainings",
                dogTrainingService.getTrainingsByDogId(dogId, auth)
        );

        // Reset form
        model.addAttribute(
                "trainingForm",
                new DogTrainingRequestDTO("", "", LocalDate.now(), 0, "", dogId)
        );

        // Success feedback
        model.addAttribute("successMessage", "Träning sparad");

        return "dog-details";
    }

    @PostMapping("/{dogId}/trainings/{trainingId}/delete")
    public String deleteTraining(
            @PathVariable Integer dogId,
            @PathVariable Integer trainingId,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {
        dogTrainingService.deleteTraining(trainingId, auth);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Träning raderad"
        );

        return "redirect:/dogs/" + dogId;
    }
}

