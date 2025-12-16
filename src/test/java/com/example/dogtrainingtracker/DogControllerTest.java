package com.example.dogtrainingtracker;

import com.example.dogtrainingtracker.controller.DogController;
import com.example.dogtrainingtracker.dto.DogResponseDTO;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WebMvcTest(DogController.class)
class DogControllerRoleBasedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DogService dogService;

    @MockitoBean
    private DogTrainingService dogTrainingService;

    private DogResponseDTO createMockDog(Integer id, String name, String breed, Integer ownerId) {
        // Create a mock Dog entity to use DogResponseDTO constructor
        com.example.dogtrainingtracker.entities.Dog mockDog = new com.example.dogtrainingtracker.entities.Dog();
        mockDog.setId(id);
        mockDog.setName(name);
        mockDog.setBreed(breed);
        mockDog.setBirthdate(LocalDate.of(2020, 1, 1));

        com.example.dogtrainingtracker.entities.User mockUser = new com.example.dogtrainingtracker.entities.User();
        mockUser.setId(ownerId);
        mockUser.setUsername("user" + ownerId);
        mockDog.setOwner(mockUser);

        return new DogResponseDTO(mockDog);
    }

    // TEST GET api/dogs

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getDogs_shouldReturn4DogsForAdmin() throws Exception {
        // Arrange - Admin sees all 4 dogs
        List<DogResponseDTO> allDogs = Arrays.asList(
                createMockDog(1, "Buddy", "Golden Retriever", 1),
                createMockDog(2, "Max", "Labrador", 1),
                createMockDog(3, "Bella", "Poodle", 2),
                createMockDog(4, "Charlie", "Bulldog", 2)
        );

        when(dogService.getAllDogs(any())).thenReturn(allDogs);

        // Act & Assert
        mockMvc.perform(get("/api/dogs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].name").value("Buddy"))
                .andExpect(jsonPath("$[1].name").value("Max"))
                .andExpect(jsonPath("$[2].name").value("Bella"))
                .andExpect(jsonPath("$[3].name").value("Charlie"));
    }

    @Test
    @WithMockUser(username = "user")
    void getDogs_shouldReturn2DogsForRegularUser() throws Exception {
        // Arrange - User1 sees there 2 dogs
        List<DogResponseDTO> userDogs = Arrays.asList(
                createMockDog(1, "Buddy", "Golden Retriever", 1),
                createMockDog(2, "Max", "Labrador", 1)
        );

        when(dogService.getAllDogs(any())).thenReturn(userDogs);

        // Act & Assert
        mockMvc.perform(get("/api/dogs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Buddy"))
                .andExpect(jsonPath("$[1].name").value("Max"));
    }

    @Test
    @WithMockUser(username = "user2")
    void getDogs_shouldReturnDifferent2DogsForAnotherUser() throws Exception {
        // Arrange - User2 sees there 2 dogs
        List<DogResponseDTO> userDogs = Arrays.asList(
                createMockDog(3, "Bella", "Poodle", 2),
                createMockDog(4, "Charlie", "Bulldog", 2)
        );

        when(dogService.getAllDogs(any())).thenReturn(userDogs);

        // Act & Assert
        mockMvc.perform(get("/api/dogs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Bella"))
                .andExpect(jsonPath("$[1].name").value("Charlie"));
    }

    @Test
    void getDogs_shouldReturnUnauthorizedForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/dogs"))
                .andExpect(status().isUnauthorized());
    }

    // TEST POST api/dogs

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createDog_shouldReturnCreatedDog() throws Exception {
        DogResponseDTO createdDog = createMockDog(10, "Rocky", "Beagle", 1);
        when(dogService.createDog(any(), any())).thenReturn(createdDog);

        String jsonBody = """
        {
          "name": "Rocky",
          "breed": "Beagle",
          "birthdate": "2020-01-01"
        }
        """;

        mockMvc.perform(post("/api/dogs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/dogs/10"))
                .andExpect(jsonPath("$.name").value("Rocky"))
                .andExpect(jsonPath("$.breed").value("Beagle"));
    }

    // TEST PUT api/dogs/{id}

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateDog_shouldReturnUpdatedDog() throws Exception {
        DogResponseDTO updatedDog = createMockDog(1, "Rocky", "Cocker Spaniel", 1);

        when(dogService.updateDog(any(), any(), any())).thenReturn(updatedDog);

        String jsonBody = """
    {
      "name": "Rocky",
      "breed": "Cocker Spaniel",
      "birthdate": "2020-01-01"
    }
    """;

        mockMvc.perform(put("/api/dogs/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rocky"))
                .andExpect(jsonPath("$.breed").value("Cocker Spaniel"));
    }
}
