package com.example.dogtrainingtracker;

import com.example.dogtrainingtracker.controller.DogTrainingController;
import com.example.dogtrainingtracker.dto.DogTrainingResponseDTO;
import com.example.dogtrainingtracker.service.DogService;
import com.example.dogtrainingtracker.service.DogTrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(DogTrainingController.class)
class DogTrainingControllerRoleBasedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DogService dogService;

    @MockitoBean
    private DogTrainingService dogTrainingService;

    private DogTrainingResponseDTO createMockTraining(Integer id, String activity, Integer dogId) {
        // Create a mock Dog training entity
        com.example.dogtrainingtracker.entities.DogTraining mockTraining = new com.example.dogtrainingtracker.entities.DogTraining();
        mockTraining.setId(id);
        mockTraining.setActivity(activity);
        mockTraining.setLocation("Park");
        mockTraining.setTrainingDate(LocalDate.now());
        mockTraining.setDurationMinutes(30);
        mockTraining.setNotes("Good training");
        mockTraining.setCreatedAt(LocalDateTime.now());

        com.example.dogtrainingtracker.entities.Dog mockDog = new com.example.dogtrainingtracker.entities.Dog();
        mockDog.setId(dogId);
        mockTraining.setDog(mockDog);

        return new DogTrainingResponseDTO(mockTraining);
    }

    // TEST GET api/dogtraining

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllTrainings_shouldReturnAllForAdmin() throws Exception {
        List<DogTrainingResponseDTO> allTrainings = Arrays.asList(
                createMockTraining(1, "Agility", 1),
                createMockTraining(2, "Obedience", 2)
        );

        when(dogTrainingService.getAllTrainings(any())).thenReturn(allTrainings);

        mockMvc.perform(get("/api/dogtraining")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].activity").value("Agility"))
                .andExpect(jsonPath("$[1].activity").value("Obedience"));
    }

    @Test
    void getAllTrainings_unauthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/dogtraining"))
                .andExpect(status().isUnauthorized());
    }

    //TEST POST api/dogtraining

    @Test
    @WithMockUser(username = "user")
    void createTraining_shouldReturnCreatedTraining() throws Exception {
        DogTrainingResponseDTO created = createMockTraining(10, "Agility", 1);
        when(dogTrainingService.createTraining(any(), any())).thenReturn(created);

        String jsonBody = """
    {
      "dogId": 1,
      "activity": "Agility",
      "location": "Park",
      "trainingDate": "2024-10-10",
      "durationMinutes": 30,
      "notes": "Good progress"
    }
    """;

        mockMvc.perform(post("/api/dogtraining")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/dogtraining/10"))
                .andExpect(jsonPath("$.activity").value("Agility"))
                .andExpect(jsonPath("$.location").value("Park"));
    }

    @Test
    void createTraining_unauthenticated_shouldReturn401() throws Exception {
        String jsonBody = """
    {
      "dogId": 1,
      "activity": "Agility",
      "location": "Park",
      "trainingDate": "2024-10-10",
      "durationMinutes": 30,
      "notes": "Good progress"
    }
    """;

        mockMvc.perform(post("/api/dogtraining")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isUnauthorized());
    }
}