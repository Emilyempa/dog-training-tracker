package com.example.dogtrainingtracker.config;

import jakarta.transaction.Transactional;
import com.example.dogtrainingtracker.entities.Dog;
import com.example.dogtrainingtracker.entities.DogTraining;
import com.example.dogtrainingtracker.entities.User;
import com.example.dogtrainingtracker.repository.DogRepository;
import com.example.dogtrainingtracker.repository.DogTrainingRepository;
import com.example.dogtrainingtracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DevDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);

    private final DogRepository dogRepository;
    private final DogTrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataInitializer(DogRepository dogRepository,
                              DogTrainingRepository trainingRepository,
                              UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        this.dogRepository = dogRepository;
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean forceInit = args.containsOption("force-init");

        if (forceInit || userRepository.count() == 0) {
            log.info("Initializing dev data...");

            // Create user
            var user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("ROLE_USER");
            user.setEnabled(true);

            var admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            admin.setEnabled(true);

            userRepository.saveAll(List.of(user, admin));

            log.info("Created users: user/password (ROLE_USER) and admin/admin123 (ROLE_ADMIN)");

            // Create dogs
            var peggy = new Dog();
            peggy.setName("Peggy");
            peggy.setBreed("Bichon Frisé");
            peggy.setBirthdate(LocalDate.of(2024, 9, 14));
            peggy.setOwner(user);

            var rosa = new Dog();
            rosa.setName("Rosa");
            rosa.setBreed("Border Collie");
            rosa.setBirthdate(LocalDate.of(2019, 7, 22));
            rosa.setOwner(user);

            var charlie = new Dog();
            charlie.setName("Charlie");
            charlie.setBreed("Labrador");
            charlie.setBirthdate(LocalDate.of(2021, 11, 8));
            charlie.setOwner(admin);

            var luna = new Dog();
            luna.setName("Luna");
            luna.setBreed("German Shepherd");
            luna.setBirthdate(LocalDate.of(2022, 1, 30));
            luna.setOwner(admin);

            dogRepository.saveAll(List.of(peggy, rosa, charlie, luna));

            log.info("Saved dog Peggy with id={}, owner={}", peggy.getId(), peggy.getOwner().getUsername());
            log.info("Saved dog Rosa with id={}, owner={}", rosa.getId(), rosa.getOwner().getUsername());
            log.info("Saved dog Charlie with id={}, owner={}", charlie.getId(), charlie.getOwner().getUsername());
            log.info("Saved dog Luna with id={}, owner={}", luna.getId(), luna.getOwner().getUsername());

            // Create training for Peggy (user's dog)
            var training1 = new DogTraining();
            training1.setActivity("Lydnad");
            training1.setLocation("Hundparken");
            training1.setTrainingDate(LocalDate.now().minusDays(7));
            training1.setDurationMinutes(45);
            training1.setNotes("Gjorde stora framsteg med 'sitt' och 'ligg'");
            training1.setDog(peggy);

            var training2 = new DogTraining();
            training2.setActivity("Agility");
            training2.setLocation("Träningshall");
            training2.setTrainingDate(LocalDate.now().minusDays(3));
            training2.setDurationMinutes(60);
            training2.setNotes("Första gången över hinder, lite osäker men modig");
            training2.setDog(peggy);

            var training3 = new DogTraining();
            training3.setActivity("Spårning");
            training3.setLocation("Skogen");
            training3.setTrainingDate(LocalDate.now().minusDays(1));
            training3.setDurationMinutes(30);
            training3.setNotes("Hittade alla fyra föremål!");
            training3.setDog(peggy);

            // Create training for Rosa (user's dog)
            var training4 = new DogTraining();
            training4.setActivity("Vallning");
            training4.setLocation("Gården");
            training4.setTrainingDate(LocalDate.now().minusDays(5));
            training4.setDurationMinutes(90);
            training4.setNotes("Tränade vallning med får, naturlig talang");
            training4.setDog(rosa);

            var training5 = new DogTraining();
            training5.setActivity("Lydnad");
            training5.setLocation("Hundklubben");
            training5.setTrainingDate(LocalDate.now().minusDays(2));
            training5.setDurationMinutes(45);
            training5.setNotes("Perfekt 'kom hit' även med distraktioner");
            training5.setDog(rosa);

            // Create training for Charlie (admin's dog)
            var training6 = new DogTraining();
            training6.setActivity("Apportering");
            training6.setLocation("Stranden");
            training6.setTrainingDate(LocalDate.now().minusDays(4));
            training6.setDurationMinutes(30);
            training6.setNotes("Älskar vatten! Apporterade bollen 20 gånger");
            training6.setDog(charlie);

            var training7 = new DogTraining();
            training7.setActivity("Socialträning");
            training7.setLocation("Hundparken");
            training7.setTrainingDate(LocalDate.now());
            training7.setDurationMinutes(40);
            training7.setNotes("Lekte med 5 andra hundar, bra kroppsspråk");
            training7.setDog(charlie);

            // Create training for Luna (admin's dog)
            var training8 = new DogTraining();
            training8.setActivity("Nosarbete");
            training8.setLocation("Träningsplats");
            training8.setTrainingDate(LocalDate.now().minusDays(6));
            training8.setDurationMinutes(50);
            training8.setNotes("Hittade gömt godis på 2 minuter");
            training8.setDog(luna);

            var training9 = new DogTraining();
            training9.setActivity("Lydnad");
            training9.setLocation("Trädgården");
            training9.setTrainingDate(LocalDate.now().minusDays(1));
            training9.setDurationMinutes(25);
            training9.setNotes("Tränade 'stanna' på distans, behöver mer övning");
            training9.setDog(luna);

            trainingRepository.saveAll(List.of(
                    training1, training2, training3, training4, training5,
                    training6, training7, training8, training9
            ));

            log.info("Dev data initialized: {} users, {} dogs, {} trainings",
                    userRepository.count(),
                    dogRepository.count(),
                    trainingRepository.count());
        } else {
            log.info("Dev data already present. Skipping initialization.");
        }
    }
}
